package io.anserini.rts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class TopicPoller {

  public static final Logger LOG = LogManager.getLogger(TRECSearcher.class);

  /* Second stage: client gets topics from broker */
  /* GET /topics/:clientid */
  public static TRECTopic[] getInitialTopics(String api_base, String clientid, String interestProfilePath)
      throws Exception {
    Client client = ClientBuilder.newClient();
    TRECTopic[] topics;
    WebTarget webTarget = client.target(api_base + "topics/" + clientid);
    Response postResponse = webTarget.request(MediaType.APPLICATION_JSON).get();

    if (postResponse.getStatus() == 200) {
      LOG.info("Get topics success");
      String jsonString = postResponse.readEntity(String.class);
      ObjectMapper mapper = new ObjectMapper();
      topics = mapper.readValue(jsonString, TypeFactory.defaultInstance().constructArrayType(TRECTopic.class));

      LOG.info(topics.toString());
      LOG.info(postResponse.toString());
      File file = new File(interestProfilePath);
      boolean isDirectoryCreated = file.mkdir();
      if (isDirectoryCreated) {
        LOG.info("Interest profile directory successfully made");
      } else {
        FileUtils.deleteDirectory(file);
        file.mkdir();
        LOG.info("Interest profile directory successfully covered");
      }
      for (int i = 0; i < topics.length; i++) {
        writeTRECTopicToDisk(topics[i]);
      }
      return topics;
    } else
      try {
        throw new Exception(
            postResponse.getStatus() + " " + postResponse.getStatusInfo().toString() + "\nGet topics failed.");
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    return null;

  }

  /*
   * Forth stage: client periodically gets updated topics from broker (same API)
   * GET /topics/:clientid
   */
  public static TRECTopic[] getUpdatedTopic(String api_base, String clientid, String interestProfilePath)
      throws Exception {
    Client client = ClientBuilder.newClient();
    TRECTopic[] topics;
    WebTarget webTarget = client.target(api_base + "topics/" + clientid);
    Response postResponse = webTarget.request(MediaType.APPLICATION_JSON).get();

    if (postResponse.getStatus() == 200) {
      LOG.info("Get topics success");
      String jsonString = postResponse.readEntity(String.class);
      ObjectMapper mapper = new ObjectMapper();
      topics = mapper.readValue(jsonString, TypeFactory.defaultInstance().constructArrayType(TRECTopic.class));

      return topics;
    } else
      try {
        throw new Exception(
            postResponse.getStatus() + " " + postResponse.getStatusInfo().toString() + "\nGet updated topics failed.");
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    return null;

  }

  public static void writeTRECTopicToDisk(TRECTopic trecTopic) throws JSONException, IOException {
    // TODO Auto-generated method stub
    JSONObject obj = new JSONObject();
    obj.put("index", trecTopic.topid);
    obj.put("query", trecTopic.title);
    obj.put("narr", trecTopic.narrative);
    obj.put("desc", trecTopic.description);

    /*
     * It is at participants' discretion on how to utilize the interest profile
     * and implement proper query expansion module
     */

    obj.put("expansion", new JSONArray());

    try (FileWriter topicFile = new FileWriter(TRECSearcher.interestProfilePath + trecTopic.topid + ".json")) {
      topicFile.write(obj.toString());
      LOG.info("Successfully wrote interest profile " + trecTopic.topid + " to disk.");
      topicFile.close();
    }

  }
}
