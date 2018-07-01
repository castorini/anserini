/**
 * Anserini: An information retrieval toolkit built on Lucene
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
    termIndexStatusTotPos = 24;   // only "text" fields are indexed with positions
    storedFieldStatusTotalDocCounts = 4;
    storedFieldStatusTotFields = 12;  // 4 tweets * (1 id + 1 text + 1 raw)

    evalMetricValue = (float)(0.0/1+1.0/2)/3.0f; // 2 retrieved docs in total: (please note the querytweettime filters 1 rel tweet)
                                              // 1st retrieved doc is non-rel, 2nd retrieved is rel
                                              // and there are in total 3 rel docs in qrels
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
