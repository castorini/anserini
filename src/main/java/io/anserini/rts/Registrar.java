package io.anserini.rts;

import java.io.StringReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Registrar {
  public static final Logger LOG = LogManager.getLogger(TRECSearcher.class);

  /* First stage: client registers from broker and gets client id */
  /* POST /register/system */
  public static String register(String api_base, String groupid, String alias) throws Exception {
    Client client = ClientBuilder.newClient();
    String clientid;
    WebTarget webTarget = client.target(api_base + "register/system");
    Response postResponse = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(
        new String("{\"groupid\":\"" + groupid + "\",\"alias\":\"" + alias + "\"}"), MediaType.APPLICATION_JSON));
    LOG.info("Register status " + postResponse.getStatus());
    if (postResponse.getStatus() == 200) {
      String jsonString = postResponse.readEntity(String.class);
      JsonNode rootNode = new ObjectMapper().readTree(new StringReader(jsonString));
      clientid = rootNode.get("clientid").asText();
      LOG.info("Register success with clientid " + clientid);
      return clientid;
    } else
      try {
        throw new Exception(postResponse.getStatus() + " " + postResponse.getStatusInfo().toString()
            + "\nRegister failed with the groupid " + groupid);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    return null;

  }

}
