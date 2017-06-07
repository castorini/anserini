package io.anserini.kg.freebase;

import io.anserini.kg.freebase.IndexTopics.TopicLuceneDocumentGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import io.anserini.rerank.ScoredDocuments;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Lookups a Freebase mid and returns all properties associated with it.
 */
public class LookupTopic implements Closeable {
  private static final Logger LOG = LogManager.getLogger(LookupTopic.class);

  private final IndexReader reader;

  static final class Args {
    // Required arguments

    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    public String index;

    @Option(name = "-query", metaVar = "[query]", required =  true, usage = "topic name to query for")
    public String query;
  }

  private LookupTopic(String indexDir) throws IOException {
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
   * @param queryName the entity name to search
   * @throws Exception on error
   */
  public void search(String queryName) throws Exception {
    LOG.info("Querying started...");

    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);
    SimpleAnalyzer analyzer = new SimpleAnalyzer();
    int numHits = 20;

    // find exact title
    QueryParser titleParser = new QueryParser(TopicLuceneDocumentGenerator.FIELD_TITLE, analyzer);
    Query titleQuery = titleParser.parse(queryName);
    TopDocs rs = searcher.search(titleQuery, numHits);
    ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

    for (int i = 0; i < docs.documents.length; i++) {
      String resultDoc = String.format("%d - SCORE: %f\nTOPIC_MID: %s\nWIKI_TITLE: %s\nW3_LABEL: %s\n\n",
              (i + 1),
              docs.scores[i],
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_TOPIC_MID).stringValue(),
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_TITLE).stringValue(),
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_LABEL).stringValue());
      System.out.println(resultDoc);
    }

    if (docs.documents.length != 0) {
      System.out.println("Exact WIKI_TITLE found! Ending search.");
      return;
    } else {
      System.out.println("Exact WIKI_TITLE not found. Searching for the label...");
    }
    System.out.println();

    // find exact label
    QueryParser labelParser = new QueryParser(TopicLuceneDocumentGenerator.FIELD_LABEL, analyzer);
    Query labelQuery = labelParser.parse(queryName);
    rs = searcher.search(labelQuery, numHits);
    docs = ScoredDocuments.fromTopDocs(rs, searcher);

    for (int i = 0; i < docs.documents.length; i++) {
      String resultDoc = String.format("%d - SCORE: %f\nTOPIC_MID: %s\nWIKI_TITLE: %s\nW3_LABEL: %s\n\n",
              (i + 1),
              docs.scores[i],
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_TOPIC_MID).stringValue(),
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_TITLE).stringValue(),
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_LABEL).stringValue());
      System.out.println(resultDoc);
    }

    if (docs.documents.length != 0) {
      System.out.println("Exact W3_LABEL found! Ending search.");
      return;
    } else {
      System.out.println("Exact W3_LABEL not found. Ranking the topics using BM25 according the text/title/label...");
    }
    System.out.println();

    float k1 = 1.5f;
    float b = 0.75f;
    Similarity similarity = new BM25Similarity(k1, b);
    searcher.setSimilarity(similarity);
    MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
            new String[]{ TopicLuceneDocumentGenerator.FIELD_TITLE,
                    TopicLuceneDocumentGenerator.FIELD_LABEL,
                    TopicLuceneDocumentGenerator.FIELD_TEXT },
            analyzer);
    queryParser.setDefaultOperator(QueryParser.Operator.OR);
    Query query = queryParser.parse(queryName);

    rs = searcher.search(query, numHits);
    docs = ScoredDocuments.fromTopDocs(rs, searcher);

    for (int i = 0; i < docs.documents.length; i++) {
      String resultDoc = String.format("%d - SCORE: %f\nTOPIC_MID: %s\nWIKI_TITLE: %s\nW3_LABEL: %s\n",
              (i + 1),
              docs.scores[i],
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_TOPIC_MID).stringValue(),
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_TITLE).stringValue(),
              docs.documents[i].getField(TopicLuceneDocumentGenerator.FIELD_LABEL).stringValue() );
      System.out.println(resultDoc);
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
      System.err.println("Example command: "+ LookupTopic.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new LookupTopic(searchArgs.index).search(searchArgs.query);
  }
}
