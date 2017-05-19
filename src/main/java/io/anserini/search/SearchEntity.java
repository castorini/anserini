package io.anserini.search;

import io.anserini.index.generator.LuceneRDFDocumentGenerator;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Search an indexed RDF collection for the entity name.
 *
 * This should simulate some conjunctive queries that
 * can be expressed using SPARQL.
 */
public class SearchEntity implements Closeable {
  private static final Logger LOG = LogManager.getLogger(SearchEntity.class);
  private static final Pattern FREEBASE_PATTERN = Pattern.compile("www\\.freebase\\.com/(.*)");

  private final IndexReader reader;

  static final class Args {
    // Required arguments

    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    public String index;

    @Option(name = "-data", metaVar = "[Path]", required = true, usage = "path to the dataset")
    public String data;
  }

  private SearchEntity(String indexDir) throws IOException {
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
   * @param dataPath the path to the dataset file
   * @throws Exception on error
   */
  public void searchFile(String dataPath) throws Exception {
    LOG.info("Reading dataset from " + dataPath);
    Path filePath = Paths.get(dataPath);

    if (!Files.exists(filePath) || !Files.isDirectory(filePath) || !Files.isReadable(filePath)) {
      throw new IllegalArgumentException(dataPath + " does not exist or is not a directory.");
    }

    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);

    // Open the file
    FileInputStream fstream = new FileInputStream(dataPath);
    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
    String strLine;
    int lineNumber = 1;
    //Read File Line By Line
    while ((strLine = br.readLine()) != null) {
      // process each line - Subject-entity [tab] relationship [tab] Object-entity [tab] question
      String[] items = strLine.split("\t");
      String subjectEntity = items[0]; // www.freebase.com/m/06w1vyz
      String relationship = items[1];
      String objectEntity = items[2];
      String question = items[3];

      String subjectMid = FREEBASE_PATTERN.matcher(subjectEntity).group(1).replaceAll("/", "."); // m.06w1vyz
      String subject = "www.freebase.com/" + subjectMid; // www.freebase.com/m.06w1vyz
      String predicate = "http://www.w3.org/2000/01/rdf-schema#label";

      // Search for exact subject URI
      TermQuery query = new TermQuery(new Term(LuceneRDFDocumentGenerator.FIELD_SUBJECT, subject));

      // Collect all matching lucene document ids
      Set<Integer> matchingDocIds = new HashSet<>(5);
      searcher.search(query, new CheckHits.SetCollector(matchingDocIds));

      if (matchingDocIds.size() == 0) { // We couldn't find any matching documents
        String msg = "Cannot find subject: " + query + ", at line number: " + lineNumber;
        LOG.warn(msg);
      }
//      else {
//        // Retrieve and print documents
//        matchingDocIds.forEach(luceneDocId -> {
//          try {
//            Document doc = reader.document(luceneDocId);
//            doc.iterator().forEachRemaining(field -> {
//              if (predicate == null || field.name().equals(predicate)) {
//                String fieldMessage = field.name() + "\t:\t " + field.stringValue();
//                LOG.info(fieldMessage);
//              }
//            });
//          }
//          catch (IOException e) {
//            LOG.error("Error retrieving lucene document: {}", luceneDocId, e);
//          }
//        });
//      }
      lineNumber++;
    }

    //Close the input stream
    br.close();
    LOG.info("Done.");
  }

    /**
     * Prints query results to the standard output stream.
     *
     * @param subject the subject to search
     * @param predicate the predicate to search, or null to print all properties
     * @throws Exception on error
     */
  public void search(String subject, String predicate) throws Exception {
    LOG.info("Querying started...");

    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);

    // Search for exact subject URI
    TermQuery query = new TermQuery(new Term(LuceneRDFDocumentGenerator.FIELD_SUBJECT, subject));

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
            if (predicate == null || field.name().equals(predicate)) {
              String fieldMessage = field.name() + "\t:\t " + field.stringValue();
              LOG.info(fieldMessage);
            }
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
      System.err.println("Example command: "+ SearchEntity.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new SearchEntity(searchArgs.index).searchFile(searchArgs.data);
  }
}
