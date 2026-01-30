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

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class PrebuiltInvertedIndexTest {
  @Test
  public void testInvalidName() {
    PrebuiltInvertedIndex.Entry entry = PrebuiltInvertedIndex.get("fake_index");
    assertNull(entry);
  }

  @Test
  public void testLookupByName() {
    PrebuiltInvertedIndex.Entry entry;

    entry = PrebuiltInvertedIndex.get("msmarco-v1-passage");
    assertNotNull(entry);
    assertEquals("lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz", entry.filename);
    assertEquals("678876e8c99a89933d553609a0fd8793", entry.md5);
    assertEquals(8841823, entry.documents);
    assertEquals(2170758745L, entry.compressedSize);

    entry = PrebuiltInvertedIndex.get("msmarco-v1-doc");
    assertNotNull(entry);
    assertEquals("lucene-inverted.msmarco-v1-doc.20221004.252b5e.tar.gz", entry.filename);
    assertEquals("f66020a923df6430007bd5718e53de86", entry.md5);
    assertEquals(3213835, entry.documents);
    assertEquals(13736982339L, entry.compressedSize);
  }

  @Test
  public void testTotalCount() {
    assertEquals(14, PrebuiltInvertedIndex.entries().size());
  }

  @Test
  public void testTotalCountForBright() {
    int brightCount = 0;
    for (PrebuiltInvertedIndex.Entry entry : PrebuiltInvertedIndex.entries()) {
      if (entry != null && entry.name != null && entry.name.toUpperCase().startsWith("BRIGHT")) {
        brightCount++;
      }
    }
    assertEquals(12, brightCount);
  }

    @Test
  public void testLoadEntriesFromJarProtocol() throws Exception {
    Path tempDir = Files.createTempDirectory("anserini-prebuilt-inverted-jar");

    Path jarPath = tempDir.resolve("prebuilt-inverted.jar");
    try (JarOutputStream jarOut = new JarOutputStream(Files.newOutputStream(jarPath))) {
      JarEntry dirEntry = new JarEntry("prebuilt-indexes/");
      jarOut.putNextEntry(dirEntry);
      jarOut.closeEntry();

      JarEntry jsonEntry = new JarEntry("prebuilt-indexes/impact-inverted.json");
      jarOut.putNextEntry(jsonEntry);
      jarOut.write("[{\"name\":\"TEST\",\"type\":\"inverted\"}]".getBytes(StandardCharsets.UTF_8));
      jarOut.closeEntry();
    }

    URL jarUrl = jarPath.toUri().toURL();
    try (URLClassLoader jarClassLoader = new URLClassLoader(new URL[] {jarUrl}, null)) {
      Class<?> jarClass = Proxy.newProxyInstance(
          jarClassLoader,
          new Class<?>[] {Runnable.class},
          (proxy, method, args) -> null).getClass();
      TypeReference<List<PrebuiltInvertedIndex.Entry>> entryListType =
          new TypeReference<List<PrebuiltInvertedIndex.Entry>>() {};
      List<PrebuiltInvertedIndex.Entry> entries =
          PrebuiltIndex.loadEntries(PrebuiltIndex.Type.INVERTED, entryListType, jarClass);
      assertEquals(1, entries.size());
      assertEquals("TEST", entries.get(0).name);
    }
  }
}
