/*
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

package io.anserini.search.topicreader;

import org.junit.Test;

import io.anserini.util.Qrels;
import io.anserini.util.QrelsReader;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QrelsReaderTest {

  @Test
  public void testNewswireTopics_TopicIdsAsStrings() throws IOException {
    String qrels;

    qrels = QrelsReader.getQrelsWithStringIds(Qrels.TREC1_ADHOC);
    assertNotNull(qrels);
    assertEquals("51 0 AP880301-0271 1", qrels.split("\n")[0]);
  }
}
