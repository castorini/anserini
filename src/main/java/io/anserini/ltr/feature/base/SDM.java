package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

public class SDM implements FeatureExtractor {

  int uw_window_ = 8;
  double score_ = 0.0;
  double mu;
  double mu_phrase;
  double term_weight;
  double ordered_weight;
  double unordered_weight;

  SDM() {
    this.mu = 2500;
    this.mu_phrase = 2500;
    this.term_weight = 0.8;
    this.ordered_weight = 0.15;
    this.unordered_weight = 0.05;
  }

  SDM(double mu, double mu_phrase,
      double term_weight, double ordered_weight, double unordered_weight) {
    this.mu = mu;
    this.mu_phrase = mu_phrase;
    this.term_weight = term_weight;
    this.ordered_weight = ordered_weight;
    this.unordered_weight = unordered_weight;
  }

  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    return 0;
  }

  @Override
  public String getName() {
    return "tpDistWindow100";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new SDM();
  }
}
