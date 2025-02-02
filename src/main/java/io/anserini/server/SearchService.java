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
    IndexInitializationResult result = initializeIndex(prebuiltIndex);
    this.indexDir = result.indexDir;
    this.isHnswIndex = result.isHnswIndex;
    if (result.error != null) {
      throw new RuntimeException(result.error);
    }
  }

  public List<Map<String, Object>> search(String query, int hits) {
    return search(query, hits, null, null, null);
  }

  public List<Map<String, Object>> search(String query, int hits,
      Integer efSearch, String encoder, String queryGenerator) {
    validateSearchParameters(query, hits);
    validateSettings(efSearch, encoder, queryGenerator);

    try {
      if (!isHnswIndex) {
        try (SimpleSearcher searcher = new SimpleSearcher(indexDir)) {
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
          return candidates;
        }
      } else {
        IndexInfo indexInfo = IndexInfo.get(prebuiltIndex);
        HnswDenseSearcher.Args args = new HnswDenseSearcher.Args();
        args.index = indexDir;
        args.efSearch = efSearch != null ? efSearch
            : getEfSearchOverride() != null ? getEfSearchOverride()
                : IndexInfo.DEFAULT_EF_SEARCH;
        args.encoder = encoder != null ? encoder.replace(".class", "")
            : getEncoderOverride() != null ? getEncoderOverride().replace(".class", "")
            : indexInfo.encoder != null ? indexInfo.encoder.replace(".class", "") : null;
        args.queryGenerator = queryGenerator != null ? queryGenerator.replace(".class", "")
            : getQueryGeneratorOverride() != null ? getQueryGeneratorOverride().replace(".class", "")
            : indexInfo.queryGenerator.replace(".class", "");
        try (HnswDenseSearcher<Float> searcher = new HnswDenseSearcher<Float>(args)) {
          ScoredDoc[] results = searcher.search(query, hits);
          List<Map<String, Object>> candidates = new ArrayList<>();
          for (ScoredDoc r : results) {
            candidates.add(Map.of("docid", r.docid, "score", r.score));
          }
          return candidates;
        }
      }
    } catch (Exception e) {
      return List.of();
    }
  }

  public Map<String, Object> getDocument(String docid) {
    if (!isHnswIndex) throw new IllegalArgumentException("getDocument is only supported for HNSW indexes");
    try (SimpleSearcher searcher = new SimpleSearcher(indexDir)) {
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
      return candidate;
    } catch (Exception e) {
      e.printStackTrace();
      return Map.of();
    }
  }

  public Integer getEfSearchOverride() {
    return (Integer) indexOverrides.get("efSearch");
  }

  public String getEncoderOverride() {
    return (String) indexOverrides.get("encoder");
  }

  public String getQueryGeneratorOverride() {
    return (String) indexOverrides.get("queryGenerator");
  }

  public void setEfSearchOverride(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("efSearch cannot be empty");
    }
    validateSettings(Integer.parseInt(value), getEncoderOverride(), getQueryGeneratorOverride());
    indexOverrides.put("efSearch", Integer.parseInt(value));
  }

  public void setEncoderOverride(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Encoder cannot be empty");
    }
    validateSettings(getEfSearchOverride(), value, getQueryGeneratorOverride());
    indexOverrides.put("encoder", value.replace(".class", ""));
  }

  public void setQueryGeneratorOverride(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("QueryGenerator cannot be empty");
    }
    validateSettings(getEfSearchOverride(), getEncoderOverride(), value);
    indexOverrides.put("queryGenerator", value.replace(".class", ""));
  }

  private void validateSearchParameters(String query, int hits) {
    if (query == null || query.trim().isEmpty()) {
      throw new IllegalArgumentException("Query cannot be empty");
    }
    if (hits <= 0) {
      throw new IllegalArgumentException("Number of hits must be positive");
    }
  }

  private void validateSettings(Integer efSearch, String encoder, String queryGenerator) {
    IndexInfo indexInfo = IndexInfo.get(prebuiltIndex);

    if (efSearch != null) {
      if (efSearch <= 0) {
        throw new IllegalArgumentException("efSearch must be positive but got " + efSearch);
      }
      if (!isHnswIndex) {
        throw new IllegalArgumentException("efSearch parameter is only supported for HNSW indexes, but index " + prebuiltIndex + " is not HNSW");
      }
    }

    if (encoder != null) {
      if (!encoder.equals(indexInfo.encoder)) {
        throw new IllegalArgumentException("Unsupported encoder: " + encoder + " for index " + prebuiltIndex);
      }
    }

    if (queryGenerator != null) {
      if (!queryGenerator.equals(indexInfo.queryGenerator)) {
        throw new IllegalArgumentException(
            "Unsupported queryGenerator: " + queryGenerator + " for index " + prebuiltIndex);
      }
    }
  }

  private IndexInitializationResult initializeIndex(String prebuiltIndex) {
    try {
      PrebuiltIndexHandler handler = new PrebuiltIndexHandler(prebuiltIndex);
      handler.initialize();
      handler.download();
      String indexDir = handler.decompressIndex();
      IndexInfo indexInfo = IndexInfo.get(prebuiltIndex);
      boolean isHnsw = indexInfo.indexType == IndexInfo.IndexType.DENSE_HNSW;
      return new IndexInitializationResult(indexDir, isHnsw, null);
    } catch (Exception e) {
      return new IndexInitializationResult(null, false, e);
    }
  }

  private static class IndexInitializationResult {
    final String indexDir;
    final boolean isHnswIndex;
    final Exception error;

    IndexInitializationResult(String indexDir, boolean isHnswIndex, Exception error) {
      this.indexDir = indexDir;
      this.isHnswIndex = isHnswIndex;
      this.error = error;
    }
  }
}