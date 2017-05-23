package io.anserini.search;

import io.anserini.index.generator.LuceneFreebaseEntityDocumentGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.CheckHits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Search an indexed FreebaseEntity collection.
 *
 */
public class SearchFreebaseEntity implements Closeable {
  private static final Logger LOG = LogManager.getLogger(SearchFreebaseEntity.class);

  private final IndexReader reader;

  static final class Args {
    // Required arguments

    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    public String index;

    @Option(name = "-entity", metaVar = "[Entity URI]", required =  true, usage = "entity subject URI")
    public String entity;

    // Optional arguments

    @Option(name = "-title", metaVar = "[title name]", usage = "title to search")
    String title;

    @Option(name = "-text", metaVar = "[text name]", usage = "text to search")
    String text;
  }

  private SearchFreebaseEntity(String indexDir) throws IOException {
    // Initialize index reader
    LOG.info("Reading index from " + indexDir);

    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  /**
   * Prints query results to the standard output stream.
   *
   * @param entity the entity ID to search
   * @param title the title to search
   * @param text the text field to search
   * @throws Exception on error
   */
  public void search(String entity, String title, String text) throws Exception {
    LOG.info("Querying started...");

    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);

    // Search for exact subject URI
    TermQuery query = new TermQuery(new Term(LuceneFreebaseEntityDocumentGenerator.FIELD_ENTITY, entity));

    // Collect all matching lucene document ids
    Set<Integer> matchingDocIds = new HashSet<>(5);
    searcher.search(query, new CheckHits.SetCollector(matchingDocIds));

    if (matchingDocIds.size() == 0) { // We couldn't find any matching documents
      String msg = "Cannot find subject: " + query;
      LOG.warn(msg);
    } else {
      // Retrieve and print documents
      matchingDocIds.forEach(luceneDocId -> {
        try {
          Document doc = reader.document(luceneDocId);
          doc.iterator().forEachRemaining(field -> {
            String fieldMessage = field.name() + "\t:\t " + field.stringValue();
            LOG.info(fieldMessage);
          });
        } catch (IOException e) {
          LOG.error("Error retrieving lucene document: {}", luceneDocId, e);
        }
      });
    }

    LOG.info("Querying completed.");
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();

    // Parse args
    CmdLineParser parser = new CmdLineParser(searchArgs,
            ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example command: "+ SearchFreebaseEntity.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new SearchFreebaseEntity(searchArgs.index).search(searchArgs.entity, searchArgs.title, searchArgs.text);
  }
}
