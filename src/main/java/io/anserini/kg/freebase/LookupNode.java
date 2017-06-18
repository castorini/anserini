package io.anserini.kg.freebase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lookups a Freebase mid and returns all properties associated with it.
 */
public class LookupNode implements Closeable {
  private static final Logger LOG = LogManager.getLogger(LookupNode.class);

  private final IndexReader reader;

  static final class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    public Path index;

    @Option(name = "-mid", metaVar = "[mid]", required =  true, usage = "Freebase machine id")
    public String subject;
  }

  private LookupNode(Path indexPath) throws IOException {
    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexPath + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  /**
   * Prints all known facts about a particular mid.
   * @param mid subject mid
   * @throws Exception on error
   */
  public void search(String mid) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);

    // Search for exact subject URI
    TermQuery query = new TermQuery(new Term(IndexNodes.FIELD_ID, mid));

    TopDocs topDocs = searcher.search(query, 1);
    if (topDocs.totalHits == 0) {
      System.err.println("Error: mid not found!");
      System.exit(-1);
    }
    if (topDocs.totalHits > 1) {
      System.err.println("Error: more than one matching mid found. This shouldn't happen!");
      System.exit(-1);
    }

    Document doc = reader.document(topDocs.scoreDocs[0].doc);
    doc.forEach(field -> {
      System.out.println(field.name() + " = " + field.stringValue());
    });
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ LookupNode.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    LookupNode lookup = new LookupNode(searchArgs.index);
    lookup.search(searchArgs.subject);
    lookup.close();
  }
}
