package io.anserini.util;

import io.anserini.index.IndexWebCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class DumpIndex {
  private static final Logger LOG = LogManager.getLogger(DumpIndex.class);

  String print_repository_stats( String indexDir ) throws IOException {
    FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
    DirectoryReader reader = DirectoryReader.open(dir);

    int docCount = reader.numDocs();
    int contentsCount = reader.getDocCount(IndexWebCollection.FIELD_BODY);
    long termCount = reader.getSumTotalTermFreq(IndexWebCollection.FIELD_BODY);

    StringBuilder sb = new StringBuilder();
    sb.append("Repository statistics:\n");
    sb.append("documents:\t" + docCount + "\n");
    sb.append("contentsCount(doc with contents):\t" + contentsCount + "\n");
    //sb.append("unique terms:\t" + uniqueTermCount);
    sb.append("total terms:\t" + termCount + "\n");
    sb.append("fields:\t\t" + IndexWebCollection.FIELD_ID + " " + IndexWebCollection.FIELD_BODY);
    sb.append("\n");

    return sb.toString();
  }

  boolean argsEnough(int n, int required) {
    if (n < required) {
      printUsage();
      return false;
    }
    return true;
  }

  void printUsage() {
    StringBuilder sb = new StringBuilder();
    sb.append("DumpIndex <repository> <command> [ <argument> ]*\n")
      .append("These commands retrieve data from the repository: \n")
      .append("    Command              Argument       Description\n")
      .append("    stats (s)                           Print statistics for the Repository\n");
    System.out.println(sb.toString());
  }

  public static void main(String[] clArgs) throws IOException{
    int argc = clArgs.length;
    final DumpIndex ic = new DumpIndex();
    try {
      String indexPath = clArgs[0];
      String command = clArgs[1];
      try {
        if (command.equals("s") || command.equals("stats")) {
          ic.argsEnough(2, argc);
          System.out.println(ic.print_repository_stats(indexPath));
        }
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } catch (IndexOutOfBoundsException e) {
      ic.printUsage();
    }
  }
}
