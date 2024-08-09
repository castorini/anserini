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
package io.anserini.fusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

class FusionMethods {
  static final String AVERAGE = "average";
  static final String RRF = "rrf";
  static final String INTERPOLATION = "interpolation";

  public static TreeMap<String, HashMap<String, Double>> average(TreeMap<String, HashMap<String, DocScore>> runA,
      TreeMap<String, HashMap<String, DocScore>> runB, int depth, int k) {

    RescoreMethods.scale(runA, 1 / (double) (runA.size()));
    RescoreMethods.scale(runB, 1 / (double) (runB.size()));

    return AggregationMethods.sum(runA, runB, depth, k);
  }

  public static TreeMap<String, HashMap<String, Double>> reciprocal_rank_fusion(TreeMap<String, HashMap<String, DocScore>> runA,
      TreeMap<String, HashMap<String, DocScore>> runB, double rrf_k, int depth, int k) {

    RescoreMethods.rrf(runA, rrf_k);
    RescoreMethods.rrf(runB, rrf_k);

    return AggregationMethods.sum(runA, runB, depth, k);
  }

  public static TreeMap<String, HashMap<String, Double>> interpolation(TreeMap<String, HashMap<String, DocScore>> runA,
      TreeMap<String, HashMap<String, DocScore>> runB, double alpha, int depth, int k) {

    RescoreMethods.scale(runA, alpha);
    RescoreMethods.scale(runB, 1 - alpha);

    return AggregationMethods.sum(runA, runB, depth, k);
  }

}

class AggregationMethods {
  public static TreeMap<String, HashMap<String, Double>> sum(TreeMap<String, HashMap<String, DocScore>> runA,
      TreeMap<String, HashMap<String, DocScore>> runB, int depth, int k) {
    Set<String> queries = new HashSet<String>();
    TreeMap<String, HashMap<String, Double>> finalHashMap = new TreeMap<String, HashMap<String, Double>>();

    // add all keys into set of queries
    for (String key : runA.keySet()) {
      queries.add(key);
    }
    for (String key : runB.keySet()) {
      queries.add(key);
    }
    Iterator<String> queryIterator = queries.iterator();
    while (queryIterator.hasNext()) {
      String query = queryIterator.next();
      HashMap<String, Double> aggregated = sumIndividualTopic(
          runA.getOrDefault(query, new HashMap<String, DocScore>()),
          runB.getOrDefault(query, new HashMap<String, DocScore>()), depth, k);
      finalHashMap.put(query, aggregated);
    }

    return finalHashMap;
  }

  private static HashMap<String, Double> sumIndividualTopic(HashMap<String, DocScore> docDataA,
      HashMap<String, DocScore> docDataB, int depth, int k) {
    HashMap<String, Double> mergedHashMap = new HashMap<String, Double>();

    // shrink entries
    shrinkToNEntriesDepth(docDataA, depth);
    shrinkToNEntriesDepth(docDataB, depth);
    for (String key : docDataA.keySet()) {
      mergedHashMap.put(key, docDataA.get(key).score);
    }
    for (String key : docDataB.keySet()) {
      Double existingValue = mergedHashMap.getOrDefault(key, 0.0);
      mergedHashMap.put(key, docDataB.get(key).score + existingValue);
    }
    shrinkToNEntriesOutput(mergedHashMap, k);
    return mergedHashMap;
  }

  private static void shrinkToNEntriesDepth(Map<String, DocScore> map, int n) {
    // we keep the entires with highest scores
    int amountToRemove = map.size() - n;
    if (amountToRemove <= 0) {
      return;
    }

    ArrayList<Entry<String, DocScore>> asList = new ArrayList<Entry<String, DocScore>>();
    for (Entry<String, DocScore> entry : map.entrySet()) {
      asList.add(entry);
    }

    Collections.sort(asList, new Comparator<Entry<String, DocScore>>() {
      @Override
      public int compare(Entry<String, DocScore> o1, Entry<String, DocScore> o2) {
        return o1.getValue().score.compareTo(o2.getValue().score);
      }
    });

    for (int i = 0; i < n; i++) {
      map.remove(asList.get(i).getKey());
    }
  }

  private static void shrinkToNEntriesOutput(HashMap<String, Double> hashMap, int n) {
    int amountToRemove = hashMap.size() - n;
    if (amountToRemove <= 0) {
      return;
    }

    ArrayList<Entry<String, Double>> asList = new ArrayList<Entry<String, Double>>();
    for (Entry<String, Double> entry : hashMap.entrySet()) {
      asList.add(entry);
    }
    Collections.sort(asList, new Comparator<Entry<String, Double>>() {
      @Override
      public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
        return o1.getValue().compareTo(o2.getValue());
      }
    });

    for (int i = 0; i < n; i++) {
      hashMap.remove(asList.get(i).getKey());
    }
  }

}

class RescoreMethods {
  public static void normalize(Map<String, HashMap<String, DocScore>> hashMap) {
    for (String outerKey : hashMap.keySet()) {
      Map<String, DocScore> innerHashMap = hashMap.get(outerKey);
      Double min = Double.MAX_VALUE;
      Double max = -1.0;
      for (String innerKey : innerHashMap.keySet()) {
        Double innerValue = innerHashMap.get(innerKey).score;
        if (innerValue < min) {
          min = innerValue;
        }
        if (innerValue > max) {
          max = innerValue;
        }
      }
      for (String innerKey : innerHashMap.keySet()) {
        // Double innerValue = innerHashMap.get(innerKey).score;
        // Double newValue = (innerValue - min) / (max - min);
        DocScore innerValue = innerHashMap.get(innerKey);
        innerValue.score = (innerValue.score - min) / (max - min);
      }
    }
  }

  public static void scale(Map<String, HashMap<String, DocScore>> hashMap, double scale) {
    for (String outerKey : hashMap.keySet()) {
      Map<String, DocScore> innerHashMap = hashMap.get(outerKey);
      for (String innerKey : innerHashMap.keySet()) {
        DocScore innerValue = innerHashMap.get(innerKey);
        innerValue.score *= scale;
      }
    }
  }

  public static void rrf(Map<String, HashMap<String, DocScore>> hashMap, double rrf_k) {
    for (String outerKey : hashMap.keySet()) {
      Map<String, DocScore> innerHashMap = hashMap.get(outerKey);
      for (String innerKey : innerHashMap.keySet()) {
        DocScore innerValue = innerHashMap.get(innerKey);
        innerValue.score = 1 /((double)innerValue.initialRank + rrf_k);
      }
    }
  }

}
