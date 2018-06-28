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

package io.anserini.document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;


public class JsonLineObjectTest extends DocumentTest<JsonDocument> {
  private String sampleFile = "sampleJsonLineObject.json";

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String sampleDoc = "[\n" +
      "{\n " +
      "  \"id\": \"doc1\",\n" +
      "  \"contents\": \"this is the contents 1.\"\n" +
      "}\n" +
      "{\n " +
      "  \"id\": \"doc2\",\n" +
      "  \"contents\": \"this is the contents 2.\"\n" +
      "}";
    Writer writer = new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(sampleFile), "utf-8"));
    writer.write(sampleDoc);

    dType = new JsonDocument(sampleFile);

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "doc1");
    doc1.put("content", "this is the contents 1.");
    expected.add(doc1);
    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "doc2");
    doc2.put("content", "this is the contents 2.");
    expected.add(doc2);
  }

  @After
  public void tearDown() throws Exception {
    File file = new File(sampleFile);
    file.delete();
    super.tearDown();
  }
}
