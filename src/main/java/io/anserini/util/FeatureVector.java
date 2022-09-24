/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.util;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FeatureVector {
  private Object2FloatOpenHashMap<String> features = new Object2FloatOpenHashMap<String>();

  public enum Order {
    FEATURE_DESCENDING, FEATURE_ASCENDING, VALUE_DESCENDING, VALUE_ASCENDING
  }

  public FeatureVector() {}

  public void addFeatureValue(String feature, float value) {
    if (!features.containsKey(feature)) {
      features.put(feature, value);
    } else {
      features.put(feature, features.getFloat(feature) + value);
    }
  }

  public FeatureVector pruneToSize(int k) {
    List<FeatureValuePair> pairs = getOrderedFeatures();
    Object2FloatOpenHashMap<String> pruned = new Object2FloatOpenHashMap<>();

    for (FeatureValuePair pair : pairs) {
      pruned.put(pair.getFeature(), pair.getValue());
      if (pruned.size() >= k) {
        break;
      }
    }

    this.features = pruned;
    return this;
  }

  public FeatureVector scaleToUnitL2Norm() {
    double norm = computeL2Norm();
    for (String f : features.keySet()) {
      features.put(f, (float) (features.getFloat(f) / norm));
    }

    return this;
  }

  public FeatureVector scaleToUnitL1Norm() {
    double norm = computeL1Norm();
    for (String f : features.keySet()) {
      features.put(f, (float) (features.getFloat(f) / norm));
    }

    return this;
  }

  public Set<String> getFeatures() {
    return features.keySet();
  }

  public float getValue(String feature) {
    return features.containsKey(feature) ? features.getFloat(feature) : 0.0f;
  }

  public Iterator<String> iterator() {
    return features.keySet().iterator();
  }

  public boolean contains(String feature) {
    return features.containsKey(feature);
  }

  public double computeL2Norm() {
    double norm = 0.0;
    for (String term : features.keySet()) {
      norm += Math.pow(features.getFloat(term), 2.0);
    }
    return Math.sqrt(norm);
  }

  public double computeL1Norm() {
    double norm = 0.0;
    for (String term : features.keySet()) {
      norm += Math.abs(features.getFloat(term));
    }
    return norm;
  }

  public static FeatureVector fromTerms(List<String> features) {
    FeatureVector f = new FeatureVector();
    for (String t : features) {
      f.addFeatureValue(t, 1.0f);
    }
    return f;
  }

  private List<FeatureValuePair> getOrderedFeatures() {
    return getOrderedFeatures(Order.VALUE_DESCENDING);
  }

  private List<FeatureValuePair> getOrderedFeatures(Order order) {
    List<FeatureValuePair> pairs = new ArrayList<>(features.size());
    Iterator<String> featureIterator = features.keySet().iterator();
    while (featureIterator.hasNext()) {
      String feature = featureIterator.next();
      float value = features.getFloat(feature);
      FeatureValuePair featureValuePair = new FeatureValuePair(feature, value);
      pairs.add(featureValuePair);
    }

    if (order.equals(Order.VALUE_DESCENDING)) {
      Collections.sort(pairs, (FeatureValuePair x, FeatureValuePair y) -> {
        if (x.getValue() == y.getValue()) return x.getFeature().compareTo(y.getFeature());
        return x.getValue() > y.getValue() ? -1 : 1;
      });
    } else if (order.equals(Order.VALUE_ASCENDING)) {
      Collections.sort(pairs, (FeatureValuePair x, FeatureValuePair y) -> {
        if (x.getValue() == y.getValue()) return x.getFeature().compareTo(y.getFeature());
        return x.getValue() > y.getValue() ? 1 : -1;
      });
    } else if (order.equals(Order.FEATURE_ASCENDING)) {
      Collections.sort(pairs, Comparator.comparing(FeatureValuePair::getFeature));
    } else if (order.equals(Order.FEATURE_DESCENDING)) {
      Collections.sort(pairs, Comparator.comparing(FeatureValuePair::getFeature).reversed());
    }

    return pairs;
  }

  @Override
  public String toString() {
    return this.toString(Order.VALUE_DESCENDING, features.size());
  }

  public String toString(int k) {
    return this.toString(Order.VALUE_DESCENDING, k);
  }

  public String toString(Order order) {
    return this.toString(order, features.size());
  }

  public String toString(Order order, int k) {
    StringBuilder builder = new StringBuilder();
    List<FeatureValuePair> features = getOrderedFeatures(order);
    Iterator<FeatureValuePair> it = features.iterator();
    builder.append("[");
    int i = 0;
    while (it.hasNext() && i++ < k) {
      FeatureValuePair pair = it.next();
      if (i != 1)
        builder.append(", ");
      builder.append(pair.getFeature() + "=" + pair.getValue());
    }
    builder.append("]");
    return builder.toString();
  }

  public static FeatureVector interpolate(FeatureVector x, FeatureVector y, float xWeight) {
    FeatureVector z = new FeatureVector();
    Set<String> vocab = new HashSet<String>();
    vocab.addAll(x.getFeatures());
    vocab.addAll(y.getFeatures());
    Iterator<String> features = vocab.iterator();
    while (features.hasNext()) {
      String feature = features.next();
      float weight = (float) (xWeight * x.getValue(feature) + (1.0 - xWeight) * y.getValue(feature));
      z.addFeatureValue(feature, weight);
    }
    return z;
  }

  public class FeatureValuePair {
    private String feature;
    private float value;

    public FeatureValuePair(String key, float value) {
      this.feature = key;
      this.value = value;
    }

    public String getFeature() {
      return feature;
    }

    public float getValue() {
      return value;
    }

    @Override
    public String toString() {
      return new StringBuilder(feature + "=" + value).toString();
    }
  }
}
