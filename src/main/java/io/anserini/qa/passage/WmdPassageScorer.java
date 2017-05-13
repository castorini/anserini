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
package io.anserini.qa.passage;

import com.google.common.collect.MinMaxPriorityQueue;
import io.anserini.embeddings.WordEmbeddingDictionary;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class WmdPassageScorer implements PassageScorer {

  private final WordEmbeddingDictionary wmdDictionary;
  private final MinMaxPriorityQueue<ScoredPassage> scoredPassageHeap;
  private final int topPassages;
  private final List<String> stopWords;

  public WmdPassageScorer(String index, Integer k) throws IOException {
    this.wmdDictionary = new WordEmbeddingDictionary(index);
    wmdDictionary.getEmbeddingVector("newton");
    this.topPassages = k;
    scoredPassageHeap = MinMaxPriorityQueue.maximumSize(topPassages).create();
    stopWords = new ArrayList<>();

    //Get file from resources folder
    InputStream is = getClass().getResourceAsStream("/io/anserini/qa/english-stoplist.txt");
    BufferedReader bRdr = new BufferedReader(new InputStreamReader(is));
    String line;
    while ((line = bRdr.readLine()) != null) {
      if (!line.contains("#")) {
        stopWords.add(line);
      }
    }
  }

  public double distance(float[] leftVector, float[] rightVector) {
    double sum = 0.0;

    for (int i = 0; i < leftVector.length; i++) {
      sum += Math.pow(leftVector[i] - rightVector[i], 2);
    }
    return  Math.sqrt(sum);
  }

  @Override
  public void score(String query, Map<String, Float> sentences) throws Exception {
    StandardAnalyzer sa = new StandardAnalyzer();
    TokenStream tokenStream = sa.tokenStream("contents", new StringReader(query));
    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();

    HashSet<String> questionTerms = new HashSet<>();
    HashSet<String> candidateTerms = new HashSet<>();

    // avoid duplicate passages
    HashSet<String> seenSentences = new HashSet<>();

    while (tokenStream.incrementToken()) {
      questionTerms.add(charTermAttribute.toString());
    }

    for (Map.Entry<String, Float> sent : sentences.entrySet()) {
      double wmd = 0.0;
      candidateTerms.clear();
      StandardAnalyzer sa2 = new StandardAnalyzer();
      TokenStream candTokenStream = sa2.tokenStream("contents", new StringReader(sent.getKey()));
      charTermAttribute = candTokenStream.addAttribute(CharTermAttribute.class);
      candTokenStream.reset();

      while (candTokenStream.incrementToken()) {
        candidateTerms.add(charTermAttribute.toString());
      }

      for(String qTerm : questionTerms) {
        double minWMD = Double.MAX_VALUE;
        for (String candTerm : candidateTerms) {
          try {
            double thisWMD = distance(wmdDictionary.getEmbeddingVector(qTerm), wmdDictionary.getEmbeddingVector(candTerm));
            if (minWMD > thisWMD) {
              minWMD = thisWMD;
            }
          } catch (ArrayIndexOutOfBoundsException e) {
            // term is OOV
          }
        }
        if (minWMD != Double.MAX_VALUE)
          wmd += minWMD;
      }

      if (candidateTerms.size() <= 4) {
        continue;
      }

      double weightedScore  = -1 * (wmd + 0.0001 * sent.getValue());
      ScoredPassage scoredPassage = new ScoredPassage(sent.getKey(), weightedScore, sent.getValue());
      if ((scoredPassageHeap.size() < topPassages || weightedScore > scoredPassageHeap.peekLast().getScore()) &&
              !seenSentences.contains(sent)) {
        if (scoredPassageHeap.size() == topPassages) {
          scoredPassageHeap.pollLast();
        }
        scoredPassageHeap.add(scoredPassage);
        seenSentences.add(sent.getKey());
      }
    }
  }


  @Override
  public List<ScoredPassage> extractTopPassages() {
    List<ScoredPassage> scoredList = new ArrayList<>(scoredPassageHeap);
    Collections.sort(scoredList);
    return scoredList;
  }

  @Override
  public JSONObject getTermIdfJSON() {
    return null;
  }

  @Override
  public JSONObject getTermIdfJSON(List<String> sentList) {
    return null;
  }

}
