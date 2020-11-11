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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.json.DirectJsonQueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.solr.SolrTestCaseJ4.params;

@LuceneTestCase.SuppressSysoutChecks(bugUrl = "None")
public abstract class SolrEndToEndTest extends LuceneTestCase {
  private static final Logger LOG = LogManager.getLogger(SolrEndToEndTest.class);

  protected ObjectPool<SolrClient> stubSolrPool;
  protected final String searchOutputPrefix = "e2eTestSearch";

  protected EmbeddedSolrServer client;

  protected static File getFile(String path) {
    final URL url = SolrEndToEndTest.class.getClassLoader().getResource(path);
    if (url != null) {
      try {
        return new File(url.toURI());
      } catch (Exception e) {
        throw new RuntimeException("Resource was found on classpath, but cannot be resolved to a normal file: " + path);
      }
    }
    final File file = new File(path);
    if (file.exists()) {
      return file;
    }
    throw new RuntimeException("Cannot find resource in classpath or in file-system (relative to CWD): " + path);
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    final File solrHome = createTempDir().toFile();
    final File configSetBaseDir = new File(solrHome.toPath() + File.separator + "configsets");
    FileUtils.copyDirectory(getFile("solr/anserini"), new File(configSetBaseDir + File.separator + "anserini"));

    SolrResourceLoader loader = new SolrResourceLoader(solrHome.toPath());
    NodeConfig config = new NodeConfig.NodeConfigBuilder("embeddedSolrServerNode", loader)
        .setConfigSetBaseDirectory(configSetBaseDir.getAbsolutePath()).build();
    client = new EmbeddedSolrServer(config, getCollectionName());
    LOG.info("Created Embedded Solr Server");

    CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
    createRequest.setCoreName(getCollectionName());
    createRequest.setConfigSet("anserini");
    createRequest.process(client);
    client.commit();
    LOG.info("Created Solr Core: " + getCollectionName());

    GenericObjectPoolConfig<SolrClient> poolConfig = new GenericObjectPoolConfig<>();
    poolConfig.setMaxTotal(1); // only 1 EmbeddedSolrServer instance will be created by getSolrClient
    poolConfig.setMinIdle(1);
    stubSolrPool = new GenericObjectPool<>(new StubSolrClientFactory(client), poolConfig);
  }

  @After
  @Override
  public void tearDown() throws Exception {
    super.tearDown();

    client.deleteByQuery("*:*");
    client.commit();
    client.close();
    stubSolrPool.close();
  }

  protected IndexArgs createDefaultIndexArgs() {
    IndexArgs args = new IndexArgs();

    args.solrIndex = getCollectionName();
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

    args.solrIndex = getCollectionName();
    args.output = searchOutputPrefix + topicReader;
    args.topicReader = topicReader;
    args.topics = new String[]{topicFile};
    args.zkUrl = "localhost"; // SearchSolr initialization workaround

    return args;
  }

  protected static class StubSolrClientFactory extends BasePooledObjectFactory<SolrClient> {
    final SolrClient client;

    public StubSolrClientFactory(SolrClient client) {
      this.client = client;
    }

    @Override
    public SolrClient create() {
      return this.client;
    }

    @Override
    public PooledObject<SolrClient> wrap(SolrClient solrClient) {
      return new DefaultPooledObject<>(solrClient);
    }
  }

  protected IndexCollection getIndexRunner(IndexArgs args) throws Exception {
    IndexCollection runner = new IndexCollection(args);
    Field f = runner.getClass().getDeclaredField("solrPool");
    f.setAccessible(true);
    f.set(runner, stubSolrPool);
    return runner;
  }

  protected SearchSolr getSearchRunner(SearchSolr.Args args) throws Exception {
    SearchSolr runner = new SearchSolr(args);
    Field f = runner.getClass().getDeclaredField("client");
    f.setAccessible(true);
    ((SolrClient) f.get(runner)).close(); // close the old client
    f.set(runner, client);
    return runner;
  }

  protected abstract String getCollectionName();

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
      QueryResponse response = schemaRequest.process(client, getCollectionName());
      assertEquals(0, response.getStatus());
    }

    IndexArgs indexArgs = getIndexArgs();
    IndexCollection indexRunner = getIndexRunner(indexArgs);
    indexRunner.run();

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
