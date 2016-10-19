package io.anserini.rts;

import java.util.ArrayList;

import com.google.gson.JsonArray;

public class InterestProfile {

  public String topicIndex;
  public String query;
  public ArrayList<String> expansion = new ArrayList<String>();
  public String titleQuery;
  public String titleExpansionQuery;
  public String expansionQuery;
  public int queryTokenCount;

  public String titleQueryString() {
    titleQuery = TRECTwokenizer.trecTokenizeQuery(query);
    queryTokenCount = query.split(" ").length;
    return titleQuery;
  }

  public String titleExpansionQueryString(int titleBoostFactor, int expansionBoostFactor) {
    String titleQuery = TRECTwokenizer.trecTokenizeQueryBoost(query, titleBoostFactor) + " ";
    expansionQuery = "";
    for (String expansionCluster : expansion) {
      expansionQuery = expansionQuery + "(" + expansionCluster + ")^" + (new Integer(expansionBoostFactor).toString())
          + " ";
    }
    return titleQuery + expansionQuery;

  }

  public InterestProfile(String topicIndex, String query, JsonArray expansion) {
    this.topicIndex = topicIndex;
    this.query = query;

    for (int i = 0; i < expansion.size(); i++) {
      JsonArray synonymArray = expansion.get(i).getAsJsonArray();
      ArrayList<String> synonymArrayList = new ArrayList<String>();
      for (int j = 0; j < synonymArray.size(); j++)
        synonymArrayList.add(synonymArray.get(j).toString());

      String concatenatedString = "";

      for (String t : synonymArrayList) {
        concatenatedString = concatenatedString + t + " ";
      }
      String thisExpansionCluster = "";

      for (String t : TRECTwokenizer.trecTokenizeQuery(concatenatedString).split(" ")) {
        thisExpansionCluster = thisExpansionCluster + t + " OR ";
      }

      this.expansion.add(thisExpansionCluster.substring(0, thisExpansionCluster.length() - 4));
    }
  }
}
