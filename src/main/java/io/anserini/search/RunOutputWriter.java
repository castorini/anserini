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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.eval.ExcludeDocs;
import io.anserini.index.Constants;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RunOutputWriter<K extends Comparable<K>> implements Closeable {
  private final PrintWriter out;
  private final String format;
  private final String runtag;
  private final ObjectMapper mapper = new ObjectMapper(); // For JSON serialization
  private final PrintWriter outputRerankerRequests;
  private final String exclude;

  public RunOutputWriter(String output, String format, String runtag, String outputRerankerRequests, String exclude) throws IOException {
    this.out = new PrintWriter(Files.newBufferedWriter(Paths.get(output), StandardCharsets.UTF_8));
    this.format = format;
    this.runtag = runtag;
    this.outputRerankerRequests = outputRerankerRequests == null ? null : new PrintWriter(Files.newBufferedWriter(Paths.get(outputRerankerRequests), StandardCharsets.UTF_8));
    this.exclude = exclude;
  }

  public RunOutputWriter(String output, String format, String runtag, String outputRerankerRequests) throws IOException {
    this(output, format, runtag, outputRerankerRequests, null);
  }

  private Map<String, Object> extractDocumentContent(ScoredDoc scoredDoc) throws JsonProcessingException {
    String raw = scoredDoc.lucene_document.get(Constants.RAW);
    if (raw == null) {
      throw new IllegalArgumentException("Raw document with docid " + scoredDoc.docid + " not found in index.");
    }

    try {
      JsonNode rootNode = mapper.readTree(raw);
      if (rootNode != null && rootNode.isObject()) {
        Map<String, Object> content = mapper.convertValue(rootNode, new TypeReference<Map<String, Object>>() {});
        content.remove(Constants.ID);
        content.remove("_id");
        content.remove("docid");
        return content;
      }
    } catch (JsonProcessingException e) {
      // Fall back to a structured wrapper for non-JSON stored raw documents.
    }

    Map<String, Object> content = new LinkedHashMap<>();
    content.put("raw", raw);

    String contents = scoredDoc.lucene_document.get(Constants.CONTENTS);
    if (contents != null && !contents.equals(raw)) {
      content.put("contents", contents);
    }

    return content;
  }

  public void writeTopic(K qid, String query, ScoredDoc[] results) throws JsonProcessingException {
    int rank = 1;
    if (exclude != null) {
      try {
        ExcludeDocs excludeDocs = new ExcludeDocs(exclude);
        results = excludeDocs.exclude((String)qid, results);
      } catch (Exception e) {
        System.err.println("Error processing exclude docs: " + e.getMessage());
      }
    }
    if (outputRerankerRequests != null) {
      List<Map<String, Object>> candidates = new ArrayList<>();
      for (ScoredDoc r : results) {
        Map<String, Object> candidate = new LinkedHashMap<>();
        candidate.put("docid", r.docid);
        candidate.put("score", r.score);
        candidate.put("doc", extractDocumentContent(r));
        candidates.add(candidate);
      }
      Map<String, Object> queryMap = new LinkedHashMap<>();
      queryMap.put("query", new LinkedHashMap<>(Map.of("qid", qid, "text", query)));
      queryMap.put("candidates", candidates);
      outputRerankerRequests.println(mapper.writeValueAsString(queryMap));
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
    if (outputRerankerRequests != null) {
      outputRerankerRequests.close();
    }
  }
}
