package io.anserini.ltr;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.Qrels;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

public class TweetsLtrDataGenerator implements Reranker {
  private final PrintStream out;
  private final Qrels qrels;
  private final FeatureExtractors extractorChain;


  public TweetsLtrDataGenerator(PrintStream out, Qrels qrels, FeatureExtractors extractors) throws FileNotFoundException {
    this.out = out;
    this.qrels = qrels;
    this.extractorChain = extractors == null ? WebFeatureExtractor.getDefaultExtractors() : extractors;

  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();

    for (int i = 0; i < docs.documents.length; i++) {
      Terms terms = null;
      try {
        terms = reader.getTermVector(docs.ids[i], StatusField.TEXT.name);
      } catch (IOException e) {
        continue;
      }

      String qid = context.getQueryId().replaceFirst("^MB0*", "");
      String docid = docs.documents[i].getField(StatusField.ID.name).stringValue();

      out.print(qrels.getRelevanceGrade(qid, docid));
      out.print(" qid:" + qid);
      out.print(" 1:" + docs.scores[i]);

      float[] intFeatures = this.extractorChain.extractAll(docs.documents[i], terms, context);

      for (int j=0; j<intFeatures.length; j++ ) {
        out.print(" " + (j+2) + ":" + intFeatures[j]);
      }

      out.print(" # docid:" + docid);
      out.print("\n");
    }

    return docs;
  }
}
