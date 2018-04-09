package io.anserini.search;

import io.anserini.analysis.TweetAnalyzer;
import io.anserini.util.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MockDirectoryWrapper;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will contain setup and teardown code for testing
 */
@SuppressWarnings("deprecation")
@Deprecated
public class TweetSearchTest extends LuceneTestCase {
  /*protected static final String FILTER_FIELD_NAME = IndexTweets.StatusField.ID.name;
  protected static final String SEARCH_FIELD_NAME = IndexTweets.StatusField.TEXT.name;
  protected static final Analyzer TEST_ANALYZER = new TweetAnalyzer();
  protected static final String DEFAULT_QID = "1";

  protected Directory DIRECTORY;
  protected IndexWriter testWriter;

  protected String addTweet(long id, String screenName, String created_at, String text) {
    return String.format("{\"id\":%d,\"user\":{\"screen_name\":\"%s\",\"name\":\"\"," +
            "\"profile_image_url\":\"\"," +
            "\"followers_count\":-1,\"friends_count\":-1,\"statuses_count\":-1}," +
            "\"created_at\":\"%s\",\"text\":\"%s\",\"lang\":\"EN\"," +
            "\"in_reply_to_status_id\":-1,\"retweeted_status_id\":-1}",
            id, screenName, created_at, text);
  }

  protected Document addTestTweet(String jsonTweetStr) throws IOException {
    Status status = Status.fromJson(jsonTweetStr);
    System.out.println(status.getEpoch());
    final FieldType textOptions = new FieldType();
    textOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    textOptions.setStored(true);
    textOptions.setTokenized(true);
    textOptions.setStoreTermVectors(true);
    Document doc = new Document();
    doc.add(new LongPoint(IndexTweets.StatusField.ID.name, status.getId()));
    doc.add(new StoredField(IndexTweets.StatusField.ID.name, status.getId()));
    doc.add(new LongPoint(IndexTweets.StatusField.EPOCH.name, status.getEpoch()));
    //doc.add(new StoredField(IndexTweets.StatusField.EPOCH.name, status.getEpoch()));
    doc.add(new TextField(IndexTweets.StatusField.SCREEN_NAME.name, status.getScreenname(), Field.Store.YES));

    doc.add(new Field(IndexTweets.StatusField.TEXT.name, status.getText(), textOptions));

    doc.add(new IntPoint(IndexTweets.StatusField.FRIENDS_COUNT.name, status.getFollowersCount()));
    doc.add(new StoredField(IndexTweets.StatusField.FRIENDS_COUNT.name, status.getFollowersCount()));
    doc.add(new IntPoint(IndexTweets.StatusField.FOLLOWERS_COUNT.name, status.getFriendsCount()));
    doc.add(new StoredField(IndexTweets.StatusField.FOLLOWERS_COUNT.name, status.getFriendsCount()));
    doc.add(new IntPoint(IndexTweets.StatusField.STATUSES_COUNT.name, status.getStatusesCount()));
    doc.add(new StoredField(IndexTweets.StatusField.STATUSES_COUNT.name, status.getStatusesCount()));

    long inReplyToStatusId = status.getInReplyToStatusId();
    if (inReplyToStatusId > 0) {
      doc.add(new LongPoint(IndexTweets.StatusField.IN_REPLY_TO_STATUS_ID.name, inReplyToStatusId));
      doc.add(new StoredField(IndexTweets.StatusField.IN_REPLY_TO_STATUS_ID.name, inReplyToStatusId));
      doc.add(new LongPoint(IndexTweets.StatusField.IN_REPLY_TO_USER_ID.name, status.getInReplyToUserId()));
      doc.add(new StoredField(IndexTweets.StatusField.IN_REPLY_TO_USER_ID.name, status.getInReplyToUserId()));
    }

    String lang = status.getLang();
    if (!lang.equals("unknown")) {
      doc.add(new TextField(IndexTweets.StatusField.LANG.name, status.getLang(), Field.Store.YES));
    }

    long retweetStatusId = status.getRetweetedStatusId();
    if (retweetStatusId > 0) {
      doc.add(new LongPoint(IndexTweets.StatusField.RETWEETED_STATUS_ID.name, retweetStatusId));
      doc.add(new StoredField(IndexTweets.StatusField.RETWEETED_STATUS_ID.name, retweetStatusId));
      doc.add(new LongPoint(IndexTweets.StatusField.RETWEETED_USER_ID.name, status.getRetweetedUserId()));
      doc.add(new StoredField(IndexTweets.StatusField.RETWEETED_USER_ID.name, status.getRetweetedUserId()));
      doc.add(new IntPoint(IndexTweets.StatusField.RETWEET_COUNT.name, status.getRetweetCount()));
      doc.add(new StoredField(IndexTweets.StatusField.RETWEET_COUNT.name, status.getRetweetCount()));
    }

    testWriter.addDocument(doc);
    testWriter.commit();
    return doc;
  }

  *//**
   * MUST call super
   * constructs the necessary rerankers and extractorchains
   * @throws Exception
   *//*
  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    // Use a RAMDirectory instead of MemoryIndex because we might test with multiple documents
    DIRECTORY = new MockDirectoryWrapper(new Random(), new RAMDirectory());
    testWriter = new IndexWriter(DIRECTORY, new IndexWriterConfig(TEST_ANALYZER));
  }

  *//**
   * MUST call super
   * @throws Exception
   *//*
  @Override
  @After
  public void tearDown() throws Exception {
    super.tearDown();
    testWriter.close();
  }

  protected void assertRetrieIDs(int[] expected, List<String> tweetTexts, MicroblogTopic topic) throws IOException {
    List<Document> addedDocs = new ArrayList<>();
    for (String docText : tweetTexts) {
      Document testDoc = addTestTweet(docText);
      addedDocs.add(testDoc);
    }
    testWriter.forceMerge(1);

    String queryStr = topic.getQuery();
    IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(DIRECTORY));
    IndexReader reader = DirectoryReader.open(DIRECTORY);
    //Query filter = LongPoint.newRangeQuery(FILTER_FIELD_NAME, 0L, topic.getQueryTweetTime());
    Query filter = LongPoint.newRangeQuery(IndexTweets.StatusField.EPOCH.name, 1364551421L, 1364576621L);
    Query query = AnalyzerUtils.buildBagOfWordsQuery(SEARCH_FIELD_NAME, TEST_ANALYZER, queryStr);
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(filter, BooleanClause.Occur.FILTER);
    builder.add(query, BooleanClause.Occur.MUST);
    Query q = builder.build();

    TopDocs hits = searcher.search(q, 1000);
    int[] returned = new int[hits.scoreDocs.length];
    for (int i = 0; i < hits.scoreDocs.length; i++) {
      int docId = hits.scoreDocs[i].doc;
      Document d = reader.document(docId);
      IndexableField id = d.getField(FILTER_FIELD_NAME);
      returned[i] = Integer.parseInt(id.stringValue());
    }
    assertArrayEquals(expected, returned);
  }

  @Test
  public void test1() throws Exception {
    MicroblogTopic topic = new MicroblogTopic(DEFAULT_QID, "document test", 1364576400); //Fri, Mar 29 2013 17:00:00 GMT
    List<String> tweets = new ArrayList<>();
    tweets.add(addTweet(1364580221, "a", "Fri Mar 29 18:03:41 +0000 2013", "this is document one"));
    tweets.add(addTweet(1364555021, "b", "Fri Mar 29 11:03:41 +0000 2013", "this is document two"));
    tweets.add(addTweet(1364558621, "c", "Fri Mar 29 12:03:41 +0000 2013", "this is document three"));
    tweets.add(addTweet(1364576621, "d", "Fri Mar 29 17:03:41 +0000 2013", "this is document four"));
    tweets.add(addTweet(1364551421, "e", "Fri Mar 29 10:03:41 +0000 2013", "this is document five"));
    tweets.add(addTweet(1364583821, "f", "Sat Apr 07 13:05:42 +0000 2018", "this is document six"));

    int[] expected = {1364555021, 1364558621, 1364551421};
    assertRetrieIDs(expected, tweets, topic);
  }*/
}
