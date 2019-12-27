/*
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

package io.anserini.rerank;

import io.anserini.search.SearchArgs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.List;

public class RerankerContext<K> {
  private final IndexSearcher searcher;
  private final Query query;
  private final K queryId;
  private final String queryDocId; // this is for News Track Background Linking task
  private final String queryText;
  private final List<String> queryTokens;
  private final Query filter;
  private final SearchArgs searchArgs;

  public RerankerContext(IndexSearcher searcher, K queryId, Query query, String queryDocId, String queryText,
      List<String> queryTokens, Query filter, SearchArgs searchArgs) throws IOException {
    this.searcher = searcher;
    this.query = query;
    this.queryId = queryId;
    this.queryDocId = queryDocId;
    this.queryText = queryText;
    this.queryTokens = queryTokens;
    this.filter = filter;
    this.searchArgs = searchArgs;
  }

  public IndexSearcher getIndexSearcher() {
    return searcher;
  }

  public Query getFilter() {
    return filter;
  }

  public Query getQuery() {
    return query;
  }

  public K getQueryId() {
    return queryId;
  }
  
  public String getQueryDocId() {
    return queryDocId;
  }
  
  public String getQueryText() {
    return queryText;
  }

  public List<String> getQueryTokens() {
    return queryTokens;
  }

  public SearchArgs getSearchArgs() {
    return searchArgs;
  }
}
