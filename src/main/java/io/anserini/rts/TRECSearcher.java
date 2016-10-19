package io.anserini.rts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TRECSearcher {
  public static final Logger LOG = LogManager.getLogger(TRECSearcher.class);

  private static final String HOST_OPTION = "host";
  private static final String INDEX_OPTION = "index";
  private static final String PORT_OPTION = "port";
  private static final String GROUPID_OPTION = "groupid";

  static BufferedWriter scenarioALogWriter;
  static BufferedWriter scenarioBLogWriter;

  /* Change values for the following 3, non-critical variable */
  static final String interestProfilePath = "src/main/java/io/anserini/rts/TREC2016Profiles/";
  private static final String scenarioLogPath = "src/main/java/io/anserini/rts/scenarioLog";
  static final String alias = "WaterlooBaseline";

  static String api_base;
  static String clientid;
  static TRECTopic[] topics;

  private static String groupid;
  static String indexName;

  private static ArrayList<TimerTask> threadList = new ArrayList<TimerTask>();
  private static ArrayList<Timer> timerList = new ArrayList<Timer>();

  static long minuteInterval = 60 * 1000;
  static long dailyInterval = 24 * 60 * minuteInterval;
  /*
   * Organizer suggests that poll the broker API for topics no more frequent
   * than once every hour
   */
  private static long topicCheckInterval = 60 * minuteInterval;

  public void close() throws IOException {
    Indexer.close();
  }

  public static void keepTaskInList(TimerTask tasknew, Timer timer) {
    threadList.add(tasknew);
    timerList.add(timer);
  }

  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(HOST_OPTION, true, "host");
    options.addOption(INDEX_OPTION, true, "index path");
    options.addOption(PORT_OPTION, true, "port");
    options.addOption(GROUPID_OPTION, true, "groupid");

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (org.apache.commons.cli.ParseException e) {
      System.err.println("Error parsing command line: " + e.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(HOST_OPTION) || !cmdline.hasOption(INDEX_OPTION) || !cmdline.hasOption(PORT_OPTION)
        || !cmdline.hasOption(GROUPID_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(TRECSearcher.class.getName(), options);
      System.exit(-1);
    }

    String host = cmdline.getOptionValue(HOST_OPTION);
    groupid = cmdline.getOptionValue(GROUPID_OPTION);
    int port = Integer.parseInt(cmdline.getOptionValue(PORT_OPTION));
    api_base = new String("http://" + host + ":" + port + "/");

    clientid = Registrar.register(api_base, groupid, alias);
    topics = TopicPoller.getInitialTopics(api_base, clientid, interestProfilePath);
    indexName = Indexer.StartIndexing(INDEX_OPTION);

    Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    Calendar tomorrow = Calendar.getInstance();
    tomorrow.set(Calendar.HOUR, 0);
    tomorrow.set(Calendar.MINUTE, 0);
    tomorrow.set(Calendar.SECOND, 0);
    tomorrow.set(Calendar.AM_PM, Calendar.AM);
    tomorrow.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
    tomorrow.setTimeZone(TimeZone.getTimeZone("UTC"));

    File file = new File(scenarioLogPath);
    boolean isDirectoryCreated = file.mkdir();
    if (isDirectoryCreated) {
      LOG.info("Scenario log profile directory successfully made");
    } else {
      FileUtils.deleteDirectory(file);
      file.mkdir();
      LOG.info("Scenario log profile directory successfully covered");
    }
    scenarioALogWriter = new BufferedWriter(new FileWriter(new File(scenarioLogPath + "/scenarioALog")));
    scenarioBLogWriter = new BufferedWriter(new FileWriter(new File(scenarioLogPath + "/scenarioBLog")));

    for (TRECTopic topic : topics) {
      Timer timer = new Timer();
      TimerTask tasknew = new TRECScenarioRunnable(indexName, interestProfilePath + topic.topid + ".json",
          api_base + "tweet/" + topic.topid + "/:tweetid/" + clientid, "A");

      // Schedule scenario A search task every minute Interval
      // At the first time, there's a 30000 milliseconds delay for the
      // delay in connecting Twitter Streaming API
      timer.scheduleAtFixedRate(tasknew, 30000, minuteInterval);
      keepTaskInList(tasknew, timer);

    }

    for (TRECTopic topic : topics) {
      Timer timer = new Timer();
      TimerTask tasknew = new TRECScenarioRunnable(indexName, interestProfilePath + topic.topid + ".json",
          api_base + "tweets/" + topic.topid + "/" + clientid, "B");
      LOG.info("Scenario B will start at epoch " + tomorrow.getTimeInMillis() + " Now is " + now.getTimeInMillis());

      // [Deprecated!] Scenario A only for this year
      // Schedule scenario B search task every day at 0'00'01
      // The 1000 milliseconds delay is to ensure that the search action
      // lies exactly at the new day, as long as 1000 milliseconds delay
      // will not discount the reward.
      // At the first time, there's a delay to wait till a new day.
      timer.scheduleAtFixedRate(tasknew, (long) (tomorrow.getTimeInMillis() - now.getTimeInMillis() + 1000),
          dailyInterval);
      keepTaskInList(tasknew, timer);
    }

    Timer timer = new Timer();
    TimerTask tasknew = new NewTopicsPeriodicalRunnable();
    LOG.info("Successfully set up the thread for periodically check new topics every" + topicCheckInterval);
    timer.scheduleAtFixedRate(tasknew, 0, topicCheckInterval);
    keepTaskInList(tasknew, timer);

    Indexer.join();
  }
}
