package io.anserini.integration.solr;

import io.anserini.collection.TrecCollection;
import io.anserini.index.IndexArgs;
import io.anserini.search.SearchSolr;

public class TrecEndToEndTest extends SolrEndToEndTest {
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
