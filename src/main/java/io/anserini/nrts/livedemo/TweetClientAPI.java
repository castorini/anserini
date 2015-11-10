package io.anserini.nrts.livedemo;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import io.anserini.nrts.basicsearcher.TweetStreamIndexer;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class TweetClientAPI {

  static Client client = ClientBuilder.newClient();
  static String clientid;
  static String groupid;

  static TweetTopic[] topics;
  private static final String HOST_OPTION = "host";
  private static final String INDEX_OPTION = "index";
  private static final String PORT_OPTION = "port";
  private static final String INTERVAL_OPTION = "interval";
  private static final String GROUPID_OPTION="groupid";
  private static final String DAILYLIMIT_OPTION="dailylimit";
  
  
  static String api_base;
  static IndexWriter indexWriter;
  boolean shutDown=false;
  
  private static final Logger LOG = LogManager.getLogger(TweetClientAPI.class);
  
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
  
  class RegisterException extends Exception {
    public RegisterException(String msg){
       super(msg);
    }
 }
  
  /*First stage: client registers from broker and gets client id */
  public void register() throws JsonProcessingException, IOException, JSONException {
    WebTarget webTarget = client.target(api_base + "register/system"); 
    
    /* formulate request bodies in JSON, worked out fine in TweetPusherRunnable, but failed here
     * JSONObject groupidjson=new JSONObject();
       groupidjson.put("groupid", String.valueOf("uwar"));
       Response postResponse = webTarget.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(groupidjson, MediaType.APPLICATION_JSON));
     */    
    
    Response postResponse = webTarget.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(new String("{\"groupid\":\""+groupid+"\"}") , MediaType.APPLICATION_JSON));

    if (postResponse.getStatus()==200){
      LOG.info("Register success,");
      String jsonString = postResponse.readEntity(String.class);    
      JsonNode rootNode = new ObjectMapper().readTree(new StringReader(jsonString));
      clientid=rootNode.get("clientid").asText();
      LOG.info(" clientid is " + clientid);      
    } else
      try {
        throw new RegisterException("Register failed to register with this groupid");
      } catch (RegisterException e) {
        System.out.println(postResponse.getStatus());
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    
  }
  
  /*Second stage: client gets topics from broker */
  public void getTopic() throws JsonParseException, JsonMappingException, IOException, JSONException { 
    /* topics Json format: [{"topid":"test1","query":"birthday"},{"topid":"test2","query":"batman"},{"topid":"test3","query":"star wars"}] */
    WebTarget webTarget = client.target(api_base + "topics/"+clientid); // target(String uri) version

    Response postResponse = webTarget.request(MediaType.APPLICATION_JSON).get();

    if (postResponse.getStatus()==200){
      LOG.info("Retrieve topics success");
      String jsonString = postResponse.readEntity(String.class);
      ObjectMapper mapper = new ObjectMapper();
      topics = mapper.readValue(jsonString, TypeFactory.defaultInstance().constructArrayType(TweetTopic.class));
      for (int i=0;i<topics.length;i++){
        LOG.info("Topic " + topics[i].topid + ": " + topics[i].query);
      }
    }
    
  }

  public static void main(final String[] args)
      throws JsonParseException, JsonMappingException, IOException, InterruptedException, JSONException {
    /* options/arguments: -index twitter -host lab.roegiest.com -port 33334 -interval 0.1 -groupid uwar -dailylimit 6 */
    
    Options options = new Options();
    options.addOption(HOST_OPTION, true, "hostname");
    options.addOption(INDEX_OPTION, true, "index path");
    options.addOption(PORT_OPTION, true, "port");
    options.addOption(INTERVAL_OPTION,true,"interval");
    options.addOption(GROUPID_OPTION,true,"groupid");
    options.addOption(DAILYLIMIT_OPTION,true,"dailylimit");

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
      formatter.printHelp(TweetClientAPI.class.getName(), options);
      System.exit(-1);
    }

    if (!cmdline.hasOption(INDEX_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(TweetClientAPI.class.getName(), options);
      System.exit(-1);
    }
    if (!cmdline.hasOption(INTERVAL_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(TweetClientAPI.class.getName(), options);
      System.exit(-1);
    }
    if (!cmdline.hasOption(GROUPID_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(TweetClientAPI.class.getName(), options);
      System.exit(-1);
    }
    if (!cmdline.hasOption(DAILYLIMIT_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(TweetClientAPI.class.getName(), options);
      System.exit(-1);
    }
    

    int port = cmdline.hasOption(PORT_OPTION) ? Integer.parseInt(cmdline.getOptionValue(PORT_OPTION)) : 8080;
    float interval = cmdline.hasOption(INTERVAL_OPTION) ? Float.parseFloat(cmdline.getOptionValue(INTERVAL_OPTION)) : 1; 
    String groupid=cmdline.hasOption(GROUPID_OPTION)?cmdline.getOptionValue(GROUPID_OPTION):"uwar";
    int dailylimit=cmdline.hasOption(DAILYLIMIT_OPTION)?Integer.parseInt(cmdline.getOptionValue(DAILYLIMIT_OPTION)):10;
    String host = cmdline.getOptionValue(HOST_OPTION);
    String dir=cmdline.getOptionValue(INDEX_OPTION);
    api_base = new String("http://" + host + ":" + port + "/");
    
    Directory index = new MMapDirectory(Paths.get(dir));
    IndexWriterConfig config = new IndexWriterConfig(new TweetAnalyzer());
    indexWriter = new IndexWriter(index, config);
    
    TweetStreamIndexer tweetStreamIndexer = new TweetStreamIndexer(indexWriter);
    Thread tweetStreamIndexerThread = new Thread(tweetStreamIndexer);
    tweetStreamIndexerThread.start();
    
    TweetClientAPI tweetClientAPI = new TweetClientAPI(groupid);
    tweetClientAPI.register();
    tweetClientAPI.getTopic();
    
    /*Third stage: client pushes relevant tweets' tweetid & topid to broker, based on topics set, time interval, etc. information*/
    Thread tweetPusherThread = new Thread(new TweetPusherRunnable(indexWriter,dailylimit,clientid,interval,api_base,topics));
    tweetPusherThread.start();
    
    tweetStreamIndexerThread.join();
    tweetPusherThread.join();
    
    tweetStreamIndexerThread.join();
    tweetPusherThread.join();
    indexWriter.close();
    client.close();

  }

}
