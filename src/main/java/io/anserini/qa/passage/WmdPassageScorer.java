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
import io.anserini.embeddings.TermNotFoundException;
import io.anserini.embeddings.WordEmbeddingDictionary;
import org.apache.lucene.analysis.StopFilter;
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
    this.topPassages = k;
    scoredPassageHeap = MinMaxPriorityQueue.maximumSize(topPassages).create();
    stopWords = new ArrayList<>();

    // Get file from resources folder
    InputStream is = getClass().getResourceAsStream("/io/anserini/qa/english-stoplist.txt");
    BufferedReader bRdr = new BufferedReader(new InputStreamReader(is));
    String line;
    while ((line = bRdr.readLine()) != null) {
      if (!line.startsWith("#")) {
        stopWords.add(line);
      }
    }
  }

  public double distance(float[] leftVector, float[] rightVector) {
    double sum = 0.0;

    for (int i = 0; i < leftVector.length; i++) {
      sum += Math.pow(leftVector[i] - rightVector[i], 2);
    }
    return Math.sqrt(sum);
  }

  @Override
  public void score(String query, Map<String, Float> sentences) throws Exception {
    StandardAnalyzer sa = new StandardAnalyzer(StopFilter.makeStopSet(stopWords));
    TokenStream tokenStream = sa.tokenStream("contents", new StringReader(query));
    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();

    Set<String> questionTerms = new HashSet<>();
    Set<String> candidateTerms = new HashSet<>();

    // avoid duplicate passages
    Set<String> seenSentences = new HashSet<>();

    while (tokenStream.incrementToken()) {
      questionTerms.add(charTermAttribute.toString());
    }

    for (Map.Entry<String, Float> sent : sentences.entrySet()) {
      double wmd = 0.0;
      candidateTerms.clear();
      sa = new StandardAnalyzer(StopFilter.makeStopSet(stopWords));
      TokenStream candTokenStream = sa.tokenStream("contents", new StringReader(sent.getKey()));
      charTermAttribute = candTokenStream.addAttribute(CharTermAttribute.class);
      candTokenStream.reset();

      while (candTokenStream.incrementToken()) {
        candidateTerms.add(charTermAttribute.toString());
      }

      for (String qTerm : questionTerms) {
        double minWMD = Double.MAX_VALUE;
        for (String candTerm : candidateTerms) {
          try {
            double thisWMD = distance(wmdDictionary.getEmbeddingVector(qTerm), wmdDictionary.getEmbeddingVector(candTerm));
            if (minWMD > thisWMD) {
              minWMD = thisWMD;
            }
          } catch (TermNotFoundException e) {
            String missingTerm = e.getMessage();

            // if the question term and the answer term both do not exist in the
            // dictionary and question term equals to the answer term, then word
            // mover's distance is 0
            if (!qTerm.equals(missingTerm)) {
              continue;
            }

            if (qTerm.equals(candTerm)) {
              minWMD = 0.0;
            } else {
              try {
                // if the embedding for the question term doesn't exist, consider
                // it to be an unknown term
                double thisWMD = distance(wmdDictionary.getEmbeddingVector("unk"), wmdDictionary.getEmbeddingVector(candTerm));
                if (minWMD > thisWMD) {
                  minWMD = thisWMD;
                }
              } catch (TermNotFoundException e1) {
                // "unk" is OOV
              }
            }
          } catch (IOException e) {
            // thrown if the search fails
          }
        }

        if (minWMD != Double.MAX_VALUE) {
          wmd += minWMD;
        }
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
