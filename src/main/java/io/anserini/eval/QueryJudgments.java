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

package io.anserini.eval;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

/**
 * The judgments for a specific query.
 * Relevance is represented by an integer:
 * rel &gt; 0  graded relevance
 * rel == 0 non-relevant
 * rel &lt; 0  not judged (possibly categorized as non-relevant)
 */
public class QueryJudgments {
  final private Map<String, Map<String, Integer>> qrels;

  public QueryJudgments(String filename) throws IOException {
    qrels = readJudgmentsFile(filename, true, false);
  }

  public Map<String, Map<String, Integer>> getQrels() {
    return qrels;
  }

  public QueryJudgments(String filename, boolean graded, boolean ignoreNegative) throws IOException {
    qrels = readJudgmentsFile(filename, graded, ignoreNegative);
  }

  /*
  * read TREC formatted judgment file
  * format: qnum  0   doc-name   grade
  * e.g.
  *  19    0   doc303       1
   * 19    0   doc7295      0
  */
  public Map<String, Map<String, Integer>> readJudgmentsFile(String fileName, boolean graded, boolean ignoreNegative) throws IOException {
    Map<String, Map<String, Integer>> judgments = new TreeMap<>();

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
        if(arr.length != 4) {
          throw new RuntimeException("Malformed line at line "+lineNumber+" in "+fileName+": "+ line);
        }

        String qid = arr[0];
        String docno = arr[2];

        if (!judgments.containsKey(qid)) {
          judgments.put(qid, new TreeMap<>());
        }

        // add this judgment to the query
        int grade =  Integer.parseInt(arr[3]);
        if (grade < 0 && ignoreNegative) continue;
        if (!graded) {
          grade = (grade > 0) ? 1 : 0;
        }
        judgments.get(qid).put(docno, grade);
        lineNumber++;
      }
    }
    return judgments;
  }

  /**
   * Method will return whether this docId for this qid is judged or not
   * Note that if qid is invalid this will always return false
   * @param qid     qid
   * @param docId   docId
   * @return
   */
  public boolean isDocJudged(String qid, String docId) {
    if (!qrels.containsKey(qid)) {
      return false;
    }

    if (!qrels.get(qid).containsKey(docId)) {
      return false;
    } else {
      return true;
    }
  }

  public int getRelevanceGrade(String qid, String docid) {
    if (!qrels.containsKey(qid)) {
      return 0;
    }

    if (!qrels.get(qid).containsKey(docid)) {
      return 0;
    }

    if ( qrels.get(qid).get(docid) <= 0 ) return 0;
    return qrels.get(qid).get(docid);
  }

  public Set<String> getQids() {
    return this.qrels.keySet();
  }

  public Map<String, Integer> getDocMap(String qid) {
    if (this.qrels.containsKey(qid)) {
      return this.qrels.get(qid);
    } else {
      return null;
    }
  }
}

