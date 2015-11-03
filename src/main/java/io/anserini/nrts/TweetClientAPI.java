package io.anserini.nrts;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.index.twitter.TweetAnalyzer;
import twitter4j.JSONException;

public class TweetClientAPI {

  static Client client = ClientBuilder.newClient();
  String clientid, groupid;

  static TweetTopic[] topics;
  private static final String HOST_OPTION = "host";
  private static final String INDEX_OPTION = "index";
  private static final String PORT_OPTION = "port";
  static String api_base;
  static String resourcePath = "src/main/java/io/anserini/nrts/public/";
  static String MustacheTemplatePath = resourcePath + "servletResponseTemplate.mustache";
  static IndexWriter indexWriter;

  private IndexReader reader;
  
  @JsonIgnoreProperties(ignoreUnknown=true)
  static public class TweetTopic{
    @JsonProperty("topid")
    public String topid;
    @JsonProperty("query")
    public String query;
    @JsonCreator
    public TweetTopic(@JsonProperty("topid") String topicID,@JsonProperty("query") String query){
      super();
      this.topid=topicID;
      this.query=query;
    }
  }

  TweetClientAPI(String groupID) {
    this.groupid = groupID;
  }

  public void register() throws JsonProcessingException, IOException {
    WebTarget webTarget = client.target(api_base + "register/system"); 
    Response postResponse = webTarget.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(new String("{\"groupid\":\""+groupid+"\"}") , MediaType.APPLICATION_JSON));

    if (postResponse.getStatus()==200){
      System.out.print("Register success,");
      String jsonString = postResponse.readEntity(String.class);    
      JsonNode rootNode = new ObjectMapper().readTree(new StringReader(jsonString));
      clientid=rootNode.get("clientid").asText();
      System.out.println(" clientid is " + clientid);      
    }
    
    else System.out.print("Register failed");
    
  }

  public void getTopic() throws JsonParseException, JsonMappingException, IOException, JSONException { 
    //[{"topid":"test1","query":"birthday"},{"topid":"test2","query":"batman"},{"topid":"test3","query":"star wars"}]
    WebTarget webTarget = client.target(api_base + "topics/"+clientid); // target(String uri) version

    Response postResponse = webTarget.request(MediaType.APPLICATION_JSON).get();

    if (postResponse.getStatus()==200){
      System.out.println("Retrieve topics success");
      String jsonString = postResponse.readEntity(String.class);
      ObjectMapper mapper = new ObjectMapper();
      topics = mapper.readValue(jsonString, TypeFactory.defaultInstance().constructArrayType(TweetTopic.class));
      for (int i=0;i<topics.length;i++){
        System.out.println("Topic " + topics[i].topid + ": " + topics[i].query);
      }
    }
    
  }

  class TweetPusherRunnable implements Runnable {
    public void run() {
      System.out.println("Running TweetPusher Thread");
      try {
        while(true){
          System.out.println("Wake up and query...");
          for (int i = 0; i < topics.length; i++) {
            System.out.print("Quering:"+topics[i].query+", "); // test 
            try {
              Query q = new QueryParser(TweetStreamIndexer.StatusField.TEXT.name, TweetSearcher.ANALYZER)
                  .parse(topics[i].query);
              try {
                reader = DirectoryReader.open(indexWriter, true);
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader, indexWriter, true);
              if (newReader != null) {
                reader.close();
                reader = newReader;
              }
              IndexSearcher searcher = new IndexSearcher(reader);

              int topN = 5;
              TopScoreDocCollector collector = TopScoreDocCollector.create(topN);
              searcher.search(q, collector);
              ScoreDoc[] hits = collector.topDocs().scoreDocs;
              System.out.println("Found "+hits.length+" hits");

              for (int j = 0; j < hits.length && j < topN; ++j) {
                int docId = hits[j].doc;
                Document d = searcher.doc(docId);
                System.out.println("Tweet ID:" + String.valueOf(d.get(TweetStreamIndexer.StatusField.ID.name)) + " Tweet text:" + d.get(StatusField.TEXT.name));
                String targetURL=api_base + "tweet/"+topics[i].topid+"/"+String.valueOf(d.get(TweetStreamIndexer.StatusField.ID.name))+"/"+clientid;
                System.out.println(targetURL);
                WebTarget webTarget = client.target(targetURL);
                Response postResponse = webTarget.request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(new String("{\"topid\":\""+topics[i].topid+"\",\"status.id\":\""+String.valueOf(d.get(TweetStreamIndexer.StatusField.ID.name))+"\",\"clientid\":\""+clientid+"\"}"), MediaType.APPLICATION_JSON));
                System.out.println("Push tweets status:"+postResponse.getStatus());
              }
            } catch (Exception e) {
              e.printStackTrace();
            }// Client push tweetid, topic id to broker
          }
          Thread.sleep(4000);// Let the thread sleep for a while.
        }
      } catch (InterruptedException e) {
        System.out.println("Thread interrupted.");
      }
    }
  }

  public static void main(final String[] args)
      throws JsonParseException, JsonMappingException, IOException, InterruptedException, JSONException {
    Options options = new Options();
    options.addOption(HOST_OPTION, true, "hostname");
    options.addOption(INDEX_OPTION, true, "index path");
    options.addOption(PORT_OPTION, true, "port");

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (org.apache.commons.cli.ParseException e) {
      System.err.println("Error parsing command line: " + e.getMessage());
      System.exit(-1);
    }
    if (!cmdline.hasOption(HOST_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(TweetSearcher.class.getName(), options);
      System.exit(-1);
    }

    if (!cmdline.hasOption(INDEX_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(TweetSearcher.class.getName(), options);
      System.exit(-1);
    }

    int port = cmdline.hasOption(PORT_OPTION) ? Integer.parseInt(cmdline.getOptionValue(PORT_OPTION)) : 8080;
    String host = cmdline.getOptionValue(HOST_OPTION);
    String dir=cmdline.getOptionValue(INDEX_OPTION);
    api_base = new String("http://" + host + ":" + port + "/");

    Directory index = new MMapDirectory(Paths.get(dir));
    IndexWriterConfig config = new IndexWriterConfig(new TweetAnalyzer());
    indexWriter = new IndexWriter(index, config);

    TweetStreamIndexer tweetStreamIndexer = new TweetStreamIndexer(indexWriter);
    Thread tweetStreamIndexerThread = new Thread(tweetStreamIndexer);
    tweetStreamIndexerThread.start();
    
    TweetClientAPI tweetClientAPI = new TweetClientAPI("uwar");
    tweetClientAPI.register();
    tweetClientAPI.getTopic();
    Thread tweetPusherThread = new Thread(tweetClientAPI.new TweetPusherRunnable());
    tweetPusherThread.start();
    
    tweetStreamIndexerThread.join();
    tweetPusherThread.join();
    indexWriter.close();
    client.close();
    

  }

}
