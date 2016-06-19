package io.anserini.ltr;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.search.MicroblogTopicSet;
import io.anserini.search.SearchWebCollection;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * Main class for feature extractors feed in command line arguments to dump features
 */
public class FeatureExtractorCli {
  private static final Logger LOG = LogManager.getLogger(FeatureExtractorCli.class);

  private static class FeatureExtractionArgs {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index directory")
    public String indexDir;

    @Option(name = "-qrel", metaVar = "[path]", required = true, usage = "Qrel File")
    public String qrelFile;

    @Option(name = "-topic", metaVar = "[path]", required = true, usage = "Topic File")
    public String topicsFile;

    @Option(name = "-out", metaVar = "[path]", required = true, usage = "Output File")
    public String outputFile;

    @Option(name = "-collection", metaVar = "[path]", required = true, usage = "[clueweb|gov2|twitter]")
    public String collection;

    @Option(name = "-extractors", metaVar = "[path]", required = false, usage = "FeatureExtractors File")
    public String extractors = null;

  }
  /**
   * requires the user to supply the index directory and also the directory containing the qrels and topics
   * @param args  indexDir, qrelFile, topicFile, outputFile
   */
  public static void main(String args[]) throws Exception {

    long curTime = System.nanoTime();
    FeatureExtractionArgs parsedArgs = new FeatureExtractionArgs();
    CmdLineParser parser= new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    Directory indexDirectory = FSDirectory.open(Paths.get(parsedArgs.indexDir));
    IndexReader reader = DirectoryReader.open(indexDirectory);
    Qrels qrels = new Qrels(parsedArgs.qrelFile);

    FeatureExtractors extractors = null;
    if (parsedArgs.extractors != null) {
      extractors = FeatureExtractors.loadExtractor(parsedArgs.extractors);
    }

    // Query parser needed to construct the query object for feature extraction in the loop
    PrintStream out = new PrintStream (new FileOutputStream(new File(parsedArgs.outputFile)));
    if (parsedArgs.collection.equals("gov2") || parsedArgs.collection.equals("clueweb")) {
      // Open the topics file and read it
      SortedMap<Integer, String> topics = parsedArgs.collection.equals("gov2") ?
              SearchWebCollection.readTeraByteTackQueries(Paths.get(parsedArgs.topicsFile)) :
              SearchWebCollection.readWebTrackQueries(Paths.get(parsedArgs.topicsFile));
      LOG.debug(String.format("%d topics found", topics.size()));

      WebFeatureExtractor extractor = new WebFeatureExtractor(reader, qrels, convertTopicsFormat(topics), extractors);
      extractor.printFeatures(out);
    } else if (parsedArgs.collection.equals("twitter")) {
      Map<String,String> topics = MicroblogTopicSet.fromFile(new File(parsedArgs.topicsFile)).toMap();
      LOG.debug(String.format("%d topics found", topics.size()));
      TwitterFeatureExtractor extractor = new TwitterFeatureExtractor(reader, qrels, topics, extractors);
      extractor.printFeatures(out);
    } else {
      System.err.println("Unrecognized collection " + parsedArgs.collection );
    }
  }

  private static Map<String,String> convertTopicsFormat(Map<Integer,String> topics) {
    HashMap<String, String> convertedTopics = new HashMap<>(topics.size());

    for (Map.Entry<Integer,String> entry : topics.entrySet()) {
      convertedTopics.put(String.valueOf(entry.getKey()), entry.getValue());
    }
    return convertedTopics;
  }

}
