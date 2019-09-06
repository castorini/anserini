package io.anserini.util;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.SmallFloat;
import org.kohsuke.args4j.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

public class ExtractDocumentLengths {

  public static class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index")
    String index;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
    String output;
  }

  public static void main(String[] args) throws Exception {
    Args myArgs = new Args();
    CmdLineParser parser = new CmdLineParser(myArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    Directory dir = FSDirectory.open(Paths.get(myArgs.index));
    IndexReader reader = DirectoryReader.open(dir);

    PrintStream out = new PrintStream(new FileOutputStream(new File(myArgs.output)));

    int numDocs = reader.numDocs();
    out.println("luceneID\tcount\tuniquecount\tlossycount");
    for (int i = 0; i < numDocs; i++) {
      int total = 0;
      Terms terms = reader.getTermVector(i, "contents");
      if(terms == null) {
        out.println(i + "\t" + 0 + "\t" + 0 + "\t" + 0);
        continue;
      }
      TermsEnum termsEnum = terms.iterator();
      while ((termsEnum.next()) != null) {
        total += termsEnum.totalTermFreq();
      }
      long length = SmallFloat.longToInt4(terms.size());
      out.println(i + "\t" + total + "\t" + terms.size() + "\t" + length) ;
    }
    out.close();
  }
}
