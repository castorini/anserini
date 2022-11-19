/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.rerank.lib;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.index.Constants;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.FeatureVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;

public class RocchioReranker implements Reranker {
  private static final Logger LOG = LogManager.getLogger(RocchioReranker.class);

  private final Analyzer analyzer;
  private final Class parser;
  private final String field;

  private final int topFbTerms;
  private final int topFbDocs;
  private final int bottomFbTerms;
  private final int bottomFbDocs;
  private final float alpha;
  private final float beta;
  private final float gamma;
  private final boolean outputQuery;
  private final boolean useNegative;

  public RocchioReranker(Analyzer analyzer, Class parser, String field, int topFbTerms, int topFbDocs, int bottomFbTerms, int bottomFbDocs, float alpha, float beta, float gamma, boolean outputQuery, boolean useNegative) {
    this.analyzer = analyzer;
    this.parser = parser;
    this.field = field;
    this.topFbTerms = topFbTerms;
    this.topFbDocs = topFbDocs;
    this.bottomFbTerms = bottomFbTerms;
    this.bottomFbDocs = bottomFbDocs;
    this.alpha = alpha;
    this.beta = beta;
    this.gamma = gamma;
    this.outputQuery = outputQuery;
    this.useNegative = useNegative;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    assert (docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    // The Rocchio Algorithm:
    //   q_new = alpha * q_original + beta * mean(top k document vectors) - gamma * mean(tail k document vectors)

    // Compute q_original:
    FeatureVector queryVector = FeatureVector.fromTerms(AnalyzerUtils.analyze(analyzer, context.getQueryText())).scaleToUnitL2Norm();

    // Compute mean(top k relevant document vectors):
    FeatureVector meanRelevantDocumentVector;
    boolean relevantFlag;
    try {
      relevantFlag = true;
      meanRelevantDocumentVector = computeMeanOfDocumentVectors(docs, reader, context.getSearchArgs().searchtweets, topFbTerms, topFbDocs, relevantFlag);
    } catch (IOException e) {
      // If we run into any issues, just return the original results - as if we never performed feedback.
      e.printStackTrace();
      return docs;
    }

    // Compute mean(tail k nonrelevant document vectors):
    FeatureVector meanNonRelevantDocumentVector;
    if (useNegative != false) {
      try {
        relevantFlag = false;
        meanNonRelevantDocumentVector = computeMeanOfDocumentVectors(docs, reader, context.getSearchArgs().searchtweets, bottomFbTerms, bottomFbDocs, relevantFlag);
      } catch (IOException e) {
        // If we run into any issues, just return the original results - as if we never performed feedback.
        e.printStackTrace();
        return docs;
      }
    } else {
      meanNonRelevantDocumentVector = new FeatureVector();
    }

    // Compute q_new based on alpha, beta and gamma weights:
    FeatureVector weightedVector = computeWeightedVector(queryVector, meanRelevantDocumentVector, meanNonRelevantDocumentVector, alpha, beta, gamma);

    // Use the weights as boosts to a second-round Lucene query:
    Map<String, Float> feedbackTerms = new HashMap<>();
    BooleanQuery.Builder feedbackQueryBuilder = new BooleanQuery.Builder();
    weightedVector.iterator().forEachRemaining(term -> {
      float boost = weightedVector.getValue(term);
      feedbackTerms.put(term, boost);
      feedbackQueryBuilder.add(new BoostQuery(new TermQuery(new Term(this.field, term)), boost), BooleanClause.Occur.SHOULD);
    });

    Query feedbackQuery = feedbackQueryBuilder.build();
    context.feedbackTerms = feedbackTerms;


    if (this.outputQuery) {
      LOG.info("QID: " + context.getQueryId());
      LOG.info("Original Query: " + context.getQuery().toString(this.field));
      LOG.info("Feedback Query: " + feedbackQuery.toString(this.field));
      feedbackTerms.forEach((k, v) -> LOG.info("Feedback term: " + k + " -> " + v));
    }

    TopDocs results;
    try {
      Query finalQuery = feedbackQuery;
      // If there's a filter condition, we need to add in the constraint.
      // Otherwise, just use the feedback query.
      if (context.getFilter() != null) {
        BooleanQuery.Builder bqBuilder = new BooleanQuery.Builder();
        bqBuilder.add(context.getFilter(), BooleanClause.Occur.FILTER);
        bqBuilder.add(feedbackQuery, BooleanClause.Occur.MUST);
        finalQuery = bqBuilder.build();
      }

      // Figure out how to break the scoring ties.
      if (context.getSearchArgs().arbitraryScoreTieBreak) {
        results = searcher.search(finalQuery, context.getSearchArgs().hits);
      } else {
        results = searcher.search(finalQuery, context.getSearchArgs().hits, BREAK_SCORE_TIES_BY_DOCID, true);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return docs;
    }

    return ScoredDocuments.fromTopDocs(results, searcher);
  }

  private FeatureVector computeMeanOfDocumentVectors(ScoredDocuments docs, IndexReader reader, boolean tweetsearch, int fbTerms, int fbDocs, boolean relevantFlag) throws IOException, NullPointerException {
    FeatureVector f = new FeatureVector();

    Set<String> vocab = new HashSet<>();
    int numdocs;
    FeatureVector docVector;
    numdocs = docs.documents.length < fbDocs ? docs.documents.length : fbDocs;

    List<FeatureVector> docvectors = new ArrayList<>();
    for (int i = 0; i < numdocs; i++) {
      int docid;
      if (relevantFlag) {
        docid = docs.ids[i];
      } else {
        docid = docs.ids[docs.ids.length - i - 1];
      }
      Terms terms = reader.getTermVector(docid, field);
      if (terms != null) {
        docVector = createDocumentVector(terms, reader, tweetsearch);
      } else {
        if (parser == null) {
          throw new NullPointerException("Please provide an index with stored doc vectors or input -collection param");
        }
        Map<String, Long> termFreqMap = AnalyzerUtils.computeDocumentVector(analyzer, parser,
            reader.document(docid).getField(Constants.RAW).stringValue());
        docVector = createDocumentVectorOnTheFly(termFreqMap, reader, tweetsearch);
      }
      vocab.addAll(docVector.getFeatures());
      docvectors.add(docVector);
    }

    // Precompute the norms once and cache results.
    float[] norms = new float[docvectors.size()];
    for (int i = 0; i < docvectors.size(); i++) {
      norms[i] = (float) docvectors.get(i).computeL2Norm();
    }

    // Get the mean of term weight for the Top n expansion documents
    for (String term : vocab) {
      float termWeight = 0.0f;
      for (int i = 0; i < docvectors.size(); i++) {
        // Avoids zero-length feedback documents, which causes division by zero when computing term weights.
        // Zero-length feedback documents occur (e.g., with CAR17) when a document has only terms
        // that contain accents (which are indexed, but not selected for feedback).
        if (norms[i] > 0.001f) {
          termWeight += (docvectors.get(i).getValue(term) / norms[i]);
        }
      }
      f.addFeatureValue(term, termWeight / docvectors.size());
    }

    f.pruneToSize(fbTerms);
    f.scaleToUnitL2Norm();

    return f;
  }

  private FeatureVector createDocumentVector(Terms terms, IndexReader reader, boolean tweetsearch) throws IOException {
    FeatureVector f = new FeatureVector();
    BytesRef text;

    int numDocs = reader.numDocs();
    TermsEnum termsEnum = terms.iterator();
    while ((text = termsEnum.next()) != null) {
      String term = text.utf8ToString();

      // We're using similar heuristics as in the RM3 implementation. See comments there.
      if (term.length() < 2 || term.length() > 20) continue;
      int df = reader.docFreq(new Term(Constants.CONTENTS, term));
      float ratio = (float) df / numDocs;
      if (tweetsearch) {
        if (numDocs > 100000000) {
          if (ratio > 0.007f) continue;
        } else {
          if (ratio > 0.01f) continue;
        }
      } else if (ratio > 0.1f) continue;

      f.addFeatureValue(term, (float) termsEnum.totalTermFreq());
    }

    return f;
  }

  private FeatureVector createDocumentVectorOnTheFly(Map<String, Long> terms, IndexReader reader, boolean tweetsearch) throws IOException {
    FeatureVector f = new FeatureVector();

    int numDocs = reader.numDocs();
    for (String term : terms.keySet()) {
      // We're using similar heuristics as in the RM3 implementation. See comments there.
      if (term.length() < 2 || term.length() > 20) continue;
      int df = reader.docFreq(new Term(Constants.CONTENTS, term));
      float ratio = (float) df / numDocs;
      if (tweetsearch) {
        if (numDocs > 100000000) {
          if (ratio > 0.007f) continue;
        } else {
          if (ratio > 0.01f) continue;
        }
      } else if (ratio > 0.1f) continue;

      f.addFeatureValue(term, (float) terms.get(term));
    }

    return f;
  }

  private FeatureVector computeWeightedVector(FeatureVector a, FeatureVector b, FeatureVector c, float alpha, float beta, float gamma) {
    FeatureVector z = new FeatureVector();
    Set<String> vocab = new HashSet<>();
    vocab.addAll(a.getFeatures());
    vocab.addAll(b.getFeatures());
    vocab.addAll(c.getFeatures());

    vocab.iterator().forEachRemaining(feature -> {
      float weighted_score = alpha * a.getValue(feature) + beta * b.getValue(feature) - gamma * c.getValue(feature);
      if (weighted_score > 0) {
        z.addFeatureValue(feature, weighted_score);
      }
    });

    return z;
  }

  @Override
  public String tag() {
    return "Rocchio(topFbDocs=" + topFbDocs + ",topFbTerms=" + topFbTerms + "bottomFbDocs=" + bottomFbDocs + ",bottomFbTerms=" + bottomFbTerms + ",alpha:" + alpha + ",beta:" + beta + ",gamma:" + gamma + ")";
  }
}
