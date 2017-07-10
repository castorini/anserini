package io.anserini.kg.freebase;

import com.google.common.collect.MinMaxPriorityQueue;
import io.anserini.kg.freebase.IndexTopics.TopicLuceneDocumentGenerator;
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
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Lookups a Freebase mid and returns all properties associated with it.
 */
public class EntityLinking implements Closeable {
  private static final Logger LOG = LogManager.getLogger(EntityLinking.class);

  private final IndexReader reader;

  private static int numHits = 200;

  static final class Args {
    // Required arguments

    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    public String index;

    @Option(name = "-data", metaVar = "[file]", required =  true, usage = "dataset file")
    public String data;

    @Option(name = "-goldData", usage = "supplying with the ground truth dataset")
    boolean goldData;

    @Option(name = "-hits", metaVar = "hits", usage = "setting number of hits to be retrieved per query")
    int hits;
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
      return new Float(this.score).compareTo(new Float(o.score));
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

  public void searchFile(String fileName) throws Exception {
    LOG.info("fileName: " + fileName);

    // Open the file
    FileInputStream fstream = new FileInputStream(fileName);
    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    String strLine;

    //Read File Line By Line
    int found = 0, notfound = 0;
    int index = -1;
    while ((strLine = br.readLine()) != null)   {
//      LOG.info("line: " + strLine);
      MinMaxPriorityQueue<RankedEntity> rankScoresHeap = MinMaxPriorityQueue.orderedBy(
              new Comparator<RankedEntity>() {
                @Override
                public int compare(RankedEntity e1, RankedEntity e2) {
                  return (e1.score > e2.score) ? -1 : (e1.score < e2.score) ? 1 : 0;
                }
              })
              .maximumSize(numHits)
              .create();
      String[] lineItems = strLine.split("\t");
      String shortMid = getShortMid(lineItems[0].trim());
      String question = lineItems[3].trim().toLowerCase();
      List<String> queries = getSearchQueries(question);
      for(String query : queries) {
//        LOG.info("query: " + query);
//        query = query.replaceAll("\\p{Punct}+$", ""); // remove punctuation at the end of word
        try {
          List<RankedEntity> rankScores = search(query);
          rankScoresHeap.addAll(rankScores);
        } catch (Exception e) {
          LOG.info("line: " + query);
          LOG.info("query: " + query);
          continue;
        }
      }

//      LOG.info("mid to compare: " + shortMid);
//      LOG.info("heap: " + rankScoresHeap);


      RankedEntity entityMidToCompare = new RankedEntity(shortMid, 0.0f, "", "");
      if (rankScoresHeap.contains(entityMidToCompare)) {
        found += 1;
        index = Arrays.asList( rankScoresHeap.toArray() ).indexOf(entityMidToCompare);
        LOG.info("found at index: " + index);
      }
      else {
        notfound += 1;
      }
    }

    //Close the input stream
    br.close();

    LOG.info("Found: " + found);
    LOG.info("Not Found: " + notfound);
    LOG.info("Querying completed.");
  }

  public void searchGoldFile(String fileName) throws Exception {
    LOG.info("fileName: " + fileName);
    LOG.info("numHits: " + numHits);

    // Open the file
    FileInputStream fstream = new FileInputStream(fileName);
    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    String strLine;

    //Read File Line By Line
    int found = 0, notfound = 0;
    List<RankedEntity> rankedEntities;
    while ((strLine = br.readLine()) != null)   {
//      LOG.info("line: " + strLine);
      String[] lineItems = strLine.split(" %%%% ");
      String shortMid = getShortMid( cleanUri(lineItems[0].trim()) );
      String[] queries = lineItems[4].trim().split(" &&&& ");
      boolean isFound = false;
      int index = -1;
      for (String query: queries) {
        if (!isFound) {
          query = removeQuotes(query.trim().toLowerCase());
          try {
            rankedEntities = search(query);
          } catch (Exception e) {
            // only keep the alphabets in the query
            String queryStripped = query.replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", " ");
            if (queryStripped.trim().length() > 0) {
              rankedEntities = search(queryStripped);
            } else {
              LOG.info(String.format("WEIRD! query: %s", query, index));
              continue;
            }
          }

          RankedEntity entityMidToCompare = new RankedEntity(shortMid, 0.0f, "", "");
          if (rankedEntities.contains(entityMidToCompare)) {
            isFound = true;
            index = rankedEntities.indexOf(entityMidToCompare);
            break;
          }
        }
      }
      if (isFound) {
        found += 1;
        LOG.info(String.format("queries: %s,\tfound at index: %d", Arrays.asList(queries), index));
      } else {
        notfound += 1;
        LOG.info(String.format("queries: %s,\tNOT found", Arrays.asList(queries), index));
      }
    }

    //Close the input stream
    br.close();

    LOG.info("Found: " + found);
    LOG.info("Not Found: " + notfound);
    LOG.info("Querying completed.");
  }

  /**
   * Prints query results to the standard output stream.
   *
   * @param queryName the entity name to search
   * @throws Exception on error
   */
  public List<RankedEntity> search(String queryName) throws Exception {
    // Initialize index searcher
    IndexSearcher searcher = new IndexSearcher(reader);

    Similarity similarity = new ClassicSimilarity();
    searcher.setSimilarity(similarity);

    MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
            new String[]{ IndexTopics.FIELD_NAME,
                    IndexTopics.FIELD_LABEL },
            new SimpleAnalyzer());
    queryParser.setDefaultOperator(QueryParser.Operator.AND);
    Query query = queryParser.parse(queryName);

    TopDocs rs = searcher.search(query, numHits);
    ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);
    List<RankedEntity> rankedEntities = new ArrayList<>(numHits);

    for (int i = 0; i < docs.documents.length; i++) {
      float score = docs.scores[i];
      String mid = docs.documents[i].getField(IndexTopics.FIELD_TOPIC_MID).stringValue();
      String shortMid = getShortMid(mid);
      String name = docs.documents[i].getField(IndexTopics.FIELD_NAME).stringValue();
      String label = docs.documents[i].getField(IndexTopics.FIELD_LABEL).stringValue();
      rankedEntities.add( new RankedEntity(shortMid, score, name, label) );
    }

    return rankedEntities;
  }

  // Removes '<', '>' if they exist, lower case
  private static String cleanUri(String uri) {
    if (uri.charAt(0) == '<')
      return uri.substring(1, uri.length() - 1).toLowerCase();
    else
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
    for (int i=0; i < tokens.length; i++) {
      ngrams.add(tokens[i]);
    }

    // add bigram
    for (int i=1; i < tokens.length; i++) {
      ngrams.add(tokens[i-1] + " " + tokens[i]);
    }

    // add 3-gram
    for (int i=2; i < tokens.length; i++) {
      ngrams.add(tokens[i-2] + " " + tokens[i-1] + " " + tokens[i]);
    }

    // add 4-gram
    for (int i=3; i < tokens.length; i++) {
      ngrams.add(tokens[i-3] + " " + tokens[i-2] + " " + tokens[i-1] + " " + tokens[i]);
    }

    return ngrams;
  }

  // get short Mid from the long URL - needed for comparison
  private static String getShortMid(String text) {
    if(text.startsWith("www.freebase.com/")) {
      return text.substring(17).replaceAll("/", ".");
    }
    else if(text.startsWith("http://rdf.freebase.com/ns/")) {
      return text.substring(27);
    }
    else if(text.startsWith("fb:")) {
      return text.substring(3);
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
    CmdLineParser parser = new CmdLineParser(searchArgs,
            ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example command: "+ EntityLinking.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    if (searchArgs.hits > 0)
      EntityLinking.numHits = searchArgs.hits;

    LOG.info("searching gold data: " + searchArgs.goldData);
    if (searchArgs.goldData)
      new EntityLinking(searchArgs.index).searchGoldFile(searchArgs.data);
    else
      new EntityLinking(searchArgs.index).searchFile(searchArgs.data);
  }
}
