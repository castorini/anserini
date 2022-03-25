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
import io.anserini.index.IndexReaderUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;
import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_TWEETID;

public class RocchioReranker implements Reranker {
  private static final Logger LOG = LogManager.getLogger(RocchioReranker.class);

  private final Analyzer analyzer;
  private final String field;

  private final int fbTerms;
  private final int fbDocs;
  private final float alpha;
  private final float beta;
  private final boolean outputQuery;

  public RocchioReranker(Analyzer analyzer, String field, int fbTerms, int fbDocs, float alpha, float beta, boolean outputQuery) {
    this.analyzer = analyzer;
    this.field = field;
    this.fbTerms = fbTerms;
    this.fbDocs = fbDocs;
    this.alpha = alpha;
    this.beta = beta;
    this.outputQuery = outputQuery;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    assert(docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    FeatureVector qfv = FeatureVector.fromTerms(AnalyzerUtils.analyze(analyzer, context.getQueryText())).scaleToUnitL1Norm();

    FeatureVector rm = computeMeanOfDocumentVectors(docs, reader);

    // Rocchio weight = alpha * original query + beta * mean(top n doc embedding)
    // Here beta = 1-alpha
    rm = interpolateQueryDocVector(qfv, rm, alpha, beta);

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

  private FeatureVector computeMeanOfDocumentVectors(ScoredDocuments docs, IndexReader reader) {
    FeatureVector f = new FeatureVector();

    Set<String> vocab = new HashSet<>();
    int numdocs;
    numdocs = docs.documents.length < fbDocs ? docs.documents.length : fbDocs;

    List<FeatureVector> docvectors = new ArrayList<>();
    for (int i = 0; i < numdocs; i++) {
      try {
        FeatureVector docVector = createDocumentVector(
            reader.getTermVector(docs.ids[i], field), reader,docs.ids[i]);
        docVector.pruneToSize(fbTerms);
        vocab.addAll(docVector.getFeatures());
        docvectors.add(docVector);
      } catch (IOException e) {
        e.printStackTrace();
        // Just return empty feature vector.
        return f;
      }
    }

    // Get the mean of term weight for the Top n expansion documents
    for (String term : vocab) {
      float fbWeight = 0.0f;
      for (int i = 0; i < docvectors.size(); i++) {
        fbWeight += (docvectors.get(i).getFeatureWeight(term));
      }
      f.addFeatureWeight(term, (float) fbWeight/docvectors.size());
    }

    f.pruneToSize(fbTerms);
    f.scaleToUnitL1Norm();

    return f;
  }

  private FeatureVector createDocumentVector(Terms terms, IndexReader reader, int lucenedDocid) {
    FeatureVector f = new FeatureVector();

    try {
      TermsEnum termsEnum = terms.iterator();

      BytesRef text;
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();
        String docid = IndexReaderUtils.convertLuceneDocidToDocid(reader, lucenedDocid);
        float weight = IndexReaderUtils.getBM25AnalyzedTermWeight(reader, String.valueOf(docid), term);
        // Produce BM25 Weight for each term 
        f.addFeatureWeight(term, (float) weight);
      }
    } catch (Exception e) {
      e.printStackTrace();
      // Return empty feature vector
      return f;
    }

    return f;
  }

  private FeatureVector interpolateQueryDocVector(FeatureVector x, FeatureVector y, float xWeight, float yWeight) {
    FeatureVector z = new FeatureVector();
    Set<String> vocab = new HashSet<String>();
    vocab.addAll(x.getFeatures());
    vocab.addAll(y.getFeatures());
    Iterator<String> features = vocab.iterator();
    while (features.hasNext()) {
      String feature = features.next();
      float weight = (float) (xWeight * x.getFeatureWeight(feature) + yWeight * y.getFeatureWeight(feature));
      z.addFeatureWeight(feature, weight);
    }
    return z;
  }
  
  @Override
  public String tag() {
    return "Rocchio(fbDocs="+fbDocs+",fbTerms="+fbTerms+",alpha:"+alpha+")";
  }
}
