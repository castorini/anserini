/**
 * Anserini: An information retrieval toolkit built on Lucene
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

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.FeatureVector;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;
import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_TWEETID;

public class Rm3Reranker implements Reranker {
  private static final Logger LOG = LogManager.getLogger(Rm3Reranker.class);

  private final Analyzer analyzer;
  private final String field;

  private int fbTerms = 20;
  private int fbDocs = 50;
  private float originalQueryWeight = 0.6f;

  private Stopper stopper;

  public static class Stopper {
    private Set<String> stopwords;

    public Stopper(String pathToStoplist) {
      try {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<String> lines = IOUtils.readLines(classloader.getResourceAsStream(pathToStoplist), Charset.defaultCharset());
        stopwords = new HashSet<>(lines);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public boolean isStopWord(String term) {
      return (stopwords.contains(term)) ? true : false;
    }
  }

  public Rm3Reranker(Analyzer analyzer, String field, String stoplist) {
    this.analyzer = analyzer;
    this.field = field;
    this.stopper = new Stopper(stoplist);
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    Preconditions.checkState(docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    FeatureVector qfv = FeatureVector.fromTerms(
        AnalyzerUtils.tokenize(analyzer, context.getQueryText())).scaleToUnitL1Norm();

    FeatureVector rm = estimateRelevanceModel(docs, reader);
    LOG.info("Relevance model estimated.");

    rm = FeatureVector.interpolate(qfv, rm, originalQueryWeight);

    StringBuilder builder = new StringBuilder();
    Iterator<String> terms = rm.iterator();
    while (terms.hasNext()) {
      String term = terms.next();
      double prob = rm.getFeatureWeight(term);
      builder.append(term + "^" + prob + " ");
    }
    String queryText = builder.toString().trim();

    QueryParser p = new QueryParser(field, new WhitespaceAnalyzer());
    Query feedbackQuery;
    try {
      feedbackQuery = p.parse(queryText);
    } catch (ParseException e) {
      e.printStackTrace();
      return docs;
    }

    LOG.info("Running new query: " + feedbackQuery);

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
        rs = searcher.search(finalQuery, context.getSearchArgs().hits, BREAK_SCORE_TIES_BY_TWEETID, true, true);
      } else {
        rs = searcher.search(finalQuery, context.getSearchArgs().hits, BREAK_SCORE_TIES_BY_DOCID, true, true);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return docs;
    }

    return ScoredDocuments.fromTopDocs(rs, searcher);
  }

  public FeatureVector estimateRelevanceModel(ScoredDocuments docs, IndexReader reader) {
    FeatureVector f = new FeatureVector();

    Set<String> vocab = Sets.newHashSet();
    int numdocs = docs.documents.length < fbDocs ? docs.documents.length : fbDocs;
    FeatureVector[] docvectors = new FeatureVector[numdocs];

    for (int i = 0; i < numdocs; i++) {
      try {
        FeatureVector docVector = FeatureVector.fromLuceneTermVector(
            reader.getTermVector(docs.ids[i], field), stopper);
        docVector.pruneToSize(fbTerms);

        vocab.addAll(docVector.getFeatures());
        docvectors[i] = docVector;
      } catch (IOException e) {
        e.printStackTrace();
        // Just return empty feature vector.
        return f;
      }
    }

    // Precompute the norms once and cache results.
    float[] norms = new float[docvectors.length];
    for (int i = 0; i < docvectors.length; i++) {
      norms[i] = (float) docvectors[i].computeL1Norm();
    }

    for (String term : vocab) {
      float fbWeight = 0.0f;
      for (int i = 0; i < docvectors.length; i++) {
        fbWeight += (docvectors[i].getFeatureWeight(term) / norms[i]) * docs.scores[i];
      }
      f.addFeatureWeight(term, fbWeight);
    }

    f.pruneToSize(fbTerms);
    f.scaleToUnitL1Norm();

    return f;
  }
}
