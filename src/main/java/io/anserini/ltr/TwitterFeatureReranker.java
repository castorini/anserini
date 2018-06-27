package io.anserini.ltr;

import io.anserini.index.generator.TweetGenerator;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.Qrels;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Used to rerank according to features
 *
 */
public class TwitterFeatureReranker implements Reranker<Integer> {
  private final PrintStream out;
  private final Qrels qrels;
  private final FeatureExtractors extractors;

  public TwitterFeatureReranker(PrintStream out, Qrels qrels, FeatureExtractors extractors) {
    this.out = out;
    this.qrels = qrels;
    this.extractors = extractors == null ? TwitterFeatureExtractor.getDefaultExtractors() : extractors;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext<Integer> context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();

    for (int i = 0; i < docs.documents.length; i++) {
      Terms terms = null;
      try {
        terms = reader.getTermVector(docs.ids[i], TweetGenerator.FIELD_BODY);
      } catch (IOException e) {
        continue;
      }

      int qid = context.getQueryId();
      String docid = docs.documents[i].getField( TweetGenerator.FIELD_ID).stringValue();

      out.print(qrels.getRelevanceGrade(qid, docid));
      out.print(" qid:" + qid);

      float[] intFeatures = this.extractors.extractAll(docs.documents[i], terms, context);

      // TODO use model to rerank
    }

    return docs;
  }
}
