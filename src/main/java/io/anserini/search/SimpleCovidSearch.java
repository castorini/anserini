package io.anserini.search;

import io.anserini.search.query.Covid19QueryGenerator;

public class SimpleCovidSearch {
  public static void main(String[] args) throws Exception {
    SimpleSearcher searcher = new SimpleSearcher("covid-2020-04-10/lucene-index-covid-2020-04-10");
    //SimpleSearcher.Result[] hits = searcher.search("incubation period covid-19");
    SimpleSearcher.Result[] hits = searcher.search(new Covid19QueryGenerator(), "incubation period", 10);

    for (int i=0; i<hits.length; i++) {
      System.out.println(String.format("%d %s %s", (i+1), hits[i].docid, hits[i].lucene_document.get("title")));
    }

    System.out.println("\n\n");
    hits = searcher.search("incubation period covid-19");

    for (int i=0; i<hits.length; i++) {
      System.out.println(String.format("%d %s %s", (i+1), hits[i].docid, hits[i].lucene_document.get("title")));
    }
  }
}
