package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.util.List;

/**
 * This feature computes collection query similarity, avgSCQ defined as
 * Avg( (1 + log(tf(t,D))) * idf(t)) found on page 33 of Carmel, Yom-Tov 2010
 * D is the collection term frequency
 */
public class SCQFeatureExtractor implements FeatureExtractor{
  private static final Logger LOG = LogManager.getLogger(SCQFeatureExtractor.class);

  private String lastQueryProcessed = "";
  private float lastComputedScore = 0.0f;

  // Computed as log( 1+ (N - N_t + 0.5)/(N_t + 0.5))
  private float computeIDF(long docFreq, long numDocs) {
    return (float) Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
  }

  private float sumSCQ(IndexReader reader, List<String> queryTokens,
                       String field) throws IOException {

    long numDocs = reader.numDocs() - reader.numDeletedDocs();
    float scq = 0.0f;

    for (String token : queryTokens) {
      long docFreq = reader.docFreq(new Term(field, token));
      //TODO what about tf = 0
      long termFreq = reader.totalTermFreq(new Term(field, token));
      if (termFreq == 0) continue;
      scq += 1 + Math.log(termFreq* computeIDF(docFreq, numDocs));
    }

    return scq;
  }

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();

    if (!lastQueryProcessed.equals(context.getQueryText())) {
      this.lastQueryProcessed = context.getQueryText();
      this.lastComputedScore = 0.0f;

      try {
        float sumScq = sumSCQ(reader, context.getQueryTokens(), context.getField());
        this.lastComputedScore = sumScq / context.getQueryTokens().size();
      } catch (IOException e) {
        this.lastComputedScore = 0.0f;
      }
    }

    return this.lastComputedScore;
  }

  @Override
  public String getName() {
    return "AvgSCQ";
  }
}
