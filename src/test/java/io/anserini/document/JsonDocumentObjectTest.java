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


public class JsonDocumentObjectTest extends DocumentTest<JsonDocument> {
  private String sampleFile = "sampleJsonObject.json";

  @Before
  public void setUp() throws Exception {
    super.setUp();

    String sampleDoc = "[\n" +
      "{\n " +
      "  \"id\": \"doc\",\n" +
      "  \"contents\": \"this is the contents.\"\n" +
      "}";
    Writer writer = new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(sampleFile), "utf-8"));
    writer.write(sampleDoc);

    dType = new JsonDocument(sampleFile);

    HashMap<String, String> doc = new HashMap<>();
    doc.put("id", "doc");
    doc.put("content", "this is the contents.");
    expected.add(doc);
  }

  @After
  public void tearDown() throws Exception {
    File file = new File(sampleFile);
    file.delete();
    super.tearDown();
  }
}
