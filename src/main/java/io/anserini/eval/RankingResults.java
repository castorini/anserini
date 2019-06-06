/**
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

package io.anserini.eval;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import static java.util.stream.Collectors.joining;

/**
 * The ranking lists for some queries
 */
public class RankingResults {

  final private Map<String, List<ResultDoc>> rankingList;

  public RankingResults(Map<String, List<ResultDoc>> results) {
    rankingList = new TreeMap<>();
    for (String query : results.keySet()) {
      ArrayList<ResultDoc> rankedList = new ArrayList<>(results.get(query));
      Collections.sort(rankedList);
      rankingList.put(query, rankedList);
    }

  }

  public RankingResults(String filename, boolean long_docids, boolean docid_desc)
      throws IOException {
    rankingList = readResultsFile(filename, long_docids, docid_desc);
  }

  public Map<String, List<ResultDoc>> getRankingList() {
    return rankingList;
  }

  /**
   * Reads in a TREC ranking file.
   *
   * format: qid Q0 docid rank score run_id
   * 1 Q0 doc1 1 8.965 run_tag_1
   * 1 Q0 doc2 2 7.465 run_tag_2
   *
   * @param fileName the ranking result file
   * @param long_docids if we should be treating the docids as Long type
   * @param docid_desc if we should be sorting the docids in descending order when there are scores tie
   *
   * @return {qid: List[ResultDoc]}
   *
   * @throws IOException any io exception
   */
  public Map<String, List<ResultDoc>> readResultsFile(String fileName, boolean long_docids,
                                                      boolean docid_desc) throws IOException {
    TreeMap<String, List<ResultDoc>> ranking = new TreeMap<>();
    InputStream stream;
    if (fileName.endsWith(".gz")) { //.gz
      stream = new DataInputStream(new GZIPInputStream(new FileInputStream(fileName)));
    } else { // in case user had already uncompressed the folder
      stream = new DataInputStream(new FileInputStream(fileName));
    }

    int lineNumber = 1;
    try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {

      while(true) {
        String line = in.readLine();
        if (line == null) {
          break;
        }

        String[] arr = line.split("[\\s\\t]+");
        if(arr.length < 6) {
          throw new RuntimeException("Malformed line at line "+lineNumber+" in "+fileName+": "+ line);
        }

        String qid = arr[0];
        String docno = arr[2];
        //String rank = arr[3];
        String score = arr[4];

        ResultDoc document = new ResultDoc(docno, Double.parseDouble(score), long_docids, docid_desc);
        if (!ranking.containsKey(qid)) {
          ranking.put(qid, new ArrayList<ResultDoc>());
        }
        ranking.get(qid).add(document);
      }

      // ensure sorted order by rank
      for (String query : ranking.keySet()) {
        List<ResultDoc> documents = ranking.get(query);
        Collections.sort(documents);
      }
    }


    String s = ranking.entrySet()
            .stream()
            .map(e -> e.getKey()+" "+e.getValue()+"\n")
            .collect(joining("&"));

    return ranking;
  }
}
