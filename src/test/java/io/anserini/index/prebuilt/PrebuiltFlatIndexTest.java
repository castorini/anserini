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

public class PrebuiltFlatIndexTest {
  @Test
  public void testInvalidName() {
    PrebuiltFlatIndex.Entry entry = PrebuiltFlatIndex.get("fake_index");
    assertNull(entry);
  }

  @Test
  public void testTotalCount() {
    assertEquals(41, PrebuiltFlatIndex.entries().size());
  }

  @Test
  public void testTotalCountForMsMarcoV1() {
    int v1Count = 0;
    for (PrebuiltFlatIndex.Entry entry : PrebuiltFlatIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.contains("v1")) {
        v1Count++;
      }
    }
    assertEquals(41, v1Count);
  }

  @Test
  public void testTotalCountForMsMarcoV2() {
    int v2Count = 0;
    for (PrebuiltFlatIndex.Entry entry : PrebuiltFlatIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.contains("v2") && !entry.name.contains("v2.1")) {
        v2Count++;
      }
    }
    assertEquals(0, v2Count);
  }

  @Test
  public void testTotalCountForMsMarcoV2_1() {
    int v2_1Count = 0;
    for (PrebuiltFlatIndex.Entry entry : PrebuiltFlatIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.contains("v2.1")) {
        v2_1Count++;
      }
    }
    assertEquals(0, v2_1Count);
  }

  @Test
  public void testTotalCountForBeir() {
    int beirCount = 0;
    for (PrebuiltFlatIndex.Entry entry : PrebuiltFlatIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.startsWith("beir")) {
        beirCount++;
      }
    }
    assertEquals(29, beirCount);
  }

  @Test
  public void testTotalCountForBright() {
    int brightCount = 0;
    for (PrebuiltFlatIndex.Entry entry : PrebuiltFlatIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.startsWith("bright")) {
        brightCount++;
      }
    }
    assertEquals(12, brightCount);
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
    Path tempDir = Files.createTempDirectory("anserini-prebuilt-flat-jar");

    Path jarPath = tempDir.resolve("prebuilt-flat.jar");
    try (JarOutputStream jarOut = new JarOutputStream(Files.newOutputStream(jarPath))) {
      JarEntry dirEntry = new JarEntry("prebuilt-indexes/");
      jarOut.putNextEntry(dirEntry);
      jarOut.closeEntry();

      JarEntry jsonEntry = new JarEntry("prebuilt-indexes/flat-test.json");
      jarOut.putNextEntry(jsonEntry);
      jarOut.write("[{\"name\":\"TEST\",\"type\":\"flat\"}]".getBytes(StandardCharsets.UTF_8));
      jarOut.closeEntry();
    }

    URL jarUrl = jarPath.toUri().toURL();
    try (URLClassLoader jarClassLoader = new URLClassLoader(new URL[] {jarUrl}, null)) {
      Class<?> jarClass = Proxy.newProxyInstance(jarClassLoader, new Class<?>[] {Runnable.class}, (proxy, method, args) -> null).getClass();
      TypeReference<List<PrebuiltFlatIndex.Entry>> entryListType = new TypeReference<List<PrebuiltFlatIndex.Entry>>() {};
      List<PrebuiltFlatIndex.Entry> entries = PrebuiltIndex.loadEntries(PrebuiltIndex.Type.FLAT, entryListType, jarClass);

      assertEquals(1, entries.size());
      assertEquals("TEST", entries.get(0).name);
    }
  }
}
