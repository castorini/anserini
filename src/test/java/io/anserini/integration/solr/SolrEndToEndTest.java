package io.anserini.integration.solr;

import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import io.anserini.search.SearchSolr;
import org.apache.commons.io.FileUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.solr.EmbeddedSolrServerTestBase;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.json.DirectJsonQueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CommonParams;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.nio.file.Files;

public abstract class SolrEndToEndTest extends EmbeddedSolrServerTestBase {
  protected ObjectPool<SolrClient> stubSolrPool;
  protected final String searchOutputPrefix = "e2eTestSearch";

  protected IndexArgs createDefaultIndexArgs() {
    IndexArgs args = new IndexArgs();

    args.solrIndex = DEFAULT_CORE_NAME;
    args.threads = 1;
    args.storePositions = true;
    args.storeDocvectors = true;
    args.storeContents = true;
    args.storeRaw = true;
    args.optimize = true;
    args.quiet = true;
    args.solr = true;

    return args;
  }

  protected SearchSolr.Args createSearchArgs(String topicReader, String topicFile) {
    SearchSolr.Args args = new SearchSolr.Args();

    args.solrIndex = DEFAULT_CORE_NAME;
    args.output = this.searchOutputPrefix + topicReader;
    args.topicReader = topicReader;
    args.topics = new String[]{topicFile};
    args.zkUrl = "localhost"; // SearchSolr initialization workaround

    return args;
  }

  protected class StubSolrClientFactory extends BasePooledObjectFactory<SolrClient> {
    @Override
    public SolrClient create() {
      return getSolrClient();
    }

    @Override
    public PooledObject<SolrClient> wrap(SolrClient solrClient) {
      return new DefaultPooledObject<>(solrClient);
    }

    @Override
    public void destroyObject(PooledObject<SolrClient> pooled) throws Exception {
      pooled.getObject().close();
    }
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    File testHome = createTempDir().toFile();
    String collectionPath = testHome.getAbsolutePath() + File.separator + DEFAULT_CORE_NAME;
    FileUtils.copyDirectory(getFile("solr/anserini"), new File(collectionPath));
    initCore("solrconfig.xml", "managed-schema", testHome.getAbsolutePath(), DEFAULT_CORE_NAME);
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    clearIndex();
    assertU(commit());
    assertU(optimize());

    GenericObjectPoolConfig<SolrClient> config = new GenericObjectPoolConfig<>();
    config.setMaxTotal(1); // only 1 EmbeddedSolrServer instance will be created by getSolrClient
    config.setMinIdle(1);
    this.stubSolrPool = new GenericObjectPool<>(new StubSolrClientFactory(), config);
  }

  protected IndexCollection getIndexRunner(IndexArgs args) throws Exception {
    IndexCollection runner = new IndexCollection(args);
    Field f = runner.getClass().getDeclaredField("solrPool");
    f.setAccessible(true);
    f.set(runner, this.stubSolrPool);
    return runner;
  }

  protected SearchSolr getSearchRunner(SearchSolr.Args args) throws Exception {
    SearchSolr runner = new SearchSolr(args);
    Field f = runner.getClass().getDeclaredField("client");
    f.setAccessible(true);
    ((SolrClient) f.get(runner)).close(); // close the old client
    f.set(runner, getSolrClient());
    return runner;
  }

  protected abstract String getSchemaAdjustmentFile();

  protected abstract IndexArgs getIndexArgs();

  protected abstract SearchSolr.Args getSearchArgs();

  protected abstract String[] getRefRankingResult();

  @Test
  public void testIndexAndSearch() throws Exception {
    String schemaAdjustmentFile = getSchemaAdjustmentFile();
    if (schemaAdjustmentFile != null) {
      // update schema, much like curl -X POST -H 'Content-type:application/json' --data-binary SCHEMA_NAME.json http://localhost:8983/solr/COLLECTION_NAME/schema
      String schemaJson = Files.readString(getFile(schemaAdjustmentFile).toPath());
      DirectJsonQueryRequest schemaRequest = new DirectJsonQueryRequest(schemaJson, params(CommonParams.QT, "/schema"));
      QueryResponse response = schemaRequest.process(getSolrClient(), DEFAULT_CORE_NAME);
      assertEquals(0, response.getStatus());
    }

    IndexArgs indexArgs = getIndexArgs();
    IndexCollection indexRunner = getIndexRunner(indexArgs);
    indexRunner.run();
    this.stubSolrPool.close();

    SearchSolr.Args searchArgs = getSearchArgs();
    SearchSolr searchRunner = getSearchRunner(searchArgs);
    searchRunner.runTopics();

    BufferedReader br = new BufferedReader(new FileReader(searchArgs.output));
    String[] ref = getRefRankingResult();
    String s;
    int cnt = 0;
    while ((s = br.readLine()) != null) {
      assertEquals(ref[cnt], s);
      cnt++;
    }
    assertEquals(cnt, ref.length);
    FileUtils.deleteQuietly(new File(searchArgs.output));
  }
}
