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

package io.anserini.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import io.anserini.search.ScoredDoc;

public class ExcludeDocs {
  private static final String BRIGHT_AOPS = "bright-aops";
  private static final String BRIGHT_THEOREMQAQ = "bright-theoremqa-questions";
  private static final String BRIGHT_LEETCODE = "bright-leetcode";
  private static final String PREFIX = "exclude.";
  private static final String SUFFIX = ".txt";

  private static final String CACHE_DIR = Path.of(System.getProperty("user.home"), ".cache", "pyserini", "topics-and-qrels").toString();
  private static final String SERVER_PATH = "https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/";

  private HashMap<String, Set<String>> excludeDocs = new HashMap<>();

  public static boolean isExcludable(String name) {
    return name.contains("bright") && (name.contains("aops") || name.contains("theoremqa") && name.contains("questions") || name.contains("leetcode"));
  }

  public ExcludeDocs(String name) throws Exception {
    String file;
    if (name.contains(BRIGHT_AOPS)) {
      file = BRIGHT_AOPS;
    } else if (name.contains(BRIGHT_THEOREMQAQ)) {
      file = BRIGHT_THEOREMQAQ;
    } else if (name.contains(BRIGHT_LEETCODE)) {
      file = BRIGHT_LEETCODE;
    } else {
      throw new IllegalArgumentException("Unknown dataset: " + name);
    }
    file = PREFIX + file + SUFFIX;

    Path local = Paths.get(CACHE_DIR, file);
    if (local == null || !Files.exists(local)) {
      String URL = SERVER_PATH + file;
      System.out.println("Downloading exclusion ids from " + URL);
      File qrelsFile = new File(local.toString());  
      try {
        FileUtils.copyURLToFile(new URI(URL).toURL(), qrelsFile);
      } catch (Exception e) {
        throw new IOException("Error downloading exclusion ids from " + URL);
      }
    }

    try (BufferedReader br = new BufferedReader(new FileReader(local.toString()))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] arr = line.split(" ");
        String qid = arr[0];
        String docid = arr[1];
        excludeDocs.computeIfAbsent(qid, k -> new java.util.HashSet<>()).add(docid);
      }
    } catch (IOException e) {
      throw new IOException("Could not read exclusion file!");
    }
  }

  public ScoredDoc[] exclude(String qid, ScoredDoc[] results) {
    if (!excludeDocs.containsKey(qid)) {
      return results;
    }
    Set<String> excludeSet = excludeDocs.get(qid);
    if (excludeSet == null || excludeSet.isEmpty()) {
      return results;
    }

    int removed = 0;
    for (int i = 0; i < results.length; i++) {
      ScoredDoc doc = results[i];
      if (excludeSet.contains(doc.docid)) {
        removed++;
      } else if (removed > 0) {
        results[i - removed] = doc; // Shift the valid doc to the left
      }
    }
    if (removed > 0) {
      ScoredDoc[] filteredResults = new ScoredDoc[results.length - removed];
      System.arraycopy(results, 0, filteredResults, 0, results.length - removed);
      return filteredResults;
    }
    return results; // No docs were removed, return original results
  }
}
