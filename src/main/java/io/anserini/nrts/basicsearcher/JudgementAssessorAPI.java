package io.anserini.nrts.basicsearcher;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/api")
public class JudgementAssessorAPI {
  private static final String tweetsPoolPath="src/main/resources/tweets-pool";
  
  private static final long serialVersionUID = 1L;

  static class SearchAPIQuery{
    
    private String topic;
    private String runtag1;
    private String runtag2;
    public SearchAPIQuery(){    
    }

    public SearchAPIQuery(String topic, String runtag1,String runtag2) {
      this.topic=topic;
      this.runtag1=runtag1;
      this.runtag2=runtag2;
    }

    public String getTopic() {
      return topic;
    }
    public String getRuntag1() {
      return runtag1;
    }
    public String getRuntag2() {
      return runtag2;
    }

    public void setTopic(String topic) {
      this.topic = topic;
    }
    
    public void setRuntag1(String runtag1) {
      this.runtag1 = runtag1;
    }
    
    public void setRuntag2(String runtag2) {
      this.runtag2 = runtag2;
    }
  }

  static class SearchResult{
    String system;
    String docid;

    public SearchResult() {}

    public String getDocid() {

      return docid;
    }
    public String getSystem(){
      return system;
    }

    public void setDocid(String docid) {
      this.docid = docid;
    }
    
    public void setSystem(String system){
      this.system = system;
    }

    public SearchResult(String docid, String system) {
      this.docid = docid;
      this.system = system;
    }
  }
  @POST
  @Path("search")
  @Produces(MediaType.APPLICATION_JSON)
  public List<SearchResult> search(SearchAPIQuery query){
    try {
      String topic=query.getTopic();
      String[] tokens = topic.split(" ");
      topic=tokens[0];
      String runtag1=query.getRuntag1();
      String runtag2=query.getRuntag2();

      System.out.println("topic:"+topic+" runtag1:"+runtag1+" runtag2: "+runtag2);
      List<SearchResult> resultHits = new ArrayList<>();
      
      try(BufferedReader br = new BufferedReader(new FileReader(tweetsPoolPath+"/selected/"+runtag1+".txt"))) {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
                     
            String delims = "[ \t]+";
            tokens = line.split(delims);
            if (tokens[0].equals(topic)){
              System.out.println(line);
              resultHits.add(new SearchResult(tokens[1],"A"));
            }
            line=br.readLine();
         }
      }
      
      try(BufferedReader br = new BufferedReader(new FileReader(tweetsPoolPath+"/selected/"+runtag2+".txt"))) {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
          while (line != null) {
                     
            String delims = "[ \t]+";
            tokens = line.split(delims);
            if (tokens[0].equals(topic)){
//              System.out.println(line);
              resultHits.add(new SearchResult(tokens[1],"B"));
            }
            line=br.readLine();
          }
      }

      return resultHits;
    }catch (Exception e){
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
