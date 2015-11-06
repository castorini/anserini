package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

/**
 * A feature extractor.
 */
public interface FeatureExtractor {
  float extract(Document doc, Terms terms, RerankerContext context);
}
