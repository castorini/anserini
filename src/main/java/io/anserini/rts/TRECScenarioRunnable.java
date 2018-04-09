package io.anserini.rts;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.anserini.index.generator.TweetGenerator.StatusField;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

//import com.ibm.icu.util.TimeZone;

public class TRECScenarioRunnable extends TimerTask {
  private static final JsonParser JSON_PARSER = new JsonParser();
  private String indexPath;

  // UWaterlooMDS design: score(q,d) = (We*Ne+Wt*Nt)*Nt/T =
  // titleCoordSimilarity(q,d) * titleExpansionSimilarity(q,d)
  // where:
  // We - expansion term boost factor
  // Wt - title term boost factor
  // Ne - num of expansion term matches in the document
  // Nt - num of title term matches in the document
  // T - num of title terms in the title field

  // Relevant discussion can be found here:
  // https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/search/similarities/TFIDFSimilarity.html

  // First scoring part: titleCoordSimilarity(q,d) = Nt/T
  private TitleCoordSimilarity titleCoordSimilarity = new TitleCoordSimilarity();
  // Second scoring art: titleExpansionSimilarity(q,d) = (We*Ne+Wt*Nt)
  private TitleExpansionSimilarity titleExpansionSimilarity = new TitleExpansionSimilarity();

  private final int titleBoostFactor = 3;
  private final int expansionBoostFactor = 1;

  private DateFormat format;
  private Calendar now;
  private String api;
  private String scenario; // either "A" or "B"

  private static final Logger LOG = TRECSearcher.LOG;
  private static BufferedWriter scenarioALogWriter = TRECSearcher.scenarioALogWriter;
  private static BufferedWriter scenarioBLogWriter = TRECSearcher.scenarioBLogWriter;
  private IndexReader reader; // should it be static or one copy per
  // thread? to be answered
  HashMap<String, String> pushedTweets = new HashMap<String, String>();
  int dailylimit = 10;
  boolean shutDown = false; // if Scenario=A and reached daily limit, will set
  // shutDown=true, and the thread
  // will sleep for the rest of the day
  private long interval;
  private InterestProfile thisInterestProfile;

  private final float duplicateThreshold = 0.6f;

  public TRECScenarioRunnable(String index, String interestProfilePath, String api, String scenario)
      throws FileNotFoundException, IOException {
    this.indexPath = index;
    this.api = api;
    LOG.info(api);
    this.scenario = scenario;
    if (scenario.equals("A"))
      interval = TRECSearcher.minuteInterval;
    else if (scenario.equals("B"))
      interval = TRECSearcher.dailyInterval;

    String JSONObjectString = "";
    try (BufferedReader br = new BufferedReader(new FileReader(interestProfilePath))) {
      String line = br.readLine();

      while (line != null) {
        JSONObjectString = JSONObjectString + line;
        line = br.readLine();
      }
      br.close();
    }
    JsonObject interestProfileObject = (JsonObject) JSON_PARSER.parse(JSONObjectString);
    thisInterestProfile = new InterestProfile(interestProfileObject.get("index").getAsString(),
        interestProfileObject.get("query").getAsString(), interestProfileObject.getAsJsonArray("expansion"));

    format = new SimpleDateFormat("E dd MMM yyyy HH:mm:ss zz");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

  }

  public class ScoreDocComparator implements Comparator<ScoreDocTimestamp> {

    @Override
    // First sort by score, then sort by timestamp. Both in descending order
    public int compare(ScoreDocTimestamp o1, ScoreDocTimestamp o2) {
      Float f1 = new Float(o1.score);
      // TODO Auto-generated method stub
      if (f1 != o2.score) {
        return (-1) * f1.compareTo(new Float(o2.score));
      } else {
        Long l1 = new Long(o1.timestamp);
        return (-1) * (l1.compareTo(new Long(o2.timestamp)));
      }
    }
  }

  public boolean isDuplicate(String whiteSpaceTokenizedText) {
    Set<String> thisTokens = new HashSet<String>(Arrays.asList(whiteSpaceTokenizedText.split(" ")));
    for (String previousWhiteSpaceTokenizedText : pushedTweets.values()) {
      Set<String> previousTokens = new HashSet<String>(Arrays.asList((previousWhiteSpaceTokenizedText).split(" ")));
      Set<String> intersectionTokens = new HashSet<String>(thisTokens);
      intersectionTokens.retainAll(previousTokens);
      if ((intersectionTokens.size() * 1.0 / thisTokens.size()) > duplicateThreshold) {
        return true;
      }

    }
    return false;
  }

  /*
   * Scenario A, post search result to broker: POST
   * /tweet/:topid/:tweetid/:clientid
   */
  @SuppressWarnings("deprecation")
  public void postTweetListScenarioA(ArrayList<String> tweetList, String api) throws IOException {
    for (String tweetid : tweetList) {
      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client.target(api.replace(":tweetid", tweetid));
      Response postResponse = webTarget.request(MediaType.APPLICATION_JSON)
          .post(Entity.entity(new String(""), MediaType.APPLICATION_JSON));
      LOG.info("Post status " + postResponse.getStatus());

      if (postResponse.getStatus() == 204 || postResponse.getStatus() == 429) {
        LOG.info("Scenario A, " + api.replace(":tweetid", tweetid) + " Returns a " + postResponse.getStatus()
            + " status code on push notification success at "
            + Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().toGMTString());
        scenarioALogWriter
            .write("Scenario A	" + Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().toGMTString() + "	"
                + Calendar.getInstance().getTimeInMillis() + "	" + thisInterestProfile.topicIndex + "	" + tweetid);
        scenarioALogWriter.newLine();
        scenarioALogWriter.flush();
      }
      client.close();

    }

  }

  /*
   * Scenario B, formatted as a plain text file, where each line has the
   * following fields: YYYYMMDD topic_id Q0 tweet_id rank score runtag
   */
  @SuppressWarnings("deprecation")
  public void postTweetListScenarioB(ArrayList<String> tweetList, String api, HashMap<String, Float> scoreMap)
      throws IOException {

    SimpleDateFormat sdf = new SimpleDateFormat();
    sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
    sdf.applyPattern("yyyyMMdd");

    Calendar yesterday = Calendar.getInstance();
    yesterday.set(Calendar.DAY_OF_YEAR, yesterday.get(Calendar.DAY_OF_YEAR) - 1);
    yesterday.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date testDate = yesterday.getTime();

    for (int i = 0; i < tweetList.size(); i++) {

      scenarioBLogWriter.write(sdf.format(testDate) + " " + thisInterestProfile.topicIndex + " Q0 " + tweetList.get(i)
          + " " + (i + 1) + " " + scoreMap.get(tweetList.get(i)) + " " + TRECSearcher.alias);
      scenarioBLogWriter.newLine();
      scenarioBLogWriter.flush();
    }

  }

  public void close() throws IOException {
    scenarioALogWriter.close();
    scenarioBLogWriter.close();
  }

  @SuppressWarnings("deprecation")
  @Override
  public void run() {
    LOG.info("Running TRECScenarioSearcher Thread for " + thisInterestProfile.topicIndex);
    try {
      // When the thread wakes up at a new day, clear pushed tweets
      if ((scenario.equals("A") && Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.DAY_OF_YEAR) != now
          .get(Calendar.DAY_OF_YEAR)) || (scenario.equals("B")))
        pushedTweets.clear();
      Query titleQuery = new QueryParser(TRECIndexerRunnable.StatusField.TEXT.name, Indexer.ANALYZER)
          .parse(thisInterestProfile.titleQueryString());
      LOG.info("Parsed titleQuery " + titleQuery.getClass() + " looks like " + titleQuery.toString() + " "
          + titleQuery.getClass());
      reader = DirectoryReader.open(FSDirectory.open(new File(indexPath).toPath()));

      IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader);
      if (newReader != null) {
        reader.close();
        reader = newReader;
      }
      IndexSearcher searcher = new IndexSearcher(reader);

      searcher.setSimilarity(titleCoordSimilarity);
      // Get the total number of hits
      TotalHitCountCollector totalHitCollector = new TotalHitCountCollector();

      // First search and scoring part: titleCoordSimilarity(q,d) = Nt/T
      searcher.search(titleQuery, totalHitCollector);

      // Create a collector for these hits
      if (totalHitCollector.getTotalHits() > 0) {
        TopScoreDocCollector titleQueryHitCollector = TopScoreDocCollector
            .create(Math.max(0, totalHitCollector.getTotalHits()));
        searcher.search(titleQuery, titleQueryHitCollector);
        ScoreDoc[] coordHits = titleQueryHitCollector.topDocs().scoreDocs;
        HashMap<Integer, Float> coordHMap = new HashMap<Integer, Float>();
        for (ScoreDoc s : coordHits) {
          coordHMap.put(s.doc, s.score);
        }

        LOG.info("Title coordinate similarity has " + totalHitCollector.getTotalHits() + " hits");

        Query titleExpansionQuery = new QueryParser(TRECIndexerRunnable.StatusField.TEXT.name, Indexer.ANALYZER)
            .parse(thisInterestProfile.titleExpansionQueryString(titleBoostFactor, expansionBoostFactor));

        BooleanQuery.Builder bqBuilder = new BooleanQuery.Builder();
        bqBuilder.add(titleExpansionQuery, BooleanClause.Occur.MUST);
        Query tweetTimeRangeQuery = LongPoint.newRangeQuery(StatusField.EPOCH.name,
            (long) (Calendar.getInstance().getTimeInMillis() - interval) / 1000,
            (long) Calendar.getInstance().getTimeInMillis() / 1000);

        // must satisfy the time window, FILTER clause do not
        // participate in scoring
        bqBuilder.add(tweetTimeRangeQuery, BooleanClause.Occur.FILTER);
        Query q = bqBuilder.build();

        LOG.info("Parsed titleExpansionQuery " + titleExpansionQuery.getClass() + " looks like "
            + titleExpansionQuery.toString() + " " + titleExpansionQuery.getClass());
        LOG.info("Parsed finalQuery " + q.getClass() + " looks like " + q.toString() + " "
            + q.getClass());
        searcher.setSimilarity(titleExpansionSimilarity);

        totalHitCollector = new TotalHitCountCollector();
        // Second search and scoring part:
        // titleExpansionSimilarity(q,d)= (We*Ne+Wt*Nt)
        searcher.search(q, totalHitCollector);

        if (totalHitCollector.getTotalHits() > 0) {
          TopScoreDocCollector finalQueryHitCollector = TopScoreDocCollector
              .create(Math.max(0, totalHitCollector.getTotalHits()));
          searcher.search(q, finalQueryHitCollector);
          ScoreDoc[] hits = finalQueryHitCollector.topDocs().scoreDocs;
          LOG.info("Title expansion similarity has " + totalHitCollector.getTotalHits() + " hits");

          // Re-score (titleExpansionSimilarity multiplied by
          // titleCoordSimilarity)
          // Sort by final score and timestamp (descending order)
          ArrayList<ScoreDocTimestamp> finalHits = new ArrayList<ScoreDocTimestamp>();
          for (int j = 0; j < hits.length; ++j) {
            int docId = hits[j].doc;
            if (coordHMap.containsKey(docId)) {
              float docScore = hits[j].score;
              Document fullDocument = searcher.doc(docId);
              long timestamp = Long.parseLong(fullDocument.get(TRECIndexerRunnable.StatusField.EPOCH.name));

              finalHits.add(new ScoreDocTimestamp(docId, docScore * coordHMap.get(docId), timestamp, fullDocument));
            }
          }
          Collections.sort(finalHits, new ScoreDocComparator());

          LOG.info("Hit " + finalHits.size() + " documents");
          if (0 != finalHits.size()) {
            LOG.info("Quering:" + titleExpansionQuery.toString() + ", Found " + finalHits.size() + " hits");
          }

          ArrayList<String> tweetList = new ArrayList<String>();
          HashMap<String, Float> scoreMap = new HashMap<String, Float>();

          for (int j = 0; j < finalHits.size(); ++j) {
            int docId = finalHits.get(j).doc;
            Document d = finalHits.get(j).fullDocument;

            if (pushedTweets.size() < dailylimit
                && !pushedTweets.containsKey(d.get(TRECIndexerRunnable.StatusField.ID.name))
                && !isDuplicate(d.get(TRECIndexerRunnable.StatusField.TEXT.name)) && finalHits.get(j).score >= 6) {

              LOG.info(searcher.explain(titleExpansionQuery, docId).toString());
              LOG.info("Multiplied by " + coordHMap.get(docId) + " Final score " + finalHits.get(j).score);
              LOG.info("Raw text " + d.get(TRECIndexerRunnable.StatusField.RAW_TEXT.name) + " "
                  + thisInterestProfile.queryTokenCount);

              tweetList.add(d.get(TRECIndexerRunnable.StatusField.ID.name));
              scoreMap.put(d.get(TRECIndexerRunnable.StatusField.ID.name), finalHits.get(j).score);

              LOG.info("Tweet ID:" + String.valueOf(d.get(TRECIndexerRunnable.StatusField.ID.name)));

              pushedTweets.put(d.get(TRECIndexerRunnable.StatusField.ID.name),
                  d.get(TRECIndexerRunnable.StatusField.TEXT.name));

            }
            if (scenario.equals("A") && (pushedTweets.size() >= dailylimit)) {
              shutDown = true;
              break;

            }
          }
          if (tweetList.size() > 0) {
            if (scenario.equals("A"))
              postTweetListScenarioA(tweetList, api);
            else if (scenario.equals("B"))
              postTweetListScenarioB(tweetList, api, scoreMap);
          } else {
            LOG.info("Nothing interesting today, Gonna sleep for regular interval");
          }

        }
      } else {
        LOG.info("For this iteration, no single tweet hit even only the title field");
      }

      if (scenario.equals("A") && !shutDown) {
        now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

      }

      if (scenario.equals("A") && shutDown) {
        now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.set(Calendar.HOUR, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.AM_PM, Calendar.AM);
        tomorrow.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
        tomorrow.setTimeZone(TimeZone.getTimeZone("UTC"));
        LOG.info("Reached dailyLimit, sleep for the rest of the day");
        LOG.info(tomorrow.getTimeInMillis() + " " + now.getTimeInMillis());
        Thread.sleep((long) tomorrow.getTimeInMillis() - now.getTimeInMillis() + 60000);
        now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        shutDown = false;
        LOG.info("Woke up at this new day!");
        pushedTweets.clear();

      }
      reader.close();

    }

    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParseException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
