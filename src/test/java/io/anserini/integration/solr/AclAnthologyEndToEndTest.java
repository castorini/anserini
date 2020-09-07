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

import io.anserini.collection.AclAnthology;
import io.anserini.index.IndexArgs;
import io.anserini.index.generator.AclAnthologyGenerator;
import io.anserini.search.SearchSolr;

public class AclAnthologyEndToEndTest extends SolrEndToEndTest {
  @Override
  protected String getCollectionName() {
    return "AclAnthology";
  }

  @Override
  protected String getSchemaAdjustmentFile() {
    return "solr/schemas/acl-anthology.json";
  }

  @Override
  public IndexArgs getIndexArgs() {
    IndexArgs indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/acl";
    indexArgs.collectionClass = AclAnthology.class.getSimpleName();
    indexArgs.generatorClass = AclAnthologyGenerator.class.getSimpleName();
    return indexArgs;
  }

  @Override
  protected SearchSolr.Args getSearchArgs() {
    return createSearchArgs("TsvInt", "src/test/resources/sample_topics/acl_topics.tsv");
  }

  @Override
  protected String[] getRefRankingResult() {
    return new String[]{ // bm25
        "1 Q0 C00-1007 1 0.294000 Solrini",
        "1 Q0 E17-1003 2 0.186100 Solrini",
        "2 Q0 C00-1003 1 0.622700 Solrini"
    };
  }
}
