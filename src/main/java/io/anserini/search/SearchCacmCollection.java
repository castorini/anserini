package io.anserini.search;

import io.anserini.index.IndexCacmRecords;
import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.rm3.Rm3Reranker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Executable for running queries on the cacm collection
 */
public class SearchCacmCollection {

  private static final Logger LOG = LogManager.getLogger(SearchCacmCollection.class);

  private static final String QUERY_TAG = "query";
  private static final String ID_ATTR = "id";

  public static SortedMap<String,String> fromFile(String filePath) throws IOException, SAXException {
    File xmlFile = new File(filePath);
    SortedMap<String, String> queryMap = new TreeMap<>();
    DocumentBuilder builder;
    try {
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      return null;
    }

    Document xmlDoc = builder.parse(xmlFile);
    NodeList queries = xmlDoc.getElementsByTagName(QUERY_TAG);
    for (int i =0; i < queries.getLength(); i ++) {
      Element query = (Element) queries.item(i);
      String text  = query.getTextContent();
      queryMap.put(query.getAttribute(ID_ATTR), text);
      LOG.debug(String.format("%s %s", query.getAttribute(ID_ATTR), text));

    }
    LOG.debug(String.format("%d toppics found", queryMap.size()));
    return queryMap;
  }

  public static void main(String[] args) throws IOException, SAXException, ParseException {
    SearchArgs searchArgs = new SearchArgs();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults());

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SearchWebCollection" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }
    Directory indexDirectory;
    if (searchArgs.inmem) {
      indexDirectory= new MMapDirectory(Paths.get(searchArgs.index));
      ((MMapDirectory) indexDirectory).setPreload(true);
    } else {
      indexDirectory = FSDirectory.open(Paths.get(searchArgs.index));
    }
    Similarity similarity = null;
    if (searchArgs.ql) {
      LOG.info("Using QL scoring model");
      similarity = new LMDirichletSimilarity(searchArgs.mu);
    } else if (searchArgs.bm25) {
      LOG.info("Using BM25 scoring model");
      similarity = new BM25Similarity(searchArgs.k1, searchArgs.b);
    } else {
      LOG.error("Error: Must specify scoring model!");
      System.exit(-1);
    }

    RerankerCascade cascade = new RerankerCascade();
    if (searchArgs.rm3) {
      cascade.add(new Rm3Reranker(new EnglishAnalyzer(), "body", "src/main/resources/io/anserini/rerank/rm3/rm3-stoplist.gov2.txt"));
    } else {
      cascade.add(new IdentityReranker());
    }
    SortedMap<String, String> topics = fromFile(searchArgs.topics);
    searchCollection(topics, searchArgs.output, similarity, searchArgs.hits, indexDirectory);

  }

  public static void searchCollection(SortedMap<String,String> topics, String submissionFile,
                                      Similarity similarity, int numHits, Directory indexDirectory) throws IOException, ParseException {

    IndexReader reader = DirectoryReader.open(indexDirectory);
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);

    final String runTag = "BM25_EnglishAnalyzeSr_" + IndexCacmRecords.FIELD_TITLE + "_" + similarity.toString();

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(submissionFile), StandardCharsets.US_ASCII));
    QueryParser queryParser = new QueryParser(IndexCacmRecords.FIELD_TITLE, IndexCacmRecords.ANALYZER);
    queryParser.setDefaultOperator(QueryParser.Operator.OR);

    for (Map.Entry<String,String> entry : topics.entrySet()) {
      String queryId = entry.getKey();
      Query query = queryParser.parse(QueryParser.escape(entry.getValue()));

      ScoreDoc[] hits = searcher.search(query, numHits).scoreDocs;

      LOG.debug(String.format("%d hits found for query %s", hits.length, queryId));
      for (int i = 0; i < hits.length; i++) {
        ScoreDoc hit = hits[i];
        int docId = hit.doc;
        org.apache.lucene.document.Document doc = searcher.doc(docId);
        out.print(queryId);
        out.print("\tQ0\t");
        out.print(doc.get(IndexCacmRecords.FIELD_INDEX));
        out.print("\t");
        out.print(i + 1);
        out.print("\t");
        out.print(hit.score);
        out.print("\t");
        out.print(runTag);
        out.println();
      }
    }
    out.flush();
    out.close();
  }
}