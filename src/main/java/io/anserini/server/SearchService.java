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

public class SearchService {

  private final String indexDir;
  private final String prebuiltIndex;
  private final float k1 = 0.9f;
  private final float b = 0.4f;
  private final ObjectMapper mapper = new ObjectMapper();
  private final boolean isHnswIndex;

  private static final int DEFAULT_EF_SEARCH = 100;
  private static final String DEFAULT_QUERY_GENERATOR = "VectorQueryGenerator";

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
    try {
      if (!isHnswIndex) {
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
        return searchHnsw(query, hits, 0, null, null);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return List.of();
    }
  }

  public List<Map<String, Object>> search(String query, int hits,
      int efSearch, String encoder, String queryGenerator) {
    if (!isHnswIndex) {
      // Ignore HNSW parameters for BM25 indexes
      return search(query, hits);
    }
    return searchHnsw(query, hits, efSearch, encoder, queryGenerator);
  }

  private List<Map<String, Object>> searchHnsw(String query, int hits,
      int efSearch, String encoder, String queryGenerator) {
    try {
      HnswDenseSearcher.Args args = createHnswArgs(efSearch, encoder, queryGenerator);
      HnswDenseSearcher searcher = new HnswDenseSearcher(args);
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
    } catch (Exception e) {
      e.printStackTrace();
      return List.of();
    }
  }

  private HnswDenseSearcher.Args createHnswArgs(int efSearch, String encoder, String queryGenerator) {
    HnswDenseSearcher.Args args = new HnswDenseSearcher.Args();
    args.index = indexDir;
    args.efSearch = efSearch;
    args.queryGenerator = queryGenerator != null ? queryGenerator : DEFAULT_QUERY_GENERATOR;
    
    // Attempt to get encoder from IndexInfo, or use provided encoder
    if (encoder != null) {
      args.encoder = encoder;
    } else if (IndexInfo.contains(prebuiltIndex)) {
      IndexInfo info = IndexInfo.get(prebuiltIndex);
      args.encoder = info.model.substring(0, info.model.indexOf(" w/ HNSW"));
    }
    return args;
  }

  public Map<String, Object> getDocument(String docid) {
    try {
      if (isHnswIndex) {
        return getHnswDocument(docid);
      }
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

  private Map<String, Object> getHnswDocument(String docid) {
    try {
      HnswDenseSearcher searcher = new HnswDenseSearcher(createHnswArgs(DEFAULT_EF_SEARCH, null, null));
      String raw = searcher.search(docid, 1)[0].lucene_document.get(Constants.RAW);
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
  
}