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

package io.anserini.eval;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

public class RankingResultsTest {
  @Test
  public void testSorting() throws IOException {
    // d2 and d3 ties with score
    ResultDoc d1 = new ResultDoc("d1", 2.02, false, false);
    ResultDoc d2 = new ResultDoc("d2", 1.9998, false, false);
    ResultDoc d3 = new ResultDoc("d3", 1.9998, false, false);
    ResultDoc d4 = new ResultDoc("d4", 1.576, false, false);

    List<ResultDoc> l = new ArrayList<>();
    l.add(d1);
    l.add(d2);
    l.add(d3);
    l.add(d4);
    Collections.sort(l);
    String[] sorted = new String[4];
    for (int i = 0; i < l.size(); i++) {
      sorted[i] = l.get(i).getDocid();
    }

    String[] expected = {"d1", "d3", "d2", "d4"};

    assertArrayEquals(expected, sorted);
  }

  @Test
  public void testSortingWithOptions() throws IOException {
    // d2 and d3 ties with score
    ResultDoc d1 = new ResultDoc("1", 1.0001, true, true);
    ResultDoc d2 = new ResultDoc("2", 1.0001, true, true);
    ResultDoc d3 = new ResultDoc("3", 1.0001, true, true);
    ResultDoc d4 = new ResultDoc("4", 1.0001, true, true);

    List<ResultDoc> l = new ArrayList<>();
    l.add(d1);
    l.add(d2);
    l.add(d3);
    l.add(d4);
    Collections.sort(l);
    String[] sorted = new String[4];
    for (int i = 0; i < l.size(); i++) {
      sorted[i] = l.get(i).getDocid();
    }

    String[] expected = {"4", "3", "2", "1"};

    assertArrayEquals(expected, sorted);
  }
}
