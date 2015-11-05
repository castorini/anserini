package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;

import java.util.List;

import org.apache.lucene.index.Terms;

import com.google.common.collect.Lists;

/**
 * A collection of {@link FeatureExtractor}s.
 */
public class FeatureExtractors {
  public List<FeatureExtractor> extractors = Lists.newArrayList();

  public FeatureExtractors() {}

  public FeatureExtractors add(FeatureExtractor extractor) {
    extractors.add(extractor);
    return this;
  }

  public float[] extractAll(Terms terms, RerankerContext context) {
    float[] features = new float[extractors.size()];

    for (int i=0; i<extractors.size(); i++) {
      features[i] = extractors.get(i).extract(terms, context);
    }

    return features;
  }
}
