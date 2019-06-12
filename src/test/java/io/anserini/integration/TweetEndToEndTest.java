/**
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

public class TweetEndToEndTest extends EndToEndTest {

  @Override
  protected void init() {
    dataDirPath = "tweets";
    collectionClass = "Tweet";
    generator = "Tweet";
    topicReader = "Microblog";

    fieldNormStatusTotalFields = 1; // text

    // We set that retweets and the tweets with ids larger than tweetMaxId will NOT be indexed!
    termIndexStatusTermCount = 32; // other indexable fields: 4 doc ids + 4 "lang" fields + 4 "screen_name" fields
    termIndexStatusTotFreq = 36;
    storedFieldStatusTotalDocCounts = 4;
    // 24 positions for text fields, plus 3 for each document because of id, screen_name and lang
    termIndexStatusTotPos = 24 + 3 * storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 12;  // 4 tweets * (1 id + 1 text + 1 raw)

    // The search output should be as follows (for Lucene 7.5):
    // 1 Q0 5 1 1.167100 Anserini
    // 1 Q0 3 2 0.693100 Anserini

    // Qrels are at src/test/resources/sample_qrels/Microblog
    // 1 0 1 0
    // 1 0 3 1
    // 1 0 5 0
    // 1 0 6 0
    // 1 0 8 1
    // 1 0 10 1
    evalMetricValue = (float) (0.0/1 + 1.0/2)/3.0f;
    // 2 retrieved docs in total: note that querytweettime filters 1 rel tweet.
    // 1st retrieved doc is not relevant, 2nd retrieved doc is relelevant,
    // and there are 3 relevant docs in qrels.
  }

  @Override
  protected void setIndexingArgs() {
    super.setIndexingArgs();
    indexCollectionArgs.tweetMaxId = 9L;
  }

  @Override
  protected void setSearchArgs() {
    super.setSearchArgs();
    searchArgs.searchtweets = true;
  }

  @Override
  protected void setEvalArgs() {
    super.setEvalArgs();
    evalArgs.longDocids = true;
  }
}
