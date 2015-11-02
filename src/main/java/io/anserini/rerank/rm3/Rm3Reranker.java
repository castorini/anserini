package io.anserini.rerank.rm3;

import io.anserini.index.IndexTweets;
import io.anserini.index.IndexTweets.StatusField;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.AnalyzerUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

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
  private int fbTerms = 20;
  private int fbDocs = 50;
  private float originalQueryWeight = 0.5f;

  private RmStopper stopper = new RmStopper("");

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    Preconditions.checkState(docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    FeatureVector qfv = FeatureVector.fromTerms(
        AnalyzerUtils.tokenize(IndexTweets.ANALYZER, context.getQueryText())).normalizeToOne();

    FeatureVector rm = estimateRelevanceModel(docs, reader);
    rm = FeatureVector.interpolate(qfv, rm, originalQueryWeight);

    StringBuilder builder = new StringBuilder();
    Iterator<String> terms = rm.iterator();
    while (terms.hasNext()) {
      String term = terms.next();
      double prob = rm.getFeaturetWeight(term);
      builder.append(term + "^" + prob + " ");
    }
    String queryText = builder.toString().trim();

    QueryParser p = new QueryParser(StatusField.TEXT.name, new WhitespaceAnalyzer());
    Query nq = null;
    try {
      nq = p.parse(queryText);
    } catch (ParseException e) {
      e.printStackTrace();
      return docs;
    }

    TopDocs rs = null;
    try {
      rs = searcher.search(nq, context.getFilter(), 1000);
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
            reader.getTermVector(docs.ids[i], StatusField.TEXT.name), stopper);
        vocab.addAll(docVector.getFeatures());
        docvectors[i] = docVector;
      } catch (IOException e) {
        e.printStackTrace();
        // Just return empty feature vector.
        return f;
      }
    }

    for (String term : vocab) {
      double fbWeight = 0.0;
      for (int i = 0; i < docvectors.length; i++) {
        FeatureVector doc = docvectors[i];
        fbWeight += (doc.getFeaturetWeight(term) / doc.getVectorNorm()) * docs.scores[i];
      }
      f.addTerm(term, fbWeight);
    }

    f.pruneToSize(fbTerms);
    f.normalizeToOne();

    return f;
  }
}
