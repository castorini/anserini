package io.anserini.search.rerank.rm3;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.search.rerank.Reranker;
import io.anserini.search.rerank.RerankerContext;
import io.anserini.search.rerank.ScoredDocuments;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

public class Rm3Reranker implements Reranker {
  private int fbTerms = 20;
  private int fbDocs = 50;
  private float originalQueryWeight = 0.5f;

  private RmStopper stopper = new RmStopper("");

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    IndexSearcher searcher = context.getIndexSearcher();
    IndexReader reader = searcher.getIndexReader();

    Query originalQuery = context.getQuery();
    System.err.println("###" + originalQuery.toString().replaceAll("text:", "").replaceAll("[^a-z0-9 ]+", ""));

    FeatureVector qfv = new FeatureVector();
    for (String t : originalQuery.toString().replaceAll("text:", "").replaceAll("[^a-z0-9 ]+", "").split("\\s+")) {
      qfv.addTerm(t);
    }
    qfv.normalizeToOne();
    System.err.println(qfv);

//    FeedbackRelevanceModel fb = new FeedbackRelevanceModel();
//    //fb.setOriginalQuery(query);
//    //fb.setRes(results);
//    fb.build(docs, stopper, reader);

    FeatureVector fbVector = estimateRelevanceModel(docs, reader);
    fbVector = FeatureVector.interpolate(qfv, fbVector, originalQueryWeight);

    //System.out.println(fbVector);

    StringBuilder builder = new StringBuilder();
    Iterator<String> terms = fbVector.iterator();
    while(terms.hasNext()) {
      String term = terms.next();
      if(term.length() < 2)
        continue;
      double prob = fbVector.getFeaturetWeight(term);
      builder.append(term + "^" + prob + " ");
    }
    String queryText = builder.toString().trim();

    //System.out.println(queryText);
    QueryParser p = new QueryParser(StatusField.TEXT.name, new WhitespaceAnalyzer());
    Query nq = null;
    try {
      nq = p.parse(queryText);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return docs;
    }

    //System.out.println(nq);
    TopDocs rs = null;
    try {
      rs = searcher.search(nq, context.getFilter(), 1000);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return ScoredDocuments.fromTopDocs(rs, searcher);
//    for (int i = 0; i < docs.documents.length; i++) {
//      try {
//        System.out.println("###" + docs.ids[i]);
//        Terms terms = reader.getTermVector(docs.ids[i], StatusField.TEXT.name);
//        System.out.println(docs.ids[i] + ": " + terms.hasFreqs());
//        TermsEnum termsEnum = terms.iterator();
//
//        BytesRef text = null;
//        while ((text = termsEnum.next()) != null) {
//          String term = text.utf8ToString();
//          int freq = (int) termsEnum.totalTermFreq();
//          System.out.println(term + ": " + freq);
//        }
//        
//        System.out.println("-----");
//      } catch (IOException e) {
//        // TODO Auto-generated catch block
//        e.printStackTrace();
//      }
//    }

    //return docs;
  }

  public FeatureVector estimateRelevanceModel(ScoredDocuments docs, IndexReader reader) {
    FeatureVector f = new FeatureVector();

    try {
      Set<String> vocab = new HashSet<String>();
      List<FeatureVector> fbDocVectors = new LinkedList<FeatureVector>();

      int numdocs = docs.documents.length < fbDocs ? docs.documents.length : fbDocs;
      double[] rsvs = new double[numdocs];
      for (int k = 0; k < numdocs; k++) {
        rsvs[k] = docs.scores[k];
      }

      for (int k = 0; k < numdocs; k++) {
        FeatureVector docVector = new FeatureVector(reader.getTermVector(docs.ids[k], StatusField.TEXT.name), stopper);
        vocab.addAll(docVector.getFeatures());
        fbDocVectors.add(docVector);
      }

      Iterator<String> it = vocab.iterator();
      while (it.hasNext()) {
        String term = it.next();
        double fbWeight = 0.0;

        Iterator<FeatureVector> docIT = fbDocVectors.iterator();
        int k = 0;
        while (docIT.hasNext()) {
          FeatureVector docVector = docIT.next();
          double docProb = docVector.getFeaturetWeight(term) / docVector.getLength();
          docProb *= rsvs[k++];

          fbWeight += docProb;
        }

        fbWeight /= (double) fbDocVectors.size();
        f.addTerm(term, fbWeight);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    f.pruneToSize(fbTerms);
    f.normalizeToOne();

    return f;
  }
}
