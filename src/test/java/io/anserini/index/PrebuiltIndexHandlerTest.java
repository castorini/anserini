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

package io.anserini.index;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.anserini.util.PrebuiltIndexHandler;

public class PrebuiltIndexHandlerTest {
  private PrebuiltIndexHandler handler;

  @Test
  public void testHandler() throws Exception {
    try {
      handler.download();
      handler.decompressIndex();
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Failed to download or decompress index.");
    }
  }

  @Before
  public void setUp() throws Exception {
    handler = new PrebuiltIndexHandler("msmarco-v1-passage"); // this is too big, need to find a smaller one for testing
                                                                        // purposes
    handler.initialize();
  }

  @After
  public void tearDown() throws Exception {
    // delete the index downloaded
    if (handler.getIndexFolderPath() != null && handler.getIndexFolderPath().toFile().exists()) {
      handler.getIndexFolderPath().toFile().delete();
    }
  }

}
