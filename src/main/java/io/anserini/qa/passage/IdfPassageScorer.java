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

import io.anserini.index.IndexUtils;
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
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
  private  final Queue<ScoredPassage> scoredPassageHeap;
  private final int topPassages;

  public IdfPassageScorer(String index, Integer k) throws IOException {
    this.util = new IndexUtils(index);
    this.directory = FSDirectory.open(new File(index).toPath());
    this.reader = DirectoryReader.open(directory);
    this.scoredPassageHeap = new PriorityQueue<ScoredPassage>();
    this.topPassages = k;
  }

  @Override
  public void score(List<String> sentences, String output) throws Exception {
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);

    ClassicSimilarity similarity = new ClassicSimilarity();

    for (String sent: sentences) {
      double idf = 0.0;
      String[] terms = sent.split(" ");
      for (String term: terms) {
        try {
          TermQuery q = (TermQuery) qp.parse(term);
          Term t = q.getTerm();
          idf += similarity.idf(reader.docFreq(t), reader.numDocs());
        } catch (Exception e) {
          continue;
        }
      }

      ScoredPassage scoredPassage = new ScoredPassage(sent, idf / sent.length());
      if (scoredPassageHeap.size() < topPassages || idf > scoredPassageHeap.element().getScore()) {
        if (scoredPassageHeap.size() == topPassages) {
          scoredPassageHeap.remove();
        }
        scoredPassageHeap.add(scoredPassage);
      }
    }
  }

  @Override
  public List<ScoredPassage> extractTopPassages() {
      return new ArrayList<>(scoredPassageHeap);
  }
}
