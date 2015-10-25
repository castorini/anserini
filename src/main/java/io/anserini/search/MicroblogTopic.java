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

package io.anserini.search;

import com.google.common.base.Preconditions;

public class MicroblogTopic {
  private String query;
  private String id;
  private long time;

  public MicroblogTopic(String id, String query, long time) {
    this.id = Preconditions.checkNotNull(id);
    this.query = Preconditions.checkNotNull(query);
    Preconditions.checkArgument(time > 0);
    this.time = time;
  }

  public String getId() {
    return id;
  }

  public String getQuery() {
    return query;
  }

  public long getQueryTweetTime() {
    return time;
  }
}
