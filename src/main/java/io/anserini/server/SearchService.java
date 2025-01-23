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

package io.anserini.server;

import io.anserini.index.Constants;
import io.anserini.search.ScoredDoc;
import io.anserini.search.SimpleSearcher;
import io.anserini.search.HnswDenseSearcher;
import io.anserini.util.PrebuiltIndexHandler;
import io.anserini.index.IndexInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearchService {

  private final String indexDir;
  private final String prebuiltIndex;
  private final float k1 = 0.9f;
  private final float b = 0.4f;
  private final ObjectMapper mapper = new ObjectMapper();
  private final boolean isHnswIndex;
  private final Map<String, Object> indexOverrides = new ConcurrentHashMap<>();

  public SearchService(String prebuiltIndex) {
    this.prebuiltIndex = prebuiltIndex;
    PrebuiltIndexHandler handler = new PrebuiltIndexHandler(prebuiltIndex);
    handler.initialize();
    try {
      handler.download();
      indexDir = handler.decompressIndex();
      isHnswIndex = prebuiltIndex.contains(".hnsw");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<Map<String, Object>> search(String query, int hits) {
    return search(query, hits, null, null, null);
  }

  public List<Map<String, Object>> search(String query, int hits,
      Integer efSearch, String encoder, String queryGenerator) {
    try {
      System.out.println("=== Search Parameters ===");
      System.out.println("Query: " + query);
      System.out.println("Hits: " + hits);
      System.out.println("EF Search: " + efSearch);
      System.out.println("Encoder: " + encoder);
      System.out.println("Query Generator: " + queryGenerator);
      System.out.println("Is HNSW Index: " + isHnswIndex);
      System.out.println("Index Dir: " + indexDir);

      if (!isHnswIndex) {
        // Regular search with document contents
        SimpleSearcher searcher = new SimpleSearcher(indexDir);
        searcher.set_bm25(k1, b);
        ScoredDoc[] results = searcher.search(query, hits);
        List<Map<String, Object>> candidates = new ArrayList<>();
        for (ScoredDoc r : results) {
          Map<String, Object> candidate = new LinkedHashMap<>();
          candidate.put("docid", r.docid);
          candidate.put("score", r.score);
          String raw = r.lucene_document.get(Constants.RAW);
          if (raw != null) {
            JsonNode rootNode = mapper.readTree(raw);
            Map<String, Object> content = mapper.convertValue(rootNode, Map.class);
            content.remove("docid");
            content.remove("id");
            content.remove("_id");
            candidate.put("doc", content);
          } else {
            candidate.put("doc", null);
          }
          candidates.add(candidate);
        }
        searcher.close();
        return candidates;
      } else {
        // HNSW search - only return docids and scores
        HnswDenseSearcher.Args args = new HnswDenseSearcher.Args();
        args.index = indexDir;

        args.efSearch = efSearch != null ? efSearch: getEfSearchOverride() != null ? getEfSearchOverride() : IndexInfo.DEFAULT_EF_SEARCH;

        IndexInfo indexInfo = IndexInfo.get(prebuiltIndex);
        args.encoder = encoder != null ? encoder: getEncoderOverride() != null ? getEncoderOverride(): indexInfo.getDefaultEncoder();

        args.queryGenerator = queryGenerator != null ? queryGenerator: getQueryGeneratorOverride() != null ? getQueryGeneratorOverride(): indexInfo.getDefaultQueryGenerator();

        System.out.println("=== HNSW Args ===");
        System.out.println("Index: " + args.index);
        System.out.println("EF Search: " + args.efSearch);
        System.out.println("Encoder: " + args.encoder);
        System.out.println("Query Generator: " + args.queryGenerator);

        HnswDenseSearcher<?> searcher = new HnswDenseSearcher<>(args);
        System.out.println("Created HNSW searcher");

        ScoredDoc[] results = searcher.search(query, hits);
        System.out.println("Search completed, results: " + (results != null ? results.length : "null"));

        List<Map<String, Object>> candidates = new ArrayList<>();
        if (results != null) {
          for (ScoredDoc r : results) {
            candidates.add(Map.of("docid", r.docid,"score", r.score));
          }
        }

        searcher.close();
        return candidates;
      }
    } catch (Exception e) {
      System.out.println("=== Search Error ===");
      System.out.println("Error type: " + e.getClass().getName());
      System.out.println("Error message: " + e.getMessage());
      e.printStackTrace();
      return List.of();
    }
  }

  public Map<String, Object> getDocument(String docid) {
    if (isHnswIndex) {
      throw new UnsupportedOperationException("Document retrieval not supported for HNSW indexes");
    }

    try {
      SimpleSearcher searcher = new SimpleSearcher(indexDir);
      String raw = searcher.doc(docid).get(Constants.RAW);
      Map<String, Object> candidate = new LinkedHashMap<>();
      if (raw != null) {
        JsonNode rootNode = mapper.readTree(raw);
        Map<String, Object> content = mapper.convertValue(rootNode, Map.class);
        content.remove("docid");
        content.remove("id");
        content.remove("_id");
        candidate.put("doc", content);
      } else {
        candidate.put("doc", null);
      }
      searcher.close();
      return candidate;
    } catch (Exception e) {
      e.printStackTrace();
      return Map.of();
    }
  }

  // Simple getters with type casting
  public Integer getEfSearchOverride() {
    return (Integer) indexOverrides.get("efSearch");
  }

  public String getEncoderOverride() {
    return (String) indexOverrides.get("encoder");
  }

  public String getQueryGeneratorOverride() {
    return (String) indexOverrides.get("queryGenerator");
  }

  // Simple setters with basic validation
  public void setEfSearchOverride(String value) {
    try {
      int efSearch = Integer.parseInt(value);
      if (efSearch <= 0) {
        throw new IllegalArgumentException("efSearch must be positive");
      }
      indexOverrides.put("efSearch", efSearch);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid efSearch value: " + value);
    }
  }

  public void setEncoderOverride(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("encoder cannot be empty");
    }
    indexOverrides.put("encoder", value);
  }

  public void setQueryGeneratorOverride(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("queryGenerator cannot be empty");
    }
    indexOverrides.put("queryGenerator", value);
  }
}