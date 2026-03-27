/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.search;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import io.anserini.eval.ExcludeDocs;

public class RunOutputWriter<K extends Comparable<K>> implements Closeable {
  private final PrintWriter out;
  private final String format;
  private final String runtag;
  private final String exclude;

  public RunOutputWriter(String output, String format, String runtag, String exclude) throws IOException {
    this.out = new PrintWriter(Files.newBufferedWriter(Paths.get(output), StandardCharsets.UTF_8));
    this.format = format;
    this.runtag = runtag;
    this.exclude = exclude;
  }

  public void writeTopic(K qid, String query, ScoredDoc[] results) {
    int rank = 1;
    if (exclude != null) {
      try {
        ExcludeDocs excludeDocs = new ExcludeDocs(exclude);
        results = excludeDocs.exclude((String)qid, results);
      } catch (Exception e) {
        System.err.println("Error processing exclude docs: " + e.getMessage());
      }
    }

    if ("msmarco".equals(format)) {
      for (ScoredDoc r : results) {
        out.append(String.format(Locale.US, "%s\t%s\t%d\n", qid, r.docid, rank));
        rank++;
      }
    } else {
      // Standard TREC format
      // + the first column is the topic number.
      // + the second column is currently unused and should always be "Q0".
      // + the third column is the official document identifier of the retrieved document.
      // + the fourth column is the rank the document is retrieved.
      // + the fifth column shows the score (integer or floating point) that generated the ranking.
      // + the sixth column is called the "run tag" and should be a unique identifier for your
      for (ScoredDoc r : results) {
        out.append(String.format(Locale.US, "%s Q0 %s %d %f %s\n", qid, r.docid, rank, r.score, runtag));
        rank++;
      }
    }
  }

  @Override
  public void close() throws IOException {
    out.close();
  }
}
