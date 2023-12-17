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

package io.anserini;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestUtils {
  public static void checkFile(String output, String[] ref) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(output));

    int cnt = 0;
    String s;
    while ((s = br.readLine()) != null) {
      assertEquals(ref[cnt], s);
      cnt++;
    }

    assertEquals(cnt, ref.length);
  }

  // Use this when we're checking TREC run files and we don't care about the scores matching exactly.
  public static void checkRunFileApproximate(String output, String[] ref) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(output));

    int cnt = 0;
    String s;
    while ((s = br.readLine()) != null) {
      String[] refParts = ref[cnt].split(" ");
      String[] sParts = s.split(" ");

      assertEquals(refParts.length, sParts.length);
      assertEquals(refParts[0], sParts[0]);
      assertEquals(refParts[1], sParts[1]);
      assertEquals(refParts[2], sParts[2]);
      assertEquals(refParts[3], sParts[3]);
      // This is the score, check with plenty of tolerance.
      assertEquals(Float.parseFloat(refParts[4]), Float.parseFloat(sParts[4]), 10e-3);
      assertEquals(refParts[5], sParts[5]);

      cnt++;
    }

    assertEquals(cnt, ref.length);
  }
}
