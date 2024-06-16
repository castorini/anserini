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

import io.anserini.index.IndexInfo;
import io.anserini.util.PrebuiltIndexHandler;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class Controller {

  private static final String DEFAULT_INDEX = "msmarco-v1-passage";

  @RequestMapping(method = RequestMethod.GET, path = {"/index/{index}/search", "/search"})
  public Map<String, Object> search(@PathVariable(value = "index", required = false) String index,
      @RequestParam("query") String query,
      @RequestParam(value = "hits", defaultValue = "10") int hits,
      @RequestParam(value = "qid", defaultValue = "") String qid) {

    if (index == null) {
      index = DEFAULT_INDEX;
    }

    if (!IndexInfo.contains(index)) {
      throw new IllegalArgumentException("Index " + index + " not found!");
    }

    SearchService searchService = new SearchService(index);
    List<Map<String, Object>> candidates = searchService.search(query, hits);

    Map<String, Object> queryMap = new LinkedHashMap<>();
    queryMap.put("query", new LinkedHashMap<>(Map.of("qid", qid, "text", query)));
    queryMap.put("candidates", candidates);

    return queryMap;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/index/{index}/cached")
  public boolean isCached(@PathVariable("index") String index) {
    if (!IndexInfo.contains(index)) {
      throw new IllegalArgumentException("Index name " + index + " not found!");
    }
    
    PrebuiltIndexHandler handler = new PrebuiltIndexHandler(index);
    handler.initialize();
    return handler.checkIndexFileExist();
  }

  @RequestMapping(method = RequestMethod.GET, path = "/indexes")
  public Map<String, Map<String, Object>> list() {
    IndexInfo[] indexes = IndexInfo.values();
    Map<String, Map<String, Object>> indexList = new LinkedHashMap<>();
    for (IndexInfo index : indexes) {
      indexList.put(index.indexName, Map.of(
        "indexName", index.indexName,
        "description", index.description,
        "filename", index.filename,
        "corpus", index.corpus,
        "model", index.model,
        "urls", index.urls,
        "md5", index.md5,
        "isCached", isCached(index.indexName)
      ));
    }
    return indexList;
  }

}