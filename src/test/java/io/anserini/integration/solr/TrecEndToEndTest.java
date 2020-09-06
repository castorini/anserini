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

package io.anserini.integration.solr;

import io.anserini.collection.TrecCollection;
import io.anserini.index.IndexArgs;
import io.anserini.search.SearchSolr;

public class TrecEndToEndTest extends SolrEndToEndTest {
  @Override
  protected String getCollectionName() {
    return "Trec";
  }

  @Override
  protected String getSchemaAdjustmentFile() {
    return null; // no need to adjust schema
  }

  @Override
  protected IndexArgs getIndexArgs() {
    IndexArgs indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/trec/collection2";
    indexArgs.collectionClass = TrecCollection.class.getSimpleName();
    return indexArgs;
  }

  @Override
  protected SearchSolr.Args getSearchArgs() {
    return createSearchArgs("Trec", "src/test/resources/sample_topics/Trec");
  }

  @Override
  protected String[] getRefRankingResult() {
    return new String[]{ // bm25
        "1 Q0 DOC222 1 0.343200 Solrini",
        "1 Q0 TREC_DOC_1 2 0.333400 Solrini",
        "1 Q0 WSJ_1 3 0.068700 Solrini"
    };
  }
}
