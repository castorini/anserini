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
import io.anserini.index.IndexUtils;
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class IdfPassageScorer implements PassageScorer {

  private final IndexUtils util;
  private final FSDirectory directory;
  private final DirectoryReader reader;
  private final MinMaxPriorityQueue<ScoredPassage> scoredPassageHeap;
  private final int topPassages;
  private final List<String> stopWords;
  private final Map<String, String> termIdfMap;

  public IdfPassageScorer(String index, Integer k) throws IOException {
    this.util = new IndexUtils(index);
    this.directory = FSDirectory.open(new File(index).toPath());
    this.reader = DirectoryReader.open(directory);
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

    termIdfMap = new HashMap<>();
  }

  @Override
  public void score(String query, Map<String, Float> sentences) throws Exception {
    EnglishAnalyzer ea = new EnglishAnalyzer(StopFilter.makeStopSet(stopWords));
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);
    ClassicSimilarity similarity = new ClassicSimilarity();

    String escapedQuery = qp.escape(query);
    Query question = qp.parse(escapedQuery);
    HashSet<String> questionTerms = new HashSet<>(Arrays.asList(question.toString()
            .trim().toLowerCase().split("\\s+")));

    // add the question terms to the termIDF Map
    for (String questionTerm : questionTerms) {
      try {
        TermQuery q = (TermQuery) qp.parse(questionTerm);
        Term t = q.getTerm();

        double termIDF = similarity.idf(reader.docFreq(t), reader.numDocs());
        termIdfMap.put(questionTerm, String.valueOf(termIDF));
      } catch (Exception e) {
        continue;
      }
    }

    // avoid duplicate passages
    HashSet<String> seenSentences = new HashSet<>();

    for (Map.Entry<String, Float> sent : sentences.entrySet()) {
      double idf = 0.0;
      HashSet<String> seenTerms = new HashSet<>();

      String[] terms = sent.getKey().toLowerCase().split("\\s+");
      for (String term: terms) {
        try {
          TermQuery q = (TermQuery) qp.parse(term);
          Term t = q.getTerm();
          double termIDF = similarity.idf(reader.docFreq(t), reader.numDocs());
          termIdfMap.put(term, String.valueOf(termIDF));

          if (questionTerms.contains(t.toString()) && !seenTerms.contains(t.toString())) {
            idf += termIDF;
            seenTerms.add(t.toString());
          } else {
            idf += 0.0;
          }
        } catch (Exception e) {
          continue;
        }
      }

      double weightedScore = idf + 0.0001 * sent.getValue();
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
    return new JSONObject(termIdfMap);
  }
}
