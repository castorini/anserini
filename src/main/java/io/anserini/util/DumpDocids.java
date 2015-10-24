package io.anserini.util;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

public class DumpDocids {

  public void readIndex(String indexDir, String docIdPath) throws IOException {
    FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
    DirectoryReader reader = DirectoryReader.open(dir);

    FileWriter fw = new FileWriter(new File(docIdPath));
    BufferedWriter bw = new BufferedWriter(fw);
    int len = reader.numDocs();
    for (int i = 0; i < len; i++) {
      String docName = reader.document(i).get("docname");
      bw.write(docName + "\n");
      // System.out.println("IndexCounter: " + i + " docs got");
      if ((i & 65535) == 0) {
        System.out.println("IndexCounter: " + i + " docs got");
      }
      // System.out.println(docName);

    }
    bw.close();
  }

  public static void main(String[] clArgs) {
    Args indexArgs = new Args();

    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties
        .defaults().withUsageWidth(90));

    try {
      parser.parseArgument(clArgs);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    final String indexDir = indexArgs.indexPath + "/index";
    final String docIdPath = indexArgs.docIdPath;

    System.out.println("Index path: " + indexDir);
    System.out.println("DocId path: " + docIdPath);
    final DumpDocids ic = new DumpDocids();
    try {
      ic.readIndex(indexDir, docIdPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
