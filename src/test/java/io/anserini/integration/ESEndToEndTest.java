package io.anserini.integration;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.elasticsearch.test.ESIntegTestCase.Scope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/*
This test followed examples from the following tutorials, as there wasn't any official documentations on ES of 
how to write ES Integration tests:
https://www.hascode.com/2016/08/elasticsearch-integration-testing-with-java/
https://github.com/joel-costigliola/elastic-search-test/blob/master/src/test/java/es/example/test/integration/EsIntegrationTest.java
*/

@ClusterScope(scope = Scope.SUITE)
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
@RunWith(com.carrotsearch.randomizedtesting.RandomizedRunner.class)
public class ESEndToEndTest extends ESIntegTestCase {
    private static final Logger LOG = LogManager.getLogger(ESEndToEndTest.class);

    private static final String INDEX = "documentTestIndex";
    private static final String ID = "documentTestId";
    private Client client;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.client = client();
    }

    @Test
    public void testE2E() throws Exception {
        Map<String, Object> doc = getTestDocument();

        IndexRequest indexRequest = new IndexRequest(INDEX).id(ID).source(doc);
        IndexResponse response = client.index(indexRequest).actionGet();

        LOG.info("ESEndToEndTest: entry added to index '%s', doc-version: '%s', doc-id: '%s'\n",
                response.getIndex(), response.getVersion(), response.getId());

        // Check if the index has been added
        refresh();
        indexExists(INDEX);
        ensureGreen(INDEX);

        SearchResponse searchResponse = client.prepareSearch(INDEX)
                                        .setQuery(QueryBuilders.termsQuery("content", "hello"))
                                        .execute()
                                        .actionGet();
        SearchHits hits = searchResponse.getHits();
        assertEquals(hits.getTotalHits(), 1);

        searchResponse = client.prepareSearch(INDEX)
                                        .setQuery(QueryBuilders.termsQuery("title", "Cord19"))
                                        .execute()
                                        .actionGet();
        hits = searchResponse.getHits();
        assertEquals(hits.getTotalHits(), 2);

        searchResponse = client.prepareSearch(INDEX)
                                        .setQuery(QueryBuilders.termsQuery("title", "Covid19"))
                                        .execute()
                                        .actionGet();
        hits = searchResponse.getHits();
        assertEquals(hits.getTotalHits(), 0);
    }

    private Map<String, Object> getTestDocument() {
        Map<String, Object> doc = Map.of(
            "title", "this is Cord19",
            "content", "this is the content",
            "raw", "this is the raw",
            "title2", "this is also Cord19",
            "content", "hello");
        return doc; 
    } 
} 