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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.io.FileUtils;

import io.anserini.StdOutStdErrRedirectableTestCase;
import io.anserini.util.PrebuiltIndexHandler;
import static org.junit.Assert.assertTrue;

public class PrebuiltIndexHandlerTest extends StdOutStdErrRedirectableTestCase {
  private PrebuiltIndexHandler handler;
  private Path originalIndexPath;
  private boolean usingTempHandler = false;

  @Before
  public void setUp() throws Exception {
    handler = new PrebuiltIndexHandler("cacm"); // we use a lightweight index for testing
    handler.initialize();

    redirectStdOut();
    redirectStdErr();
  }

  @After
  public void cleanUp() throws Exception {
    restoreStdOut();
    restoreStdErr();
  }

  @Test
  public void testHandler() throws Exception {
    downloadAndDecompressIndex(handler);
  }

  @Test
  public void testCustomCacheDirectory() throws Exception {
    Path tempDir = Files.createTempDirectory("anserini-test-cache");
    
    if (handler.getIndexFolderPath() != null) {
      originalIndexPath = handler.getIndexFolderPath();
    }
    
    handler = new PrebuiltIndexHandler("cacm", tempDir.toString());
    handler.initialize();
    usingTempHandler = true;
    
    try {
      downloadAndDecompressIndex(handler);
      
      String decompressedPath = handler.getIndexFolderPath().toString();
      assertTrue(decompressedPath.startsWith(tempDir.toString()));
      
    } finally {
      FileUtils.deleteDirectory(tempDir.toFile());
      usingTempHandler = false;
    }
  }
  
  private void downloadAndDecompressIndex(PrebuiltIndexHandler indexHandler) throws Exception {
    try {
      indexHandler.download();
      indexHandler.decompressIndex();
    } catch (IOException e) {
      e.printStackTrace();
      throw new Exception("Failed to download index.", e);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Failed to decompress index.", e);
    }
  }

  @After
  public void tearDown() throws Exception {
    // Delete the index downloaded
    if (handler.getIndexFolderPath() != null && handler.getIndexFolderPath().toFile().exists()) {
      handler.getIndexFolderPath().toFile().delete();
    }
    
    if (usingTempHandler && originalIndexPath != null && originalIndexPath.toFile().exists()) {
      originalIndexPath.toFile().delete();
    }
  }

}
