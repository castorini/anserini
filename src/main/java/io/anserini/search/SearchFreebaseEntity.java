package io.anserini.search;

import io.anserini.index.generator.LuceneFreebaseEntityDocumentGenerator;
import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.AnalyzerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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


    @Option(name = "-query", metaVar = "[Query]", required =  true, usage = "entity name to query for")
    public String query; // entity name to search for
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
   * @param queryName the entity name to search
   * @throws Exception on error
   */
  public void search(String queryName) throws Exception {
    LOG.info("Querying started...");

    // Initialize index searcher
    Map<String, Float> scoredDocs = new LinkedHashMap<>();
    IndexSearcher searcher = new IndexSearcher(reader);
    float k1 = 1.5f;
    float b = 0.75f;
    Similarity similarity = new BM25Similarity(k1, b);
    searcher.setSimilarity(similarity);
    SimpleAnalyzer analyzer = new SimpleAnalyzer();
    MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                    new String[]{ LuceneFreebaseEntityDocumentGenerator.FIELD_TITLE,
                          LuceneFreebaseEntityDocumentGenerator.FIELD_LABEL,
                          LuceneFreebaseEntityDocumentGenerator.FIELD_TEXT },
                    analyzer);
    queryParser.setDefaultOperator(QueryParser.Operator.OR);
    Query query = queryParser.parse(queryName);
    RerankerCascade cascade = new RerankerCascade();
    cascade.add(new IdentityReranker());

    int numHits = 20;
    TopDocs rs = searcher.search(query, numHits);
    ScoreDoc[] hits = rs.scoreDocs;
    List<String> queryTokens = AnalyzerUtils.tokenize(analyzer, queryName);
    RerankerContext context = new RerankerContext(searcher, query, String.valueOf(1), queryName,
            queryTokens, LuceneFreebaseEntityDocumentGenerator.FIELD_LABEL, null);
    ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

    for (int i = 0; i < docs.documents.length; i++) {
      System.out.println(String.format("%d: %s %f", (i + 1),
              docs.documents[i].getField(LuceneFreebaseEntityDocumentGenerator.FIELD_ENTITY).stringValue(),
              docs.scores[i]));
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

    new SearchFreebaseEntity(searchArgs.index).search(searchArgs.query);
  }
}
