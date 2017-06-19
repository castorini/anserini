package io.anserini.kg.freebase;

import io.anserini.rerank.ScoredDocuments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
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
public class LookupTopic implements Closeable {
  private static final Logger LOG = LogManager.getLogger(LookupNode.class);

  private final IndexReader reader;

  static final class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    public Path index;

    @Option(name = "-query", metaVar = "[query]", required =  true, usage = "topic name to query for")
    public String query;

    @Option(name="-hits", metaVar = "[hits]", usage = "number of search hits")
    private int numHits = 20;
  }

  private LookupTopic(Path indexPath) throws IOException {
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
   * @param queryName query topic name
   * @throws Exception on error
   */
  public void search(String queryName, int numHits) throws Exception {
    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);

    // search for query in multiple fields
    MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
            new String[]{ IndexTopics.FIELD_TITLE, IndexTopics.FIELD_LABEL, IndexTopics.FIELD_NAME },
            new SimpleAnalyzer());
    queryParser.setDefaultOperator(QueryParser.Operator.OR);
    Query query = queryParser.parse(queryName);

    TopDocs rs = searcher.search(query, numHits);
    ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

    for (int i = 0; i < docs.documents.length; i++) {
      String resultDoc = String.format("%d - SCORE: %f\nTOPIC_MID: %s\nOBJECT_NAME: %s\nWIKI_TITLE: %s\nW3_LABEL: %s\n",
              (i + 1),
              docs.scores[i],
              docs.documents[i].getField(IndexTopics.FIELD_TOPIC_MID).stringValue(),
              docs.documents[i].getField(IndexTopics.FIELD_NAME).stringValue(),
              docs.documents[i].getField(IndexTopics.FIELD_TITLE).stringValue(),
              docs.documents[i].getField(IndexTopics.FIELD_LABEL).stringValue() );
      System.out.println(resultDoc);
    }
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

    LOG.info(String.format("Index: %s", searchArgs.index));
    LOG.info(String.format("Query: %s", searchArgs.query));
    LOG.info(String.format("Hits: %s", searchArgs.numHits));

    LookupTopic lookup = new LookupTopic(searchArgs.index);
    lookup.search(searchArgs.query, searchArgs.numHits);
    lookup.close();
  }
}
