package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;

import org.apache.lucene.index.Terms;

/**
 * A feature extractor that generates an integer-valued feature.
 */
public interface IntFeatureExtractor {
  int extract(Terms terms, RerankerContext context);
}
