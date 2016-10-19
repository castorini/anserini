package io.anserini.document.twitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import com.google.common.base.Joiner;

public class JsonTweetsBlockTest {
  private static final String createTweet(long id, String text) {
    return "{\"id\":" + id + ",\"text\":\"" + text + "\",\"user\":{\"screen_name\":\"foo\",\"name\":\"foo\",\"profile_image_url\":\"foo\",\"followers_count\":1,\"friends_count\":1,\"statuses_count\":1},\"created_at\":\"Fri Feb 01 00:00:00 +0000 2013\"}";
  }

  @Test(expected=NoSuchElementException.class)
  public void testNoSuchElementException() throws Exception {
    String[] raw = {
        createTweet(1, "a"),
    };

    JsonTweetsBlock tweets =
        new JsonTweetsBlock(new ByteArrayInputStream(Joiner.on("\n").join(raw).getBytes()));

    Iterator<Status> iter = tweets.iterator();
    assertTrue(iter.next() != null);
    iter.next();
  }

  @Test
  public void test1() throws Exception {
    String[] raw = {
        createTweet(1, "a"),
        createTweet(2, "b"),
        createTweet(3, "c"),
        createTweet(4, "d"),
    };

    JsonTweetsBlock tweets =
        new JsonTweetsBlock(new ByteArrayInputStream(Joiner.on("\n").join(raw).getBytes()));

    Status tweet = null;
    Iterator<Status> iter = tweets.iterator();
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());

    tweet = iter.next();
    assertEquals(tweet.getId(), 1);

    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());

    tweet = iter.next();
    assertEquals(tweet.getId(), 2);

    tweet = iter.next();
    assertEquals(tweet.getId(), 3);

    assertTrue(iter.hasNext());

    tweet = iter.next();
    assertEquals(tweet.getId(), 4);

    assertFalse(iter.hasNext());
    assertFalse(iter.hasNext());
  }

  @Test
  public void test2() throws Exception {
    String[] raw = {
        "{}",
        createTweet(1, "a"),
        "{}",
        createTweet(2, "b"),
        "{}",
        createTweet(3, "c"),
        createTweet(4, "d"),
        "{}",
        "{}",
    };

    JsonTweetsBlock tweets =
        new JsonTweetsBlock(new ByteArrayInputStream(Joiner.on("\n").join(raw).getBytes()));

    Status tweet = null;
    Iterator<Status> iter = tweets.iterator();
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());

    tweet = iter.next();
    assertEquals(tweet.getId(), 1);

    tweet = iter.next();
    assertEquals(tweet.getId(), 2);

    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());

    tweet = iter.next();
    assertEquals(tweet.getId(), 3);

    tweet = iter.next();
    assertEquals(tweet.getId(), 4);

    assertFalse(iter.hasNext());
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(JsonTweetsBlockTest.class);
  }
}
