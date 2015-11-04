package io.anserini.ltr;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.ltr.feature.IntFeatureExtractors;
import io.anserini.ltr.feature.MatchingTermCount;
import io.anserini.ltr.feature.SumMatchingTf;
import io.anserini.ltr.feature.QueryFeatures;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;

import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

public class LtrDataGenerator implements Reranker {
  public LtrDataGenerator() {}

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();
    IntFeatureExtractors intFeatureExtractors = new IntFeatureExtractors();
    intFeatureExtractors.add(new MatchingTermCount()).add(new SumMatchingTf());
    intFeatureExtractors.add(new QueryFeatures());

    for (int i = 0; i < docs.documents.length; i++) {
      Terms terms = null;
      try {
        terms = reader.getTermVector(docs.ids[i], StatusField.TEXT.name);
      } catch (IOException e1) {
        continue;
      }

      System.out.print(context.getQueryId() + "\t");
      System.out.print(docs.documents[i].getField(StatusField.ID.name).stringValue() + "\t");
      System.out.print(docs.scores[i] + "\t");

      int[] intFeatures = intFeatureExtractors.extractAll(terms, context);
      System.out.print(Arrays.toString(intFeatures));

      System.out.print("\n");
    }

    return docs;
  }
}
