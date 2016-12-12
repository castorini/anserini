/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.eval.metric;

import io.anserini.eval.ResultDoc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Precision of a query, possibly with cutoff parameter
 */
public class NDCG extends MetricBase {
  protected int cutoff;

  public NDCG() {
    super();
    this.cutoff = Integer.MAX_VALUE;
  }

  public NDCG(int cutoff) {
    super();
    this.cutoff = cutoff;
  }

  @Override
  public String getName() {
    return "nDCG";
  }

  @Override
  public double evaluate(List<ResultDoc> resultList, Map<String, Integer> judgments) {
    Integer[] relList = new Integer[resultList.size()];
    Arrays.fill(relList, 0);
    // construct the rel list
    int index = 0;
    for (ResultDoc doc : resultList) {
      String docid = doc.getDocid();
      if (judgments.containsKey(docid) && judgments.get(docid) > 0) {
        relList[index] = judgments.get(doc.getDocid());
      }
      index++;
    }

    double dcg = computeDCG(relList);

    Integer[] idealList = new Integer[judgments.size()];
    Arrays.fill(idealList, 0);
    index = 0;
    for (int judgment : judgments.values()) {
      if (judgment > 0) {
        idealList[index] = judgment;
        index++;
      }
    }
    Arrays.sort(idealList, Collections.reverseOrder());
    double normalizer = computeDCG(idealList);

    if(normalizer != 0){
      return dcg / normalizer;
    }

    return 0.0;
  }

  /*
  * Based on https://github.com/lintool/Anserini/blob/master/eval/gdeval.pl#L182
  *  (2^rels[i]-1)/log2(i+1)
  */
  private double computeDCG(Integer[] rels) {
    double dcg = 0.0;
    for (int i = 0; i < Math.min(rels.length, this.cutoff); i++) {
      dcg += (Math.pow(2, rels[i]) - 1.0) / (Math.log(i + 2)/Math.log(2.0));
    }
    return dcg;
  }

  @Override
  public String getFormat() {
    return ".4f";
  }
}
