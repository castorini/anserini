package io.anserini.search;

import org.kohsuke.args4j.Option;

public class SearchArgs {

  // required arguments
  @Option(name = "-index", metaVar = "[Path]", required = true, usage = "Lucene index")
  String index;

  @Option(name = "-topics", metaVar = "[File]", required = true, usage = "topics file")
  String topics;

  @Option(name = "-output", metaVar = "[File]", required = true, usage = "output file")
  String output;

  // optional arguments

  @Option(name = "-inmem", usage = "load index completely in memory")
  boolean inmem = false;

  @Option(name = "-ql", usage = "use query likelihood scoring model")
  boolean ql = false;

  @Option(name = "-bm25", usage = "use BM25 scoring model")
  boolean bm25 = false;

  @Option(name = "-rm3", usage = "use RM3 query expansion model (implies using query likelihood)")
  boolean rm3 = false;
  
}