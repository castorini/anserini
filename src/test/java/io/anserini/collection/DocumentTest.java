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

package io.anserini.collection;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentTest extends LuceneTestCase {
  protected List<Map<String, String>> expected;
  protected List<Path> rawFiles;
  protected List<String> rawDocs;
  protected Path tmpPath;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    expected = new ArrayList<>();
    rawFiles = new ArrayList<>();
    rawDocs = new ArrayList<>();
    tmpPath = null;
  }

  public Path createFile(String doc) {
    try {
      tmpPath = createTempFile();
      Writer writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(tmpPath.toFile()), "utf-8"));
      writer.write(doc);
      writer.close();
    } catch (IOException e) {}

    return tmpPath;
  }

  @After
  public void tearDown() throws Exception {
    if (tmpPath != null) {
      File file = tmpPath.toFile();
      file.delete();
    }
    // Call garbage collector for Windows compatibility
    System.gc(); 
    super.tearDown();
  }
}
