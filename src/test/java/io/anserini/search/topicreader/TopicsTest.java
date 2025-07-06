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

package io.anserini.search.topicreader;

import org.junit.Test;

import static io.anserini.search.topicreader.Topics.*;
import static org.junit.Assert.assertEquals;

public class TopicsTest {

  @Test
  public void testBasic() {
    // Not intended to be exhaustive, just spot checks.
    assertEquals(MSMARCO_PASSAGE_DEV_SUBSET, Topics.getByName("MSMARCO_PASSAGE_DEV_SUBSET"));
    assertEquals(TREC2019_DL_PASSAGE, Topics.getByName("TREC2019_DL_PASSAGE"));
    assertEquals(TREC2020_DL, Topics.getByName("TREC2020_DL"));
  }

  @Test
  public void testSymbols() {
    // Not practical to exhaustively test all aliases, just spot checks.
    assertEquals(MSMARCO_PASSAGE_DEV_SUBSET, Topics.getByName("msmarco-passage-dev"));
    assertEquals(MSMARCO_PASSAGE_DEV_SUBSET, Topics.getByName("msmarco-v1-passage-dev"));
    assertEquals(MSMARCO_PASSAGE_DEV_SUBSET, Topics.getByName("msmarco-v1-passage.dev"));

    assertEquals(TREC2020_DL, Topics.getByName("dl20-passage"));
    assertEquals(TREC2020_DL, Topics.getByName("dl20-doc"));
    assertEquals(TREC2021_DL, Topics.getByName("dl21-passage"));
    assertEquals(TREC2021_DL, Topics.getByName("dl21-doc"));
    assertEquals(TREC2022_DL, Topics.getByName("dl22-passage"));
    assertEquals(TREC2022_DL, Topics.getByName("dl22-doc"));
    assertEquals(TREC2023_DL, Topics.getByName("dl23-passage"));
    assertEquals(TREC2023_DL, Topics.getByName("dl23-doc"));
  }
}
