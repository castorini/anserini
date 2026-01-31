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

package io.anserini.index.prebuilt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class PrebuiltHnswIndexTest {
  @Test
  public void testInvalidName() {
    PrebuiltHnswIndex.Entry entry = PrebuiltHnswIndex.get("fake_index");
    assertNull(entry);
  }

  @Test
  public void testTotalCount() {
    assertEquals(29, PrebuiltHnswIndex.entries().size());
  }

  @Test
  public void testTotalCountForBeir() {
    int beirCount = 0;
    for (PrebuiltHnswIndex.Entry entry : PrebuiltHnswIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.startsWith("beir")) {
        beirCount++;
      }
    }
    assertEquals(29, beirCount);
  }

  @Test
  public void testTotalCountForBright() {
    int brightCount = 0;
    for (PrebuiltHnswIndex.Entry entry : PrebuiltHnswIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.startsWith("bright")) {
        brightCount++;
      }
    }
    assertEquals(0, brightCount);
  }

  @Test
  public void testUrls() {
    for (PrebuiltFlatIndex.Entry entry : PrebuiltFlatIndex.entries()) {
      for (String url : entry.urls) {
        // check each url status code is 200
        try {
          final URL requestUrl = new URI(url).toURL();
          final HttpURLConnection con = (HttpURLConnection) requestUrl.openConnection();
          assertEquals(200, con.getResponseCode());
          con.disconnect();
        } catch (IOException e) {
          throw new RuntimeException("Error connecting to " + url, e);
        } catch (Exception e) {
          throw new RuntimeException("Malformed URL: " + url, e);
        }
      }
    }
  }

  @Test
  public void testLoadEntriesFromJarProtocol() throws Exception {
    Path tempDir = Files.createTempDirectory("anserini-prebuilt-hnsw-jar");

    Path jarPath = tempDir.resolve("prebuilt-hnsw.jar");
    try (JarOutputStream jarOut = new JarOutputStream(Files.newOutputStream(jarPath))) {
      JarEntry dirEntry = new JarEntry("prebuilt-indexes/");
      jarOut.putNextEntry(dirEntry);
      jarOut.closeEntry();

      JarEntry jsonEntry = new JarEntry("prebuilt-indexes/hnsw-test.json");
      jarOut.putNextEntry(jsonEntry);
      jarOut.write("[{\"name\":\"TEST\",\"type\":\"hnsw\"}]".getBytes(StandardCharsets.UTF_8));
      jarOut.closeEntry();
    }

    URL jarUrl = jarPath.toUri().toURL();
    try (URLClassLoader jarClassLoader = new URLClassLoader(new URL[] {jarUrl}, null)) {
      Class<?> jarClass = Proxy.newProxyInstance(jarClassLoader, new Class<?>[] {Runnable.class}, (proxy, method, args) -> null).getClass();
      TypeReference<List<PrebuiltHnswIndex.Entry>> entryListType = new TypeReference<List<PrebuiltHnswIndex.Entry>>() {};
      List<PrebuiltHnswIndex.Entry> entries = PrebuiltIndex.loadEntries(PrebuiltIndex.Type.HNSW, entryListType, jarClass);

      assertEquals(1, entries.size());
      assertEquals("TEST", entries.get(0).name);
    }
  }
}
