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
import io.anserini.index.IndexArgs;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;

public class RocchioReranker implements Reranker {
  private static final Logger LOG = LogManager.getLogger(RocchioReranker.class);

  private final Analyzer analyzer;
  private final String field;

  private final int topfbTerms;
  private final int topfbDocs;
  private final int bottomfbTerms;
  private final int bottomfbDocs;
  private final float alpha;
  private final float beta;
  private final float gamma;
  private final boolean outputQuery;

  public RocchioReranker(Analyzer analyzer, String field, int topfbTerms, int topfbDocs,int bottomfbTerms, int bottomfbDocs, float alpha, float beta, float gamma, boolean outputQuery) {
    this.analyzer = analyzer;
    this.field = field;
    this.topfbTerms = topfbTerms;
    this.topfbDocs = topfbDocs;
    this.bottomfbTerms = bottomfbTerms;
    this.bottomfbDocs = bottomfbDocs;
    this.alpha = alpha;
    this.beta = beta;
    this.gamma = gamma;
    this.outputQuery = outputQuery;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    assert(docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    // The Rocchio Algorithm:
    //   q_new = alpha * q_original + beta * mean(top k document vectors)

    // Compute q_original:
    FeatureVector queryVector = FeatureVector.fromTerms(AnalyzerUtils.analyze(analyzer, context.getQueryText())).scaleToUnitL2Norm();

    // Compute mean(top k document vectors):
    FeatureVector meanRelevantDocumentVector;
    boolean relevant;
    try {
      relevant = true; 
      meanRelevantDocumentVector = computeMeanOfDocumentVectors(docs, reader, context.getSearchArgs().searchtweets, topfbTerms, topfbDocs, relevant);
    } catch (IOException e) {
      // If we run into any issues, just return the original results - as if we never performed feedback.
      e.printStackTrace();
      return docs;
    }

    // Compute mean(tail k irrelevantdocument vectors):
    FeatureVector meanIrrelevantDocumentVector;
    try {
      relevant = false; 
      meanIrrelevantDocumentVector = computeMeanOfDocumentVectors(docs, reader, context.getSearchArgs().searchtweets, bottomfbTerms, bottomfbDocs, relevant);
    } catch (IOException e) {
      // If we run into any issues, just return the original results - as if we never performed feedback.
      e.printStackTrace();
      return docs;
    }

    // Compute q_new based on alpha and beta weights:
    FeatureVector weightedVector = computeWeightedVector(queryVector, meanRelevantDocumentVector, meanIrrelevantDocumentVector, alpha, beta, gamma);

    // Use the weights as boosts to a second-round Lucene query:
    BooleanQuery.Builder feedbackQueryBuilder = new BooleanQuery.Builder();
    weightedVector.iterator().forEachRemaining(term -> {
      float boost = weightedVector.getFeatureWeight(term);
      feedbackQueryBuilder.add(new BoostQuery(new TermQuery(new Term(this.field, term)), boost), BooleanClause.Occur.SHOULD);
    });
    Query feedbackQuery = feedbackQueryBuilder.build();

    if (this.outputQuery) {
      LOG.info("QID: " + context.getQueryId());
      LOG.info("Original Query: " + context.getQuery().toString(this.field));
      LOG.info("Running new query: " + feedbackQuery.toString(this.field));
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

  private FeatureVector computeMeanOfDocumentVectors(ScoredDocuments docs, IndexReader reader, boolean tweetsearch, int fbTerms, int fbDocs, boolean relevantFlag) throws IOException {
    FeatureVector f = new FeatureVector();

    Set<String> vocab = new HashSet<>();
    int numdocs;
    FeatureVector docVector;
    numdocs = docs.documents.length < fbDocs ? docs.documents.length : fbDocs;

    List<FeatureVector> docvectors = new ArrayList<>();
    for (int i = 0; i < numdocs; i++) {
      if (relevantFlag){
        docVector = createDocumentVector(reader.getTermVector(docs.ids[i], field), reader, tweetsearch);
      }else{
        docVector = createDocumentVector(reader.getTermVector(docs.ids[docs.documents.length-i-1], field), reader, tweetsearch);
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
          termWeight += (docvectors.get(i).getFeatureWeight(term) / norms[i]);
        }
      }
      f.addFeatureWeight(term, termWeight / docvectors.size());
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
      int df = reader.docFreq(new Term(IndexArgs.CONTENTS, term));
      float ratio = (float) df / numDocs;
      if (tweetsearch) {
        if (numDocs > 100000000) {
          if (ratio > 0.007f) continue;
        } else {
          if (ratio > 0.01f) continue;
        }
      } else if (ratio > 0.1f) continue;

      f.addFeatureWeight(term, (float) termsEnum.totalTermFreq());
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
      z.addFeatureWeight(feature, alpha * a.getFeatureWeight(feature) + beta * b.getFeatureWeight(feature) - gamma *  c.getFeatureWeight(feature));
    });

    return z;
  }
  
  @Override
  public String tag() {
    return "Rocchio(topfbDocs="+topfbDocs+",topfbTerms="+topfbTerms+"bottomfbDocs="+bottomfbDocs+",bottomfbTerms="+bottomfbTerms+",alpha:"+alpha+",beta:"+beta+",gamme:"+gamma+")";
  }
}
