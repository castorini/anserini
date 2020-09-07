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

import io.anserini.collection.CoreCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.generator.CoreGenerator;
import io.anserini.search.SearchSolr;

public class CoreEndToEndTest extends SolrEndToEndTest {
  @Override
  protected String getCollectionName() {
    return "Core";
  }

  @Override
  protected String getSchemaAdjustmentFile() {
    return "solr/schemas/core.json";
  }

  @Override
  protected IndexArgs getIndexArgs() {
    IndexArgs indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/core";
    indexArgs.collectionClass = CoreCollection.class.getSimpleName();
    indexArgs.generatorClass = CoreGenerator.class.getSimpleName();
    return indexArgs;
  }

  @Override
  protected SearchSolr.Args getSearchArgs() {
    return createSearchArgs("TsvInt", "src/test/resources/sample_topics/core_topics.tsv");
  }

  @Override
  protected String[] getRefRankingResult() {
    return new String[]{ // bm25
        "1 Q0 coreDoc1 1 0.243200 Solrini",
        "1 Q0 doi2 2 0.243199 Solrini",
        "2 Q0 coreDoc1 1 0.243200 Solrini",
        "2 Q0 doi2 2 0.243199 Solrini",
        "3 Q0 fullCoreDoc 1 0.534600 Solrini"
    };
  }
}
