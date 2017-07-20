package io.anserini.kg.freebase;

import com.google.common.collect.MinMaxPriorityQueue;
import io.anserini.rerank.ScoredDocuments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.OptionHandlerFilter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * Lookups a Freebase mid and returns all properties associated with it.
 */
public class EntityLinking implements Closeable {
  private static final Logger LOG = LogManager.getLogger(EntityLinking.class);

  private final IndexReader reader;

  private static int found = 0;
  private static int notfound = 0;

  private static final String FREEBASE_URI = "www.freebase.com/";
  private static final String RDF_FREEBASE_URI = "http://rdf.freebase.com/ns/";
  private static final String FREEBASE_SHORT_URI = "fb:";

  static final class Args {
    // Required arguments

    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    public String index;

    @Option(name = "-data", metaVar = "[file]", required =  true, usage = "dataset file")
    public String data;

    @Option(name = "-output", metaVar = "[file]", required =  true, usage = "output file")
    public String output;

    @Option(name = "-goldData", metaVar = "goldData", usage = "boolean flag - supplying with the ground truth dataset")
    boolean goldData;

    @Option(name = "-hits", metaVar = "hits", usage = "setting number of hits to be retrieved per query")
    int hits = 5;
  }

  class RankedEntity implements Comparable<RankedEntity> {
    String mid;
    float score;
    String name;
    String label;

    RankedEntity(String m, float s, String n, String l) {
      mid = m;
      score = s;
      name = n;
      label = l;
    }

    @Override
    public String toString() {
        return String.format("(%s, %f)", mid, score);
    }

    @Override
    public int compareTo(RankedEntity o) {
      return Float.compare(score, o.score);
    }

    @Override
    public boolean equals(Object other) {
      boolean result = false;
      if (other instanceof RankedEntity) {
        RankedEntity that = (RankedEntity) other;
        result = that.mid.equals(this.mid);
      }
      return result;
    }
  }

  private EntityLinking(String indexDir) throws IOException {
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

  public void bruteSearchFile(String fileName, int numHits, String outName) throws Exception {
    LOG.info("fileName: " + fileName);

    // Open the file
    FileInputStream fstream = new FileInputStream(fileName);

    // Open the file for writing
    BufferedWriter bw = new BufferedWriter(new FileWriter(outName));

    try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
      String strLine;

      // Read file line by line
      int index = -1;
      while ((strLine = br.readLine()) != null)   {
        MinMaxPriorityQueue<RankedEntity> rankScoresHeap = MinMaxPriorityQueue.orderedBy(
                new Comparator<RankedEntity>() {
                  @Override
                  public int compare(RankedEntity e1, RankedEntity e2) {
                    return (e1.score > e2.score) ? -1 : (e1.score < e2.score) ? 1 : 0;
                  }
                })
                .maximumSize(numHits)
                .create();
        String[] lineItems = strLine.split(" %%%% ");
        if (lineItems.length < 6) {
          LOG.info(String.format("numbered SHORT LINE < 6 items! line: %s", strLine));
          continue;
        }
        String lineId = lineItems[0].trim();
        String subject = lineItems[1].trim();
        String shortMid = getShortMid(cleanUri(subject));
        String questionText = lineItems[4].trim().toLowerCase();
        List<String> queries = getSearchQueries(questionText);
        for(String query : queries) {
          try {
            List<RankedEntity> rankScores = exactQuerySearch(query, numHits);
            rankScoresHeap.addAll(rankScores);
          } catch (Exception e) {
            LOG.info(String.format("query: %s,\tlineId: %s,\tline: %s", query, lineId, strLine));
            notfound += 1;
            continue;
          }
        }

        bw.write(String.format("%s %%%% %s %%%% %s\n", lineId, questionText, shortMid));
        RankedEntity entityMidToCompare = new RankedEntity(shortMid, 0.0f, "", "");
        if (rankScoresHeap.contains(entityMidToCompare)) {
          found += 1;
          index = Arrays.asList(rankScoresHeap.toArray()).indexOf(entityMidToCompare);
          LOG.info("found at index: " + index);
          bw.write(String.format("FOUND at index: %d\n", index));
        } else {
          notfound += 1;
          LOG.info(String.format("NOT found,\tline: %s", strLine));
          bw.write(String.format("NOT found\n"));
        }

        for (RankedEntity re : rankScoresHeap) {
          bw.write(String.format("%s %%%% %s %%%% %.5f\n", re.mid, re.name, re.score));
        }
        bw.write("------------------------------------------------------------------------------------\n");
      }

      bw.close();
    }

    LOG.info("Found: " + found);
    LOG.info("Not Found: " + notfound);
    double percent = (found * 100.0) / (found + notfound);
    LOG.info("Found/Total %: " + percent);
    LOG.info("Querying completed.");
  }

  public void searchGoldFile(String fileName, int numHits, String outName) throws Exception {
    LOG.info("fileName: " + fileName);
    LOG.info("numHits: " + numHits);

    // Open the file for writing
    BufferedWriter bw = new BufferedWriter(new FileWriter(outName));

    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
      String strLine;

      // Read file line by line
      List<RankedEntity> rankedEntities;
      while ((strLine = br.readLine()) != null) {
        // Please refer to the entity linking dataset creation script - the fields are separated by this delimiter
        String[] lineItems = strLine.split(" %%%% ");
        if (lineItems.length < 6) {
          LOG.info(String.format("numbered SHORT LINE < 6 items! line: %s", strLine));
          continue;
        }
        String lineId = lineItems[0].trim();
        String subject = lineItems[1].trim();
        String shortMid = getShortMid(cleanUri(subject));
        String relation = lineItems[2].trim();
        String object = lineItems[3].trim();
        String questionText = lineItems[4].trim();
        // Please refer to the entity linking dataset creation script - the query field is separated by this delimiter
        String[] queries = lineItems[5].trim().split(" &&&& ");
        boolean isFound = false;
        String queryFound = "";
        int index = -1;
        for (String query : queries) {
          if (!isFound) {
            query = removeQuotes(query.trim().toLowerCase());
            try {
              rankedEntities = search(query, numHits);
            } catch (Exception e) {
              // only keep the alphabets in the query
              String queryStripped = query.replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", " ");
              if (queryStripped.trim().length() > 0) {
                rankedEntities = search(queryStripped, numHits);
              } else {
                LOG.info(String.format("WEIRD! query: %s", query, index));
                continue;
              }
            }

            RankedEntity entityMidToCompare = new RankedEntity(shortMid, 0.0f, "", "");
            if (rankedEntities.contains(entityMidToCompare)) {
              isFound = true;
              index = rankedEntities.indexOf(entityMidToCompare);
              queryFound = query;
              // write to file
              for (RankedEntity re : rankedEntities) {
                bw.write(String.format("%s %%%% %s %%%% %s %%%% %.5f %%%% %s %%%% %s %%%% %s\n",
                                lineId, re.mid, re.name, re.score, relation, object, questionText));
              }
              break;
            }
          }
        }
        if (isFound) {
          found += 1;
          LOG.info(String.format("query found: %s,\tfound at index: %d", queryFound, index));
        } else {
          notfound += 1;
          LOG.info(String.format("queries: %s,\tNOT found", Arrays.asList(queries), index));
        }
      }
    }

    LOG.info("Found: " + found);
    LOG.info("Not Found: " + notfound);
    double percent = (found * 100.0) / (found + notfound);
    LOG.info("Found/Total %: " + percent);
    LOG.info("Querying completed.");
  }

  /**
   * Returns a list of query results.
   *
   * @param queryName the entity name to search
   * @throws Exception on error
   * @return a list of top ranked entities
   */
  public List<RankedEntity> exactQuerySearch(String queryName, int numHits) throws Exception {
    List<RankedEntity> rankedEntities = new ArrayList<>();

    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);

    // do exact search on query name
    QueryParser queryParser = new QueryParser(IndexTopics.FIELD_NAME, new SimpleAnalyzer());
    queryParser.setAutoGeneratePhraseQueries(true);
    queryParser.setPhraseSlop(3);
    queryName = "\"" + queryName + "\"";
    Query query = queryParser.parse(queryName);
    TopDocs rs = searcher.search(query, numHits);
    ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

    for (int i = 0; i < docs.documents.length; i++) {
      float score = docs.scores[i];
      String mid = docs.documents[i].getField(IndexTopics.FIELD_TOPIC_MID).stringValue();
      String shortMid = getShortMid(mid);
      String name = docs.documents[i].getField(IndexTopics.FIELD_NAME).stringValue();
      String label = docs.documents[i].getField(IndexTopics.FIELD_LABEL).stringValue();
      rankedEntities.add(new RankedEntity(shortMid, score, name, label));
    }

    return rankedEntities;
  }

  /**
   * Returns a list of query results.
   *
   * @param queryName the entity name to search
   * @throws Exception on error
   * @return a list of top ranked entities
   */
  public List<RankedEntity> search(String queryName, int numHits) throws Exception {
    List<RankedEntity> rankedEntities = new ArrayList<>();

    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);

    // do exact search on query name
    QueryParser queryParser = new QueryParser(IndexTopics.FIELD_NAME, new SimpleAnalyzer());
    queryParser.setAutoGeneratePhraseQueries(true);
    queryParser.setPhraseSlop(3);
    queryName = "\"" + queryName + "\"";
    Query query = queryParser.parse(queryName);
    TopDocs rs = searcher.search(query, numHits);
    ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

    for (int i = 0; i < docs.documents.length; i++) {
      float score = docs.scores[i];
      String mid = docs.documents[i].getField(IndexTopics.FIELD_TOPIC_MID).stringValue();
      String shortMid = getShortMid(mid);
      String name = docs.documents[i].getField(IndexTopics.FIELD_NAME).stringValue();
      String label = docs.documents[i].getField(IndexTopics.FIELD_LABEL).stringValue();
      rankedEntities.add(new RankedEntity(shortMid, score, name, label));
    }

    if (docs.documents.length >= numHits) {
      return rankedEntities;
    }

    int numHitsLeft = numHits - docs.documents.length;

    // do TFIDF search
    Similarity similarity = new ClassicSimilarity();
    searcher.setSimilarity(similarity);
    queryParser = new MultiFieldQueryParser(
            new String[]{IndexTopics.FIELD_NAME,
                         IndexTopics.FIELD_LABEL},
            new SimpleAnalyzer());
    queryParser.setDefaultOperator(QueryParser.Operator.AND);
    query = queryParser.parse(queryName);

    rs = searcher.search(query, numHitsLeft);
    docs = ScoredDocuments.fromTopDocs(rs, searcher);

    for (int i = 0; i < docs.documents.length; i++) {
      float score = docs.scores[i];
      String mid = docs.documents[i].getField(IndexTopics.FIELD_TOPIC_MID).stringValue();
      String shortMid = getShortMid(mid);
      String name = docs.documents[i].getField(IndexTopics.FIELD_NAME).stringValue();
      String label = docs.documents[i].getField(IndexTopics.FIELD_LABEL).stringValue();
      rankedEntities.add(new RankedEntity(shortMid, score, name, label));
    }

    return rankedEntities;
  }

  // Removes '<', '>' if they exist, lower case
  private static String cleanUri(String uri) {
    if (uri.charAt(0) == '<' && uri.charAt(uri.length()-1) == '>') {
      return uri.substring(1, uri.length()-1).toLowerCase();
    }
    return uri;
  }

  // get 1-gram, 2-gram, 3-gram, 4-gram combinations from the text
  private static List<String> getSearchQueries(String text) {
    List<String> ngrams = new ArrayList<String>();
    if (text.endsWith(".") || text.endsWith("?")) {
      text = text.substring(0, text.length()-1);
    }
    String[] tokens = text.split("\\s+");

    // add unigram
    for (int i = 0; i < tokens.length; i++) {
      ngrams.add(tokens[i]);
    }

    // add bigram
    for (int i = 1; i < tokens.length; i++) {
      ngrams.add(tokens[i-1] + " " + tokens[i]);
    }

    // add 3-gram
    for (int i = 2; i < tokens.length; i++) {
      ngrams.add(tokens[i-2] + " " + tokens[i-1] + " " + tokens[i]);
    }

    // add 4-gram
    for (int i = 3; i < tokens.length; i++) {
      ngrams.add(tokens[i-3] + " " + tokens[i-2] + " " + tokens[i-1] + " " + tokens[i]);
    }

    return ngrams;
  }

  // get short Mid from the long URL - needed for comparison
  private static String getShortMid(String text) {
    if(text.startsWith(FREEBASE_URI)) {
      return text.substring(FREEBASE_URI.length()).replaceAll("/", ".");
    } else if(text.startsWith(RDF_FREEBASE_URI)) {
      return text.substring(RDF_FREEBASE_URI.length());
    } else if(text.startsWith(FREEBASE_SHORT_URI)) {
      return text.substring(FREEBASE_SHORT_URI.length());
    }
    return text;
  }

  /**
   * Removes quotes from the literal value in object field
   */
  private String removeQuotes(String literal) {
    if (literal.charAt(0) == '\"' && literal.charAt(literal.length()-1) == '\"') {
      return literal.substring(1, literal.length() - 1);
    }
    return literal;
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();

    // Parse args
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example command: "+ EntityLinking.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    LOG.info("searching gold data: " + searchArgs.goldData);
    if (searchArgs.goldData) {
      new EntityLinking(searchArgs.index).searchGoldFile(searchArgs.data, searchArgs.hits, searchArgs.output);
    } else {
      new EntityLinking(searchArgs.index).bruteSearchFile(searchArgs.data, searchArgs.hits, searchArgs.output);
    }
  }
}
