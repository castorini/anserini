package io.anserini.util;

import org.kohsuke.args4j.Option;


public class Args {
  @Option(name = "-indexPath", metaVar = "[Path]", required = true, usage = "Directory contains index files")
  public String indexPath;
  
  @Option(name = "-docIdPath", metaVar = "[Path]", required = true, usage = "Path of dumped document ID list")
  public String docIdPath;

}
