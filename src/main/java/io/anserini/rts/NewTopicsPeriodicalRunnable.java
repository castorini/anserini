package io.anserini.rts;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewTopicsPeriodicalRunnable extends TimerTask {
  public static final Logger LOG = LogManager.getLogger(TRECSearcher.class);

  @Override
  public void run() {
    // TODO Auto-generated method stub

    TRECTopic[] newTopicSet;
    try {
      newTopicSet = TopicPoller.getUpdatedTopic(TRECSearcher.api_base, TRECSearcher.clientid,
          TRECSearcher.interestProfilePath);

      for (int i = 0; i < newTopicSet.length; i++) {
        boolean topicSeen = false;

        for (int j = 0; j < TRECSearcher.topics.length; j++) {
          if (TRECSearcher.topics[j].topid.equals(newTopicSet[i].topid))
            topicSeen = true;
        }

        if (!topicSeen) {

          TopicPoller.writeTRECTopicToDisk(newTopicSet[i]);

          Timer timerA = new Timer();
          TimerTask tasknewA = new TRECScenarioRunnable(TRECSearcher.indexName,
              TRECSearcher.interestProfilePath + newTopicSet[i].topid + ".json",
              TRECSearcher.api_base + "tweet/" + newTopicSet[i].topid + "/:tweetid/" + TRECSearcher.clientid, "A");
          timerA.scheduleAtFixedRate(tasknewA, 30000, TRECSearcher.minuteInterval);
          TRECSearcher.keepTaskInList(tasknewA, timerA);

          Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
          Calendar tomorrow = Calendar.getInstance();
          tomorrow.set(Calendar.HOUR, 0);
          tomorrow.set(Calendar.MINUTE, 0);
          tomorrow.set(Calendar.SECOND, 0);
          tomorrow.set(Calendar.AM_PM, Calendar.AM);
          tomorrow.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
          tomorrow.setTimeZone(TimeZone.getTimeZone("UTC"));

          Timer timerB = new Timer();
          TimerTask tasknewB = new TRECScenarioRunnable(TRECSearcher.indexName,
              TRECSearcher.interestProfilePath + newTopicSet[i].topid + ".json",
              TRECSearcher.api_base + "tweets/" + newTopicSet[i].topid + "/" + TRECSearcher.clientid, "A");
          LOG.info("Scenario B will start at epoch " + tomorrow.getTimeInMillis() + " Now is " + now.getTimeInMillis());
          // [Deprecated!] Scenario A only for this year
          timerB.scheduleAtFixedRate(tasknewB, (long) (tomorrow.getTimeInMillis() - now.getTimeInMillis() + 1000),
              TRECSearcher.dailyInterval);
          TRECSearcher.keepTaskInList(tasknewB, timerB);

        }
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
