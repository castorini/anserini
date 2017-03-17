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
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IdfPassageScorer implements PassageScorer {

  private final IndexUtils util;
  private final FSDirectory directory;
  private final DirectoryReader reader;
  private final MinMaxPriorityQueue<ScoredPassage> scoredPassageHeap;
  private final int topPassages;

  public IdfPassageScorer(String index, Integer k) throws IOException {
    this.util = new IndexUtils(index);
    this.directory = FSDirectory.open(new File(index).toPath());
    this.reader = DirectoryReader.open(directory);
    this.topPassages = k;
    scoredPassageHeap = MinMaxPriorityQueue.maximumSize(topPassages).create();
  }

  @Override
  public void score(List<String> sentences, String output) throws Exception {
    EnglishAnalyzer ea = new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);
    ClassicSimilarity similarity = new ClassicSimilarity();

    Query question = qp.parse(sentences.remove(0));
    HashSet<String> questionTerms = new HashSet<>(Arrays.asList(question.toString().trim().split("\\s+")));

    for (String sent: sentences) {
      double idf = 0.0;
      String[] terms = sent.split("\\s+");
      for (String term: terms) {
        try {
          TermQuery q = (TermQuery) qp.parse(term);
          Term t = q.getTerm();
          if (questionTerms.contains(t.toString())) {
            idf += similarity.idf(reader.docFreq(t), reader.numDocs());
          } else {
            idf += 0.0;
          }
        } catch (Exception e) {
          continue;
        }
      }

      double normalizedScore = idf / sent.length();
      ScoredPassage scoredPassage = new ScoredPassage(sent, normalizedScore);
      if (scoredPassageHeap.size() < topPassages || normalizedScore > scoredPassageHeap.peekFirst().getScore()) {
        if (scoredPassageHeap.size() == topPassages) {
          scoredPassageHeap.pollLast();
        }
        scoredPassageHeap.add(scoredPassage);
      }
    }
  }

  @Override
  public List<ScoredPassage> extractTopPassages() {
      List<ScoredPassage> scoredList = new ArrayList<>(scoredPassageHeap);
      Collections.sort(scoredList);
      return scoredList;
  }
}
