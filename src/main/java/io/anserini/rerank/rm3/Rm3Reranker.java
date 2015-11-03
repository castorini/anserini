package io.anserini.rerank.rm3;

import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.FeatureVector;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class Rm3Reranker implements Reranker {
  private static final Logger LOG = LogManager.getLogger(Rm3Reranker.class);

  private final Analyzer analyzer;
  private final String field;

  private int fbTerms = 20;
  private int fbDocs = 50;
  private float originalQueryWeight = 0.5f;

  private Rm3Stopper stopper = new Rm3Stopper("");

  public Rm3Reranker(Analyzer analyzer, String field) {
    this.analyzer = analyzer;
    this.field = field;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    Preconditions.checkState(docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    FeatureVector qfv = FeatureVector.fromTerms(
        AnalyzerUtils.tokenize(analyzer, context.getQueryText())).scaleToUnitL2Norm();

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
    Query nq = null;
    try {
      nq = p.parse(queryText);
    } catch (ParseException e) {
      e.printStackTrace();
      return docs;
    }

    LOG.info("Running new query: " + nq);

    TopDocs rs = null;
    try {
      if (context.getFilter() == null) {
        rs = searcher.search(nq, 1000);
      } else {
        rs = searcher.search(nq, context.getFilter(), 1000);
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
      norms[i] = (float) docvectors[i].computeL2Norm();
    }

    for (String term : vocab) {
      float fbWeight = 0.0f;
      for (int i = 0; i < docvectors.length; i++) {
        fbWeight += (docvectors[i].getFeatureWeight(term) / norms[i]) * docs.scores[i];
      }
      f.addFeatureWeight(term, fbWeight);
    }

    f.pruneToSize(fbTerms);
    f.scaleToUnitL2Norm();

    return f;
  }
}
