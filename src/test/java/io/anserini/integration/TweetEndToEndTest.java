/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.integration;

import io.anserini.collection.TweetCollection;
import io.anserini.index.IndexCollection;
import io.anserini.index.generator.TweetGenerator;

import java.util.Map;

public class TweetEndToEndTest extends EndToEndTest {
  // Note that in the test cases, we have:
  // {... "id":1,"id_str":"1","text":"RT This is a Retweet and will NOT NOT be indexed!" ... }
  // {... "id":10,"id_str":"10","text":"This tweet won't be indexed since the maxId is 9" ... }
  //
  // src/test/resources/sample_docs/tweets/tweets1: 5 JSON objects, 2 deletes
  // src/test/resources/sample_docs/tweets/tweets2: 4 JSON objects, 1 deletes
  //
  // Thus, there should be a total of 4 documents indexed: 9 objects - 5 skipped
  @Override
  protected IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/tweets/collection1";
    indexArgs.collectionClass = TweetCollection.class.getSimpleName();
    indexArgs.generatorClass = TweetGenerator.class.getSimpleName();
    indexArgs.tweetMaxId = 9L;

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    // Note that based on our settings, retweets and tweets with id > 9 will not be indexed.

    docCount = 4;
    docFieldCount = 3; // id, raw, contents

    referenceDocs.put("3", Map.of(
        "contents", "This tweet will be indexed thanks",
        "raw", "{\"created_at\":\"Thu Aug 11 22:57:52 +0000 2016\",\"id\":3,\"id_str\":\"3\",\"text\":\"This tweet will be indexed thanks.\",\"source\":\"\\u003ca href=\\\"http:\\/\\/twitter.com\\/download\\/android\\\" rel=\\\"nofollow\\\"\\u003eTwitter for Android\\u003c\\/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":3358015773,\"id_str\":\"3358015773\",\"name\":\"Cami\",\"screen_name\":\"B\",\"location\":\"Ciudad Aut\\u00f3noma de Buenos Aire\",\"url\":null,\"description\":\"15.Geminiana\\u264a Ig: CamiiMariana15 Snap: camilaracabutto\",\"protected\":false,\"verified\":false,\"followers_count\":392,\"friends_count\":307,\"listed_count\":0,\"favourites_count\":11254,\"statuses_count\":21876,\"created_at\":\"Sat Jul 04 04:32:40 +0000 2015\",\"utc_offset\":-25200,\"time_zone\":\"Pacific Time (US & Canada)\",\"geo_enabled\":false,\"lang\":\"es\",\"contributors_enabled\":false,\"is_translator\":false,\"profile_background_color\":\"000000\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_tile\":false,\"profile_link_color\":\"9266CC\",\"profile_sidebar_border_color\":\"000000\",\"profile_sidebar_fill_color\":\"000000\",\"profile_text_color\":\"000000\",\"profile_use_background_image\":false,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/742940112527429636\\/2EcOpkFu_normal.jpg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/742940112527429636\\/2EcOpkFu_normal.jpg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/3358015773\\/1470945786\",\"default_profile\":false,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[],\"urls\":[],\"user_mentions\":[{\"screen_name\":\"Jul1et4wizz\",\"name\":\"Julieta\",\"id\":1599099673,\"id_str\":\"1599099673\",\"indices\":[3,15]}],\"symbols\":[]},\"favorited\":false,\"retweeted\":false,\"filter_level\":\"low\",\"lang\":\"en\",\"timestamp_ms\":\"1470956272659\"}"));
    referenceDocs.put("5", Map.of(
        "contents", "Can you think of more interesting contents",
        "raw", "{\"created_at\":\"Thu Aug 11 23:57:52 +0000 2016\",\"id\":5,\"id_str\":\"5\",\"text\":\"Can you think of more interesting contents?\",\"source\":\"\\u003ca href=\\\"http:\\/\\/twitter.com\\/download\\/android\\\" rel=\\\"nofollow\\\"\\u003eTwitter for Android\\u003c\\/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":3358015773,\"id_str\":\"3358015773\",\"name\":\"Cami\",\"screen_name\":\"C\",\"location\":\"Ciudad Aut\\u00f3noma de Buenos Aire\",\"url\":null,\"description\":\"15.Geminiana\\u264a Ig: CamiiMariana15 Snap: camilaracabutto\",\"protected\":false,\"verified\":false,\"followers_count\":392,\"friends_count\":307,\"listed_count\":0,\"favourites_count\":11254,\"statuses_count\":21876,\"created_at\":\"Sat Jul 04 04:32:40 +0000 2015\",\"utc_offset\":-25200,\"time_zone\":\"Pacific Time (US & Canada)\",\"geo_enabled\":false,\"lang\":\"es\",\"contributors_enabled\":false,\"is_translator\":false,\"profile_background_color\":\"000000\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"profile_background_tile\":false,\"profile_link_color\":\"9266CC\",\"profile_sidebar_border_color\":\"000000\",\"profile_sidebar_fill_color\":\"000000\",\"profile_text_color\":\"000000\",\"profile_use_background_image\":false,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/742940112527429636\\/2EcOpkFu_normal.jpg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/742940112527429636\\/2EcOpkFu_normal.jpg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/3358015773\\/1470945786\",\"default_profile\":false,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[],\"urls\":[],\"user_mentions\":[{\"screen_name\":\"Jul1et4wizz\",\"name\":\"Julieta\",\"id\":1599099673,\"id_str\":\"1599099673\",\"indices\":[3,15]}],\"symbols\":[]},\"favorited\":false,\"retweeted\":false,\"filter_level\":\"low\",\"lang\":\"cn\",\"timestamp_ms\":\"1470956272659\"}"));
    referenceDocs.put("6", Map.of(
        "contents", "We have some real contents here thanks",
        "raw", "{\"created_at\":\"Thu Aug 11 21:57:50 +0000 2016\",\"id\":6,\"id_str\":\"6\",\"text\":\"We have some real contents here thanks https:\\/\\/t.co\\/1a2b3c\",\"source\":\"\\u003ca href=\\\"http:\\/\\/twitter.com\\/download\\/android\\\" rel=\\\"nofollow\\\"\\u003eTwitter for Android\\u003c\\/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":763875115104960516,\"id_str\":\"763875115104960516\",\"name\":\"Esequiel Manson\",\"screen_name\":\"X\",\"location\":\"San Miguel, Argentina\",\"url\":null,\"description\":null,\"protected\":false,\"verified\":false,\"followers_count\":0,\"friends_count\":2,\"listed_count\":0,\"favourites_count\":2,\"statuses_count\":2,\"created_at\":\"Thu Aug 11 23:09:54 +0000 2016\",\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":false,\"lang\":\"es\",\"contributors_enabled\":false,\"is_translator\":false,\"profile_background_color\":\"F5F8FA\",\"profile_background_image_url\":\"\",\"profile_background_image_url_https\":\"\",\"profile_background_tile\":false,\"profile_link_color\":\"2B7BB9\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/763877709353193472\\/Nhe0IMQI_normal.jpg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/763877709353193472\\/Nhe0IMQI_normal.jpg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/763875115104960516\\/1470957611\",\"default_profile\":true,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[{\"text\":\"perfil\",\"indices\":[0,7]},{\"text\":\"tattoo\",\"indices\":[8,15]},{\"text\":\"feo\",\"indices\":[16,20]},{\"text\":\"paisaje\",\"indices\":[21,29]}],\"urls\":[],\"user_mentions\":[],\"symbols\":[],\"media\":[{\"id\":763887130565287937,\"id_str\":\"763887130565287937\",\"indices\":[30,53],\"media_url\":\"http:\\/\\/pbs.twimg.com\\/media\\/CpnfTEnWIAERJmq.jpg\",\"media_url_https\":\"https:\\/\\/pbs.twimg.com\\/media\\/CpnfTEnWIAERJmq.jpg\",\"url\":\"https:\\/\\/t.co\\/au5Gk5k4Wd\",\"display_url\":\"pic.twitter.com\\/au5Gk5k4Wd\",\"expanded_url\":\"http:\\/\\/twitter.com\\/EsequielManson\\/status\\/763887179798020096\\/photo\\/1\",\"type\":\"photo\",\"sizes\":{\"medium\":{\"w\":1200,\"h\":1200,\"resize\":\"fit\"},\"thumb\":{\"w\":150,\"h\":150,\"resize\":\"crop\"},\"small\":{\"w\":680,\"h\":680,\"resize\":\"fit\"},\"large\":{\"w\":2048,\"h\":2048,\"resize\":\"fit\"}}}]},\"extended_entities\":{\"media\":[{\"id\":763887130565287937,\"id_str\":\"763887130565287937\",\"indices\":[30,53],\"media_url\":\"http:\\/\\/pbs.twimg.com\\/media\\/CpnfTEnWIAERJmq.jpg\",\"media_url_https\":\"https:\\/\\/pbs.twimg.com\\/media\\/CpnfTEnWIAERJmq.jpg\",\"url\":\"https:\\/\\/t.co\\/au5Gk5k4Wd\",\"display_url\":\"pic.twitter.com\\/au5Gk5k4Wd\",\"expanded_url\":\"http:\\/\\/twitter.com\\/EsequielManson\\/status\\/763887179798020096\\/photo\\/1\",\"type\":\"photo\",\"sizes\":{\"medium\":{\"w\":1200,\"h\":1200,\"resize\":\"fit\"},\"thumb\":{\"w\":150,\"h\":150,\"resize\":\"crop\"},\"small\":{\"w\":680,\"h\":680,\"resize\":\"fit\"},\"large\":{\"w\":2048,\"h\":2048,\"resize\":\"fit\"}}}]},\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"filter_level\":\"low\",\"lang\":\"und\",\"timestamp_ms\":\"1470959870658\"}"));
    referenceDocs.put("8", Map.of(
        "contents", "test adding more tweet",
        "raw", "{\"created_at\":\"Thu Aug 11 22:57:50 +0000 2016\",\"id\":8,\"id_str\":\"8\",\"text\":\"test adding more tweets\",\"source\":\"\\u003ca href=\\\"http:\\/\\/twitter.com\\/download\\/android\\\" rel=\\\"nofollow\\\"\\u003eTwitter for Android\\u003c\\/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":763875115104960516,\"id_str\":\"763875115104960516\",\"name\":\"Esequiel Manson\",\"screen_name\":\"Y\",\"location\":\"San Miguel, Argentina\",\"url\":null,\"description\":null,\"protected\":false,\"verified\":false,\"followers_count\":0,\"friends_count\":2,\"listed_count\":0,\"favourites_count\":2,\"statuses_count\":2,\"created_at\":\"Thu Aug 11 23:09:54 +0000 2016\",\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":false,\"lang\":\"es\",\"contributors_enabled\":false,\"is_translator\":false,\"profile_background_color\":\"F5F8FA\",\"profile_background_image_url\":\"\",\"profile_background_image_url_https\":\"\",\"profile_background_tile\":false,\"profile_link_color\":\"2B7BB9\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/763877709353193472\\/Nhe0IMQI_normal.jpg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/763877709353193472\\/Nhe0IMQI_normal.jpg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/763875115104960516\\/1470957611\",\"default_profile\":true,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[{\"text\":\"perfil\",\"indices\":[0,7]},{\"text\":\"tattoo\",\"indices\":[8,15]},{\"text\":\"feo\",\"indices\":[16,20]},{\"text\":\"paisaje\",\"indices\":[21,29]}],\"urls\":[],\"user_mentions\":[],\"symbols\":[],\"media\":[{\"id\":763887130565287937,\"id_str\":\"763887130565287937\",\"indices\":[30,53],\"media_url\":\"http:\\/\\/pbs.twimg.com\\/media\\/CpnfTEnWIAERJmq.jpg\",\"media_url_https\":\"https:\\/\\/pbs.twimg.com\\/media\\/CpnfTEnWIAERJmq.jpg\",\"url\":\"https:\\/\\/t.co\\/au5Gk5k4Wd\",\"display_url\":\"pic.twitter.com\\/au5Gk5k4Wd\",\"expanded_url\":\"http:\\/\\/twitter.com\\/EsequielManson\\/status\\/763887179798020096\\/photo\\/1\",\"type\":\"photo\",\"sizes\":{\"medium\":{\"w\":1200,\"h\":1200,\"resize\":\"fit\"},\"thumb\":{\"w\":150,\"h\":150,\"resize\":\"crop\"},\"small\":{\"w\":680,\"h\":680,\"resize\":\"fit\"},\"large\":{\"w\":2048,\"h\":2048,\"resize\":\"fit\"}}}]},\"extended_entities\":{\"media\":[{\"id\":763887130565287937,\"id_str\":\"763887130565287937\",\"indices\":[30,53],\"media_url\":\"http:\\/\\/pbs.twimg.com\\/media\\/CpnfTEnWIAERJmq.jpg\",\"media_url_https\":\"https:\\/\\/pbs.twimg.com\\/media\\/CpnfTEnWIAERJmq.jpg\",\"url\":\"https:\\/\\/t.co\\/au5Gk5k4Wd\",\"display_url\":\"pic.twitter.com\\/au5Gk5k4Wd\",\"expanded_url\":\"http:\\/\\/twitter.com\\/EsequielManson\\/status\\/763887179798020096\\/photo\\/1\",\"type\":\"photo\",\"sizes\":{\"medium\":{\"w\":1200,\"h\":1200,\"resize\":\"fit\"},\"thumb\":{\"w\":150,\"h\":150,\"resize\":\"crop\"},\"small\":{\"w\":680,\"h\":680,\"resize\":\"fit\"},\"large\":{\"w\":2048,\"h\":2048,\"resize\":\"fit\"}}}]},\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"filter_level\":\"low\",\"lang\":\"ab\",\"timestamp_ms\":\"1470959870658\"}"));

    fieldNormStatusTotalFields = 1; // text
    termIndexStatusTermCount = 32; // other indexable fields: 4 doc ids + 4 "lang" fields + 4 "screen_name" fields
    termIndexStatusTotFreq = 36;
    storedFieldStatusTotalDocCounts = 4;
    // 24 positions for text fields, plus 3 for each document because of id, screen_name and lang
    termIndexStatusTotPos = 24 + 3 * storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 12;  // 4 tweets * (1 id + 1 text + 1 raw)
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "Microblog";
    topicFile = "src/test/resources/sample_topics/Microblog";

    testQueries.put("bm25", createDefaultSearchArgs().bm25().searchTweets());
    referenceRunOutput.put("bm25", new String[] {
        "1 Q0 5 1 0.614300 Anserini",
        "1 Q0 3 2 0.364800 Anserini" });
  }
}
