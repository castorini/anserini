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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sun.net.httpserver.HttpServer;

import org.junit.Test;

public class PrebuiltIndexTest {
  @Test
  public void testManifestParsing() throws Exception {
    assertEquals(List.of("a-flat.json", "z-inverted.json"),
        PrebuiltIndex.parseManifest("[\"z-inverted.json\",\"a-flat.json\"]"));
    for (String json : List.of("null", "{}", "[]", "[null]", "[1]", "[\"manifest.json\"]",
        "[\"../escape.json\"]", "[\"https://example.org/file.json\"]", "[\"README.md\"]", "invalid")) {
      assertThrows(IOException.class, () -> PrebuiltIndex.parseManifest(json));
    }
  }

  @Test
  public void testDuplicateManifestFilename() {
    IOException error = assertThrows(IOException.class,
        () -> PrebuiltIndex.parseManifest("[\"test-flat.json\",\"test-flat.json\"]"));
    assertEquals("Duplicate metadata filename: test-flat.json", error.getMessage());
  }

  @Test
  public void testLoadManifestEntries() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/pinned/lucene/test-flat.json", exchange -> {
      byte[] body = "[{\"name\":\"TEST\",\"type\":\"flat\"},{\"name\":\"SKIP\",\"type\":\"hnsw\"}]".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      try (var output = exchange.getResponseBody()) {
        output.write(body);
      }
    });
    server.createContext("/pinned/lucene/bad-flat.json", exchange -> {
      byte[] body = "invalid".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      try (var output = exchange.getResponseBody()) {
        output.write(body);
      }
    });
    server.createContext("/pinned/lucene/null-flat.json", exchange -> {
      byte[] body = "null".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      try (var output = exchange.getResponseBody()) {
        output.write(body);
      }
    });
    server.start();
    try {
      String base = "http://127.0.0.1:" + server.getAddress().getPort() + "/pinned/lucene/";
      List<PrebuiltIndex.Entry> entries = PrebuiltIndex.loadEntries(PrebuiltIndex.IndexType.FLAT,
          PrebuiltIndex.Entry.class, base + "manifest.json", List.of("test-flat.json", "unrequested-inverted.json"));
      assertEquals(1, entries.size());
      assertEquals("TEST", entries.get(0).name);
      for (String file : List.of("missing-flat.json", "bad-flat.json", "null-flat.json")) {
        IllegalStateException error = assertThrows(IllegalStateException.class,
            () -> PrebuiltIndex.loadEntries(PrebuiltIndex.IndexType.FLAT, PrebuiltIndex.Entry.class,
                base + "manifest.json", List.of(file)));
        assertTrue(error.getMessage().contains(base + file));
        assertTrue(error.getCause() instanceof IOException);
      }
    } finally {
      server.stop(0);
    }
  }

  @Test
  public void testInvalidName() {
    assertNull(PrebuiltIndex.get("fake_index"));
  }

  @Test
  public void testLookupByType() {
    PrebuiltIndex.Entry entry;

    entry = PrebuiltIndex.get("bright-biology");
    assertNotNull(entry);
    assertEquals("bright-biology", entry.name);
    assertEquals("inverted", entry.type);

    entry = PrebuiltIndex.get("bright-biology.splade-v3");
    assertNotNull(entry);
    assertEquals("bright-biology.splade-v3", entry.name);
    assertEquals("impact", entry.type);

    entry = PrebuiltIndex.get("bright-biology.bge-large-en-v1.5.flat");
    assertNotNull(entry);
    assertEquals("bright-biology.bge-large-en-v1.5.flat", entry.name);
    assertEquals("flat", entry.type);

    entry = PrebuiltIndex.get("msmarco-v1-passage.cosdpr-distil.hnsw");
    assertNotNull(entry);
    assertEquals("msmarco-v1-passage.cosdpr-distil.hnsw", entry.name);
    assertEquals("hnsw", entry.type);
  }
}
