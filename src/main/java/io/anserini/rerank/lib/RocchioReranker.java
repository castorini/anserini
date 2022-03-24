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
import java.util.Iterator;
import java.util.List;

import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;
import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_TWEETID;

public class RocchioReranker implements Reranker {
  private static final Logger LOG = LogManager.getLogger(RocchioReranker.class);

  private final Analyzer analyzer;
  private final String field;

  private final int fbTerms;
  private final int fbDocs;
  private final float originalQueryWeight;
  private final boolean outputQuery;
  private final boolean filterTerms;

  public RocchioReranker(Analyzer analyzer, String field, int fbTerms, int fbDocs, float originalQueryWeight, boolean outputQuery, boolean filterTerms) {
    this.analyzer = analyzer;
    this.field = field;
    this.fbTerms = fbTerms;
    this.fbDocs = fbDocs;
    this.originalQueryWeight = originalQueryWeight;
    this.outputQuery = outputQuery;
    this.filterTerms = filterTerms;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    assert(docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    FeatureVector qfv = FeatureVector.fromTerms(AnalyzerUtils.analyze(analyzer, context.getQueryText())).scaleToUnitL1Norm();

    boolean useRf = (context.getSearchArgs().rf_qrels != null);
    FeatureVector rm = estimateRelevanceModel(docs, reader, context.getSearchArgs().searchtweets, useRf);

    rm = FeatureVector.interpolate(qfv, rm, originalQueryWeight);

    BooleanQuery.Builder feedbackQueryBuilder = new BooleanQuery.Builder();

    Iterator<String> terms = rm.iterator();
    while (terms.hasNext()) {
      String term = terms.next();
      float prob = rm.getFeatureWeight(term);
      feedbackQueryBuilder.add(new BoostQuery(new TermQuery(new Term(this.field, term)), prob), BooleanClause.Occur.SHOULD);
    }

    Query feedbackQuery = feedbackQueryBuilder.build();

    if (this.outputQuery) {
      LOG.info("QID: " + context.getQueryId());
      LOG.info("Original Query: " + context.getQuery().toString(this.field));
      LOG.info("Running new query: " + feedbackQuery.toString(this.field));
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

    int numdocs;
    if (useRf) {
      numdocs = docs.documents.length;
    }
    else {
      numdocs = docs.documents.length < fbDocs ? docs.documents.length : fbDocs;
    }

    List<FeatureVector> docvectors = new ArrayList<>();
    for (int i = 0; i < numdocs; i++) {
      if (useRf && docs.scores[i] <= .0) {
        continue;
      }
      try {
        FeatureVector docVector = createdFeatureVector(
            reader.getTermVector(docs.ids[i], field), reader, tweetsearch, fbDocs);
        docVector.pruneToSize(fbTerms);
        docvectors.add(docVector);
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

    for (int i = 0; i < docvectors.size(); i++) {
      float fbWeight = 0.0f;
      for (String term : docvectors.get(i).getFeatures()) {
        // Avoids zero-length feedback documents, which causes division by zero when computing term weights.
        // Zero-length feedback documents occur (e.g., with CAR17) when a document has only terms 
        // that accents (which are indexed, but not selected for feedback).
        if (norms[i] > 0.001f) {
          fbWeight = (docvectors.get(i).getFeatureWeight(term) / norms[i] );
        }
        f.addFeatureWeight(term, fbWeight);
      }
    }

    f.pruneToSize(fbTerms);
    f.scaleToUnitL1Norm();

    return f;
  }

  private FeatureVector createdFeatureVector(Terms terms, IndexReader reader, boolean tweetsearch, int fbDocs) {
    FeatureVector f = new FeatureVector();

    try {
      TermsEnum termsEnum = terms.iterator();

      BytesRef text;
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();

        if (term.length() < 2 || term.length() > 20) continue;
        if (this.filterTerms && !term.matches("[a-z0-9]+")) continue;

        int freq = (int) termsEnum.totalTermFreq();
        f.addFeatureWeight(term, (float) freq / fbDocs);
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
    return "Rocchio(fbDocs="+fbDocs+",fbTerms="+fbTerms+",originalQueryWeight:"+originalQueryWeight+")";
  }
}
