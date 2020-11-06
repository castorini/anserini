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

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes the TFIDF feature according to Lucene's formula,
 * Not the same because we don't compute length norm or query norm, with boost 1
 */
public class TFIDFFeatureExtractor implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(TFIDFFeatureExtractor.class);
  private String lastQueryProcessed = "";
  private Map<String,Integer> lastComputedValue = new HashMap<>();

  @Override
  public float extract(Document doc, Terms terms, String queryText, List<String> queryTokens, IndexReader reader) {
    float score = 0.0f;
    Map<String, Long> countMap = new HashMap<>();
    Map<String, Integer> docFreqs = null;
    long numDocs =  reader.numDocs();
    if (!lastQueryProcessed.equals(queryText)) {
      lastQueryProcessed = queryText;
      docFreqs = new HashMap<>();
      for (String queryToken : queryTokens) {
        try {
          docFreqs.put(queryToken, reader.docFreq(new Term(IndexArgs.CONTENTS, queryToken)));
        } catch (IOException e) {
          LOG.error("Error trying to read document frequency");
          docFreqs.put(queryToken, 0);
        }
      }
      lastComputedValue = docFreqs;
    } else {
      docFreqs = lastComputedValue;
    }
    try {
      TermsEnum termsEnum = terms.iterator();
      while (termsEnum.next() != null) {
        String termString = termsEnum.term().utf8ToString();
        if (queryTokens.contains(termString)) {
          countMap.put(termString, termsEnum.totalTermFreq());
        }
      }
    } catch (IOException e) {
      LOG.error("Error while accessing term vector");
    }

    TFIDFSimilarity similarity = new ClassicSimilarity();

    // number of query tokens found
    // how many of our query tokens were found
    //float coord = similarity.coord(countMap.size(), context.getQueryTokens().size());
    // coord removed in Lucene 7

    for (String token : queryTokens) {
      long termFreq = countMap.getOrDefault(token, 0L);
      long docFreq = docFreqs.getOrDefault(token, 0);
      float tf = similarity.tf(termFreq);
      float idf = similarity.idf(docFreq, numDocs);
      score += tf * idf*idf;
    }

    //score *= coord;

    return score;
  }

  @Override
  public String getName() {
    return "TFIDF";
  }

  @Override
  public String getField() {
    return null;
  }

  @Override
  public FeatureExtractor clone() {
    return new TFIDFFeatureExtractor();
  }
}
