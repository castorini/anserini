package io.anserini.ltr;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.MatchingTermCount;
import io.anserini.ltr.feature.QueryFeatures;
import io.anserini.ltr.feature.SumMatchingTf;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

public class TweetsLtrDataGenerator implements Reranker {
  private final PrintStream out;

  public TweetsLtrDataGenerator(PrintStream out) throws FileNotFoundException {
    this.out = out;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();
    FeatureExtractors extractors = new FeatureExtractors();
    extractors.add(new MatchingTermCount());
    extractors.add(new SumMatchingTf());
    extractors.add(new QueryFeatures());

    for (int i = 0; i < docs.documents.length; i++) {
      Terms terms = null;
      try {
        terms = reader.getTermVector(docs.ids[i], StatusField.TEXT.name);
      } catch (IOException e1) {
        continue;
      }

      out.print(context.getQueryId() + "\t");
      out.print(docs.documents[i].getField(StatusField.ID.name).stringValue() + "\t");
      out.print(docs.scores[i] + "\t");

      float[] intFeatures = extractors.extractAll(terms, context);
      out.print(Arrays.toString(intFeatures));

      out.print("\n");
    }

    return docs;
  }
}
