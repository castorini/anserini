package io.anserini.py4j;

import io.anserini.search.SearchWebCollection;
import py4j.GatewayServer;

import java.io.IOException;

/**
 * @author s43moham on 06/03/17.
 * @project anserini
 */
public class SearchWebCollectionEntryPoint {

    private SearchWebCollection searcher;

    public SearchWebCollectionEntryPoint() throws IOException {
        String index = "/home/s43moham/indexes/lucene-index.TrecQA.pos+docvectors+rawdocs/";
        searcher = new SearchWebCollection(index);
    }

    public SearchWebCollection getSearcher() {
        return searcher;
    }

    public static void main(String[] args) throws IOException {
        GatewayServer gatewayServer = new GatewayServer(new SearchWebCollectionEntryPoint());
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }
}
