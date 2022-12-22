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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;
import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_TWEETID;

public class Rm3Reranker implements Reranker {
  private static final Logger LOG = LogManager.getLogger(Rm3Reranker.class);

  private final Analyzer analyzer;
  private final Class parser;
  private final String field;

  private final int fbTerms;
  private final int fbDocs;
  private final float originalQueryWeight;
  private final boolean outputQuery;
  private final boolean filterTerms;

  public Rm3Reranker(Analyzer analyzer, Class parser, String field, int fbTerms, int fbDocs, float originalQueryWeight, boolean outputQuery, boolean filterTerms) {
    this.analyzer = analyzer;
    this.parser = parser;
    this.field = field;
    this.fbTerms = fbTerms;
    this.fbDocs = fbDocs;
    this.originalQueryWeight = originalQueryWeight;
    this.outputQuery = outputQuery;
    this.filterTerms = filterTerms;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    assert (docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    FeatureVector qfv = FeatureVector.fromTerms(context.getQueryTokens()).scaleToUnitL1Norm();

    boolean useRf = (context.getSearchArgs().rf_qrels != null);
    FeatureVector rm = estimateRelevanceModel(docs, reader, context.getSearchArgs().searchtweets, useRf);

    rm = FeatureVector.interpolate(qfv, rm, originalQueryWeight);

    BooleanQuery.Builder feedbackQueryBuilder = new BooleanQuery.Builder();
    Map<String, Float> feedbackTerms = new HashMap<>();
    Iterator<String> terms = rm.iterator();
    while (terms.hasNext()) {
      String term = terms.next();
      float prob = rm.getValue(term);

      feedbackTerms.put(term, prob);
      feedbackQueryBuilder.add(new BoostQuery(new TermQuery(new Term(this.field, term)), prob), BooleanClause.Occur.SHOULD);
    }

    Query feedbackQuery = feedbackQueryBuilder.build();
    context.feedbackTerms = feedbackTerms;

    if (this.outputQuery) {
      LOG.info("QID: " + context.getQueryId());
      LOG.info("Original Query: " + context.getQuery().toString(this.field));
      LOG.info("Feedback Query: " + feedbackQuery.toString(this.field));
      feedbackTerms.forEach((k, v) -> LOG.info("Feedback term: " + k + " -> " + v));
    }

    TopDocs rs;
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
        rs = searcher.search(finalQuery, context.getSearchArgs().hits);
      } else if (context.getSearchArgs().searchtweets) {
        rs = searcher.search(finalQuery, context.getSearchArgs().hits, BREAK_SCORE_TIES_BY_TWEETID, true);
      } else {
        rs = searcher.search(finalQuery, context.getSearchArgs().hits, BREAK_SCORE_TIES_BY_DOCID, true);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return docs;
    }

    return ScoredDocuments.fromTopDocs(rs, searcher);
  }

  private FeatureVector estimateRelevanceModel(ScoredDocuments docs, IndexReader reader, boolean tweetsearch, boolean useRf) {
    FeatureVector f = new FeatureVector();

    Set<String> vocab = new HashSet<>();
    int numdocs;
    if (useRf) {
      numdocs = docs.documents.length;
    } else {
      numdocs = docs.documents.length < fbDocs ? docs.documents.length : fbDocs;
    }

    List<FeatureVector> docvectors = new ArrayList<>();
    List<Float> docScores = new ArrayList<>();
    for (int i = 0; i < numdocs; i++) {
      if (useRf && docs.scores[i] <= .0) {
        continue;
      }
      try {
        FeatureVector docVector;
        Terms terms = reader.getTermVector(docs.ids[i], field);
        if (terms != null) {
          docVector = createdFeatureVector(terms, reader, tweetsearch);
        } else {
          if (parser == null) {
            throw new NullPointerException("Please provide an index with stored doc vectors or input -collection param");
          }
          Map<String, Long> termFreqMap = AnalyzerUtils.computeDocumentVector(analyzer, parser,
              reader.document(docs.ids[i]).getField(Constants.RAW).stringValue());
          docVector = createdFeatureVectorOnTheFly(termFreqMap, reader, tweetsearch);
        }

        docVector.pruneToSize(fbTerms);
        vocab.addAll(docVector.getFeatures());
        docvectors.add(docVector);
        docScores.add(Float.valueOf(docs.scores[i]));
      } catch (IOException e) {
        e.printStackTrace();
        // Just return empty feature vector.
        return f;
      }
    }

    // Precompute the norms once and cache results.
    float[] norms = new float[docvectors.size()];
    for (int i = 0; i < docvectors.size(); i++) {
      norms[i] = (float) docvectors.get(i).computeL1Norm();
    }

    for (String term : vocab) {
      float fbWeight = 0.0f;
      for (int i = 0; i < docvectors.size(); i++) {
        // Avoids zero-length feedback documents, which causes division by zero when computing term weights.
        // Zero-length feedback documents occur (e.g., with CAR17) when a document has only terms 
        // that contain accents (which are indexed, but not selected for feedback).
        if (norms[i] > 0.001f) {
          fbWeight += (docvectors.get(i).getValue(term) / norms[i]) * docScores.get(i);
        }
      }
      f.addFeatureValue(term, fbWeight);
    }

    f.pruneToSize(fbTerms);
    f.scaleToUnitL1Norm();

    return f;
  }

  private FeatureVector createdFeatureVector(Terms terms, IndexReader reader, boolean tweetsearch) {
    FeatureVector f = new FeatureVector();

    try {
      int numDocs = reader.numDocs();
      TermsEnum termsEnum = terms.iterator();

      BytesRef text;
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();

        if (term.length() < 2 || term.length() > 20) continue;
        if (this.filterTerms && !term.matches("[a-z0-9]+")) continue;

        // This seemingly arbitrary logic needs some explanation. See following PR for details:
        //   https://github.com/castorini/Anserini/pull/289
        //
        // We have long known that stopwords have a big impact in RM3. If we include stopwords
        // in feedback, effectiveness is affected negatively. In the previous implementation, we
        // built custom stopwords lists by selecting top k terms from the collection. We only
        // had two stopwords lists, for gov2 and for Twitter. The gov2 list is used on all
        // collections other than Twitter.
        //
        // The logic below instead uses a df threshold: If a term appears in more than n percent
        // of the documents, then it is discarded as a feedback term. This heuristic has the
        // advantage of getting rid of collection-specific stopwords lists, but at the cost of
        // introducing an additional tuning parameter.
        //
        // Cognizant of the dangers of (essentially) tuning on test data, here's what I
        // (@lintool) did:
        //
        // + For newswire collections, I picked a number, 10%, that seemed right. This value
        //   actually increased effectiveness in most conditions across all newswire collections.
        //
        // + This 10% value worked fine on web collections; effectiveness didn't change much.
        //
        // Since this was the first and only heuristic value I selected, we're not really tuning
        // parameters.
        //
        // The 10% threshold, however, doesn't work well on tweets because tweets are much
        // shorter. Based on a list terms in the collection by df: For the Tweets2011 collection,
        // I found a threshold close to a nice round number that approximated the length of the
        // current stopwords list, by eyeballing the df values. This turned out to be 1%. I did
        // this again for the Tweets2013 collection, using the same approach, and obtained a value
        // of 0.7%.
        //
        // With both values, we obtained effectiveness pretty close to the old values with the
        // custom stopwords list.
        int df = reader.docFreq(new Term(Constants.CONTENTS, term));
        float ratio = (float) df / numDocs;
        if (tweetsearch) {
          if (numDocs > 100000000) { // Probably Tweets2013
            if (ratio > 0.007f) continue;
          } else {
            if (ratio > 0.01f) continue;
          }
        } else if (ratio > 0.1f) continue;

        int freq = (int) termsEnum.totalTermFreq();
        f.addFeatureValue(term, (float) freq);
      }
    } catch (Exception e) {
      e.printStackTrace();
      // Return empty feature vector
      return f;
    }

    return f;
  }

  private FeatureVector createdFeatureVectorOnTheFly(Map<String, Long> terms, IndexReader reader, boolean tweetsearch) {
    FeatureVector f = new FeatureVector();

    try {
      int numDocs = reader.numDocs();
      for (String term : terms.keySet()) {
        if (term.length() < 2 || term.length() > 20) continue;
        if (this.filterTerms && !term.matches("[a-z0-9]+")) continue;

        // This seemingly arbitrary logic needs some explanation. See following PR for details:
        //   https://github.com/castorini/Anserini/pull/289
        //
        // We have long known that stopwords have a big impact in RM3. If we include stopwords
        // in feedback, effectiveness is affected negatively. In the previous implementation, we
        // built custom stopwords lists by selecting top k terms from the collection. We only
        // had two stopwords lists, for gov2 and for Twitter. The gov2 list is used on all
        // collections other than Twitter.
        //
        // The logic below instead uses a df threshold: If a term appears in more than n percent
        // of the documents, then it is discarded as a feedback term. This heuristic has the
        // advantage of getting rid of collection-specific stopwords lists, but at the cost of
        // introducing an additional tuning parameter.
        //
        // Cognizant of the dangers of (essentially) tuning on test data, here's what I
        // (@lintool) did:
        //
        // + For newswire collections, I picked a number, 10%, that seemed right. This value
        //   actually increased effectiveness in most conditions across all newswire collections.
        //
        // + This 10% value worked fine on web collections; effectiveness didn't change much.
        //
        // Since this was the first and only heuristic value I selected, we're not really tuning
        // parameters.
        //
        // The 10% threshold, however, doesn't work well on tweets because tweets are much
        // shorter. Based on a list terms in the collection by df: For the Tweets2011 collection,
        // I found a threshold close to a nice round number that approximated the length of the
        // current stopwords list, by eyeballing the df values. This turned out to be 1%. I did
        // this again for the Tweets2013 collection, using the same approach, and obtained a value
        // of 0.7%.
        //
        // With both values, we obtained effectiveness pretty close to the old values with the
        // custom stopwords list.
        int df = reader.docFreq(new Term(Constants.CONTENTS, term));
        float ratio = (float) df / numDocs;
        if (tweetsearch) {
          if (numDocs > 100000000) { // Probably Tweets2013
            if (ratio > 0.007f) continue;
          } else {
            if (ratio > 0.01f) continue;
          }
        } else if (ratio > 0.1f) continue;
        f.addFeatureValue(term, (float) terms.get(term));
      }
    } catch (Exception e) {
      e.printStackTrace();
      // Return empty feature vector
      return f;
    }

    return f;
  }

  @Override
  public String tag() {
    return "Rm3(fbDocs=" + fbDocs + ",fbTerms=" + fbTerms + ",originalQueryWeight:" + originalQueryWeight + ")";
  }
}
