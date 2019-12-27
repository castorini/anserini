/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FeatureVector {
  private Object2FloatOpenHashMap<String> features = new Object2FloatOpenHashMap<String>();

  public FeatureVector() {}

  public void addFeatureWeight(String term, float weight) {
    if (!features.containsKey(term)) {
      features.put(term, weight);
    } else {
      features.put(term, features.get(term) + weight);
    }
  }

  public FeatureVector pruneToSize(int k) {
    List<KeyValuePair> pairs = getOrderedFeatures();
    Object2FloatOpenHashMap<String> pruned = new Object2FloatOpenHashMap<String>();
    
    for (KeyValuePair pair : pairs) {
      pruned.put((String) pair.getKey(), pair.getValue());
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
      features.put(f, (float) (features.get(f) / norm));
    }

    return this;
  }

  public FeatureVector scaleToUnitL1Norm() {
    double norm = computeL1Norm();
    for (String f : features.keySet()) {
      features.put(f, (float) (features.get(f) / norm));
    }

    return this;
  }

  public Set<String> getFeatures() {
    return features.keySet();
  }

  public float getFeatureWeight(String feature) {
    return features.containsKey(feature) ? features.get(feature) : 0.0f;
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
      norm += Math.pow(features.get(term), 2.0);
    }
    return Math.sqrt(norm);
  }

  public double computeL1Norm() {
    double norm = 0.0;
    for (String term : features.keySet()) {
      norm += Math.abs(features.get(term));
    }
    return norm;
  }

  public static FeatureVector fromTerms(List<String> terms) {
    FeatureVector f = new FeatureVector();
    for (String t : terms) {
      f.addFeatureWeight(t, 1.0f);
    }
    return f;
  }

  // VIEWING

  @Override
  public String toString() {
    return this.toString(features.size());
  }

  private List<KeyValuePair> getOrderedFeatures() {
    List<KeyValuePair> kvpList = new ArrayList<KeyValuePair>(features.size());
    Iterator<String> featureIterator = features.keySet().iterator();
    while (featureIterator.hasNext()) {
      String feature = featureIterator.next();
      float value = features.get(feature);
      KeyValuePair keyValuePair = new KeyValuePair(feature, value);
      kvpList.add(keyValuePair);
    }

    Collections.sort(kvpList, new Comparator<KeyValuePair>() {
      public int compare(KeyValuePair x, KeyValuePair y) {
        double xVal = x.getValue();
        double yVal = y.getValue();

        return (xVal > yVal ? -1 : (xVal == yVal ? 0 : 1));
      }
    });

    return kvpList;
  }

  public String toString(int k) {
    DecimalFormat format = new DecimalFormat("#.#########");
    StringBuilder b = new StringBuilder();
    List<KeyValuePair> kvpList = getOrderedFeatures();
    Iterator<KeyValuePair> it = kvpList.iterator();
    int i = 0;
    while (it.hasNext() && i++ < k) {
      KeyValuePair pair = it.next();
      b.append(format.format(pair.getValue()) + " " + pair.getKey() + "\n");
    }
    return b.toString();

  }

  public static FeatureVector interpolate(FeatureVector x, FeatureVector y, float xWeight) {
    FeatureVector z = new FeatureVector();
    Set<String> vocab = new HashSet<String>();
    vocab.addAll(x.getFeatures());
    vocab.addAll(y.getFeatures());
    Iterator<String> features = vocab.iterator();
    while (features.hasNext()) {
      String feature = features.next();
      float weight = (float) (xWeight * x.getFeatureWeight(feature) + (1.0 - xWeight)
          * y.getFeatureWeight(feature));
      z.addFeatureWeight(feature, weight);
    }
    return z;
  }

  private class KeyValuePair {
    private String key;
    private float value;

    public KeyValuePair(String key, float value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    @Override
    public String toString() {
      StringBuilder b = new StringBuilder(value + "\t" + key);
      return b.toString();
    }

    public float getValue() {
      return value;
    }
  }
}
