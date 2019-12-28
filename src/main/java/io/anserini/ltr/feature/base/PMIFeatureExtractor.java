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

package io.anserini.ltr.feature.base;

import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PMI implemented as
 * PMI(t1, t2) = log (Pr(t1, t2|D) / Pr(t1|D)Pr(t2|D)),
 * where pr are the MLE
 * described on page 22 of Carmel, Yom-Tov 2010
 */
public class PMIFeatureExtractor<T> implements FeatureExtractor<T> {

  private String lastQueryProcessed = "";
  private float lastComputedValue = 0f;

  private int countPostingIntersect(PostingsEnum firstEnum, PostingsEnum secondEnum) throws IOException {

    int count = 0;
    int firstDocId = firstEnum.nextDoc();
    int secondDocId = secondEnum.nextDoc();
    // We are assuming that docEnum are in doc id order
    // According to Lucene documentation, the doc ids are in non decreasing order
    while (true) {
      if (firstDocId == PostingsEnum.NO_MORE_DOCS
              || secondDocId == PostingsEnum.NO_MORE_DOCS) {
        break;
      }

      if (firstDocId > secondDocId) {
        secondDocId = secondEnum.nextDoc();
      } else if (secondDocId > firstDocId) {
        firstDocId = firstEnum.nextDoc();
      } else {
        count ++;
        firstDocId = firstEnum.nextDoc();
        secondDocId = secondEnum.nextDoc();
      }
    }

    return count;
  }

  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    // We need docfreqs of each token
    // and also doc freqs of each pair
    if (!this.lastQueryProcessed.equals(context.getQueryText())) {
      this.lastQueryProcessed = context.getQueryText();
      this.lastComputedValue = 0.0f;

      Set<String> querySet = new HashSet<>(context.getQueryTokens());
      IndexReader reader = context.getIndexSearcher().getIndexReader();
      Map<String, Integer> docFreqs = new HashMap<>();
      List<String> queryTokens = new ArrayList<>(querySet);

      try {

        for (String token : querySet) {
          docFreqs.put(token, reader.docFreq(new Term(LuceneDocumentGenerator.FIELD_BODY, token)));
        }

        float sumPMI = 0.0f;
        float pairsComputed = 0.0f;

        for (int i = 0; i < queryTokens.size(); i++) {
          String firstToken = queryTokens.get(i);
          for (int j = i +1; j < queryTokens.size(); j++) {
            pairsComputed ++;
            String secondToken = queryTokens.get(j);
            PostingsEnum firstEnum = MultiTerms.getTermPostingsEnum(reader,LuceneDocumentGenerator.FIELD_BODY, new BytesRef(firstToken));
            PostingsEnum secondEnum = MultiTerms.getTermPostingsEnum(reader,LuceneDocumentGenerator.FIELD_BODY, new BytesRef(secondToken));
            int intersect;
            if (firstEnum == null || secondEnum == null) {
              intersect = 0;
            } else {
              intersect = countPostingIntersect(firstEnum, secondEnum);
            }

            if (intersect == 0) continue;
            // We should never reach this point and have doc freq =0 because then there would
            // be no intersect between docIds
            int firstDocFreq = docFreqs.getOrDefault(firstToken, 1);
            int secondDocFreq = docFreqs.getOrDefault(secondToken, 1);
            float fraction = (intersect / (float) (firstDocFreq * secondDocFreq));
            if (fraction <= 0) {
              continue;
            }
            sumPMI += Math.log(fraction);
          }
        }

        // Now compute the average
        if (pairsComputed != 0) {
          this.lastComputedValue = sumPMI / pairsComputed;
        }
      } catch (IOException e) {
        this.lastComputedValue = 0.0f;
      }
    }
    return this.lastComputedValue;
  }

  @Override
  public String getName() {
    return "PMIFeature";
  }
}
