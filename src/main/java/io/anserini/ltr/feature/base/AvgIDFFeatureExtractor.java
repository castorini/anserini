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
 * Average IDF, idf calculated using log( 1+ (N - N_t + 0.5)/(N_t + 0.5))
 * where N is the total number of docs, calculated like in BM25
 */
public class AvgIDFFeatureExtractor implements FeatureExtractor{
  private static final Logger LOG = LogManager.getLogger(AvgIDFFeatureExtractor.class);

  private float sumIdf(IndexReader reader, List<String> queryTokens,
                       long numDocs, String field) throws IOException {
    float sumIdf = 0.0f;
    for(String token : queryTokens) {
      int docFreq = reader.docFreq(new Term(field, token));
      sumIdf += Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
    }
    return sumIdf;
  }

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();

    long numDocs = reader.numDocs() - reader.numDeletedDocs();
    try {
      float sumIdf = sumIdf(reader, context.getQueryTokens(), numDocs, context.getField());
      return sumIdf / (float) context.getQueryTokens().size();
    } catch (IOException e) {
      LOG.warn("Error computing AvgIdf, returning 0");
      return 0.0f;
    }
  }

  @Override
  public String getName() {
    return "AvgIDF";
  }
}
