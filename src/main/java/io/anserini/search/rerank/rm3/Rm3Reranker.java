package io.anserini.search.rerank.rm3;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.search.rerank.Reranker;
import io.anserini.search.rerank.RerankerContext;
import io.anserini.search.rerank.ScoredDocuments;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

public class Rm3Reranker implements Reranker {
  private Stopper stopper = new Stopper("");

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

    int fbTerms = 20;
    FeedbackRelevanceModel fb = new FeedbackRelevanceModel();
    //fb.setOriginalQuery(query);
    //fb.setRes(results);
    fb.build(docs, stopper, reader);

    FeatureVector fbVector = fb.asFeatureVector();
    fbVector.pruneToSize(fbTerms);
    fbVector.normalizeToOne();
    fbVector = FeatureVector.interpolate(qfv, fbVector, 0.5);

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
}
