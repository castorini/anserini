package io.anserini.util;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

public class DumpDocids {
  private static final Logger LOG = LogManager.getLogger(DumpDocids.class);

  public static class Args {
    @Option(name = "-indexPath", metaVar = "[Path]", required = true, usage = "Directory contains index files")
    String indexPath;

    @Option(name = "-docIdPath", metaVar = "[Path]", required = true, usage = "Path of dumped document ID list")
    String docIdPath;

    @Option(name = "-docIdName", metaVar = "[String]", required = false, usage = "Field Name of document ID")
    String docId = "docname";
  }

  public class IDNameException extends Exception {
	

	public IDNameException(String message) {
		  super(message);
	  }
  }
		  
  public void readIndex(String indexDir, String docIdPath, String docId) throws IOException, IDNameException {
    FSDirectory dir = FSDirectory.open(new File(indexDir).toPath());
    DirectoryReader reader = DirectoryReader.open(dir);

    FileWriter fw = new FileWriter(new File(docIdPath));
    BufferedWriter bw = new BufferedWriter(fw);
    int len = reader.numDocs();
    if (len > 0) {
    	String docName = reader.document(0).get(docId);
    	if (docName == null) {
    		LOG.info(docId + " is a wrong document ID field name!");
    		bw.close();
    		throw new IDNameException(docId + " is a wrong document ID field name!");
    	}
    }
    else {
    	bw.close();
    	throw new IDNameException("No document is in the index!");
    }
    for (int i = 0; i < len; i++) {
      String docName = reader.document(i).get(docId);
      bw.write(docName + "\n");
      if ((i % 100000) == 0) {
        LOG.info("DumpDocids: " + i + " docs got");
      }
    }
    bw.close();
  }

  public static void main(String[] clArgs) throws IOException, IDNameException{
    Args indexArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties
        .defaults().withUsageWidth(90));
    try {
      parser.parseArgument(clArgs);
    } catch (CmdLineException e) {
      LOG.error(e.getMessage());
      parser.printUsage(System.err);
      return;
    }
    final DumpDocids ic = new DumpDocids();
    try {
      ic.readIndex(indexArgs.indexPath, indexArgs.docIdPath, indexArgs.docId);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    }
  }
}
