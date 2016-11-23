/**
 * Twitter Tools
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.index;

import io.anserini.analysis.TweetAnalyzer;
import io.anserini.document.twitter.JsonStatusCorpusReader;
import io.anserini.document.twitter.Status;
import io.anserini.document.twitter.StatusStream;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tools.bzip2.CBZip2InputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * Reference implementation for indexing statuses.
 */
public class IndexTweets {
  private static final Logger LOG = LogManager.getLogger(IndexTweets.class);

  public static final Analyzer ANALYZER = new TweetAnalyzer();
  public static String corpusFormat = null;

  private IndexTweets() {}

  public static enum StatusField {
    ID("id"),
    SCREEN_NAME("screen_name"),
    EPOCH("epoch"),
    TEXT("text"),
    LANG("lang"),
    IN_REPLY_TO_STATUS_ID("in_reply_to_status_id"),
    IN_REPLY_TO_USER_ID("in_reply_to_user_id"),
    FOLLOWERS_COUNT("followers_count"),
    FRIENDS_COUNT("friends_count"),
    STATUSES_COUNT("statuses_count"),
    RETWEETED_STATUS_ID("retweeted_status_id"),
    RETWEETED_USER_ID("retweeted_user_id"),
    RETWEET_COUNT("retweet_count");

    public final String name;

    StatusField(String s) {
      name = s;
    }
  };

  private static final String HELP_OPTION = "h";
  private static final String COLLECTION_OPTION = "collection";
  private static final String INDEX_OPTION = "index";
  private static final String MAX_ID_OPTION = "max_id";
  private static final String DELETES_OPTION = "deletes";
  private static final String OPTIMIZE_OPTION = "optimize";
  private static final String STORE_TERM_VECTORS_OPTION = "store";

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();

    options.addOption(new Option(HELP_OPTION, "show help"));
    options.addOption(new Option(OPTIMIZE_OPTION, "merge indexes into a single segment"));
    options.addOption(new Option(STORE_TERM_VECTORS_OPTION, "store term vectors"));

    options.addOption(OptionBuilder.withArgName("dir").hasArg()
        .withDescription("source collection directory").create(COLLECTION_OPTION));
    options.addOption(OptionBuilder.withArgName("dir").hasArg()
        .withDescription("index location").create(INDEX_OPTION));
    options.addOption(OptionBuilder.withArgName("file").hasArg()
        .withDescription("file with deleted tweetids").create(DELETES_OPTION));
    options.addOption(OptionBuilder.withArgName("id").hasArg()
        .withDescription("max id").create(MAX_ID_OPTION));

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (cmdline.hasOption(HELP_OPTION) || !cmdline.hasOption(COLLECTION_OPTION)
        || !cmdline.hasOption(INDEX_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(IndexTweets.class.getName(), options);
      System.exit(-1);
    }

    String collectionPath = cmdline.getOptionValue(COLLECTION_OPTION);
    String indexPath = cmdline.getOptionValue(INDEX_OPTION);

    final FieldType textOptions = new FieldType();
    textOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    textOptions.setStored(true);
    textOptions.setTokenized(true);        
    if (cmdline.hasOption(STORE_TERM_VECTORS_OPTION)) {
      textOptions.setStoreTermVectors(true);
    }

    LOG.info("collection: " + collectionPath);
    LOG.info("index: " + indexPath);

    LongOpenHashSet deletes = null;
    if (cmdline.hasOption(DELETES_OPTION)) {
      deletes = new LongOpenHashSet();
      File deletesFile = new File(cmdline.getOptionValue(DELETES_OPTION));
      if (!deletesFile.exists()) {
        System.err.println("Error: " + deletesFile + " does not exist!");
        System.exit(-1);
      }
      LOG.info("Reading deletes from " + deletesFile);
      
      FileInputStream fin = new FileInputStream(deletesFile);
      byte[] ignoreBytes = new byte[2];
      fin.read(ignoreBytes); // "B", "Z" bytes from commandline tools
      BufferedReader br = new BufferedReader(new InputStreamReader(new CBZip2InputStream(fin)));

      String s;
      while ((s = br.readLine()) != null) {
        if (s.contains("\t")) {
          deletes.add(Long.parseLong(s.split("\t")[0]));
        } else {
          deletes.add(Long.parseLong(s));
        }
      }
      br.close();
      fin.close();
      LOG.info("Read " + deletes.size() + " tweetids from deletes file.");
    }

    long maxId = Long.MAX_VALUE;
    if (cmdline.hasOption(MAX_ID_OPTION)) {
      maxId = Long.parseLong(cmdline.getOptionValue(MAX_ID_OPTION));
      LOG.info("index: " + maxId);
    }
    
    long startTime = System.currentTimeMillis();
    File file = new File(collectionPath);
    if (!file.exists()) {
      System.err.println("Error: " + file + " does not exist!");
      System.exit(-1);
    }

    StatusStream stream = new JsonStatusCorpusReader(file);

    Directory dir = FSDirectory.open(Paths.get(indexPath));
    final IndexWriterConfig config = new IndexWriterConfig(ANALYZER);

    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

    IndexWriter writer = new IndexWriter(dir, config);
    int cnt = 0;
    Status status;
    try {
      while ((status = stream.next()) != null) {
        if (status.getText() == null) {
          continue;
        }

        // Skip deletes tweetids.
        if (deletes != null && deletes.contains(status.getId())) {
          continue;
        }

        if (status.getId() > maxId) {
          continue;
        }

        cnt++;
        Document doc = new Document();
        doc.add(new LongPoint(StatusField.ID.name, status.getId()));
        doc.add(new StoredField(StatusField.ID.name, status.getId()));
        doc.add(new LongPoint(StatusField.EPOCH.name, status.getEpoch()));
        doc.add(new StoredField(StatusField.EPOCH.name, status.getEpoch()));
        doc.add(new TextField(StatusField.SCREEN_NAME.name, status.getScreenname(), Store.YES));

        doc.add(new Field(StatusField.TEXT.name, status.getText(), textOptions));

        doc.add(new IntPoint(StatusField.FRIENDS_COUNT.name, status.getFollowersCount()));
        doc.add(new StoredField(StatusField.FRIENDS_COUNT.name, status.getFollowersCount()));
        doc.add(new IntPoint(StatusField.FOLLOWERS_COUNT.name, status.getFriendsCount()));
        doc.add(new StoredField(StatusField.FOLLOWERS_COUNT.name, status.getFriendsCount()));
        doc.add(new IntPoint(StatusField.STATUSES_COUNT.name, status.getStatusesCount()));
        doc.add(new StoredField(StatusField.STATUSES_COUNT.name, status.getStatusesCount()));

        long inReplyToStatusId = status.getInReplyToStatusId();
        if (inReplyToStatusId > 0) {
          doc.add(new LongPoint(StatusField.IN_REPLY_TO_STATUS_ID.name, inReplyToStatusId));
          doc.add(new StoredField(StatusField.IN_REPLY_TO_STATUS_ID.name, inReplyToStatusId));
          doc.add(new LongPoint(StatusField.IN_REPLY_TO_USER_ID.name, status.getInReplyToUserId()));
          doc.add(new StoredField(StatusField.IN_REPLY_TO_USER_ID.name, status.getInReplyToUserId()));
        }
        
        String lang = status.getLang();
        if (!lang.equals("unknown")) {
          doc.add(new TextField(StatusField.LANG.name, status.getLang(), Store.YES));
        }
        
        long retweetStatusId = status.getRetweetedStatusId();
        if (retweetStatusId > 0) {
          doc.add(new LongPoint(StatusField.RETWEETED_STATUS_ID.name, retweetStatusId));
          doc.add(new StoredField(StatusField.RETWEETED_STATUS_ID.name, retweetStatusId));
          doc.add(new LongPoint(StatusField.RETWEETED_USER_ID.name, status.getRetweetedUserId()));
          doc.add(new StoredField(StatusField.RETWEETED_USER_ID.name, status.getRetweetedUserId()));
          doc.add(new IntPoint(StatusField.RETWEET_COUNT.name, status.getRetweetCount()));
          doc.add(new StoredField(StatusField.RETWEET_COUNT.name, status.getRetweetCount()));
          if ( status.getRetweetCount() < 0 || status.getRetweetedStatusId() < 0) {
            LOG.warn("Error parsing retweet fields of " + status.getId());
          }
        }
        
        writer.addDocument(doc);
        if (cnt % 100000 == 0) {
          LOG.info(cnt + " statuses indexed");
        }
      }

      LOG.info(String.format("Total of %s statuses added", cnt));
      
      if (cmdline.hasOption(OPTIMIZE_OPTION)) {
        LOG.info("Merging segments...");
        writer.forceMerge(1);
        LOG.info("Done!");
      }

      LOG.info("Total elapsed time: " + (System.currentTimeMillis() - startTime) + "ms");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      writer.close();
      dir.close();
      stream.close();
    }
  }
}
