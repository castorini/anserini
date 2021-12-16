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

package io.anserini.collection;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

// A file in a JsonCollection might not have the required fields that
// we expect. This tests whether the appropriate error is thrown.
public class JsonCollectionErrorCheckingTest {
  private JsonCollection collection;

  @Before
  public void setUp() throws Exception {
    collection = new JsonCollection(Paths.get("src/test/resources/sample_docs/json/collection_errors"));
  }

  @Test(expected = RuntimeException.class)
  public void missingIdField() throws IOException {
    JsonCollection.Document parsed = collection.createFileSegment(
        Paths.get("src/test/resources/sample_docs/json/collection_errors/id_missing.json")).iterator().next();
    parsed.id();
  }
  
  @Test(expected = RuntimeException.class)
  public void missingContentField() throws IOException {
    JsonCollection.Document parsed = collection.createFileSegment(
        Paths.get("src/test/resources/sample_docs/json/collection_errors/contents_missing.json")).iterator().next();
    parsed.contents();
  }
}
