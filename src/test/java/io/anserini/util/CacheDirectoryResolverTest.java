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

package io.anserini.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CacheDirectoryResolverTest {
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private String userDir;
  private String userHome;
  private String cacheProperty;

  @Before
  public void setUp() throws IOException {
    userDir = System.getProperty("user.dir");
    userHome = System.getProperty("user.home");
    cacheProperty = System.getProperty(CacheDirectoryResolver.CACHE_PROPERTY);
    System.setProperty("user.home", tempFolder.newFolder("home").toString());
    System.clearProperty(CacheDirectoryResolver.CACHE_PROPERTY);
  }

  @After
  public void tearDown() {
    System.setProperty("user.dir", userDir);
    System.setProperty("user.home", userHome);
    if (cacheProperty == null) {
      System.clearProperty(CacheDirectoryResolver.CACHE_PROPERTY);
    } else {
      System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, cacheProperty);
    }
  }

  @Test
  public void testDefaultBasePath() throws IOException {
    Path cwd = tempFolder.newFolder("no-local-cache").toPath();
    System.setProperty("user.dir", cwd.toString());

    Path base = Path.of(System.getProperty("user.home"), ".cache", "pyserini");
    assertEquals(base, CacheDirectoryResolver.getBasePath());
    assertTrue(Files.isDirectory(base));
  }

  @Test
  public void testDefaultCacheSubpaths() throws IOException {
    Path cwd = tempFolder.newFolder("default-subpaths").toPath();
    System.setProperty("user.dir", cwd.toString());

    Path base = Path.of(System.getProperty("user.home"), ".cache", "pyserini");
    assertEquals(base.resolve("indexes"), CacheDirectoryResolver.getIndexCachePath());
    assertEquals(base.resolve("topics-and-qrels"), CacheDirectoryResolver.getTopicsAndQrelsCachePath());
    assertEquals(base.resolve("encoders"), CacheDirectoryResolver.getEncodersCachePath());
    assertEquals(base.resolve("collections"), CacheDirectoryResolver.getCollectionCachePath());
    assertTrue(Files.isDirectory(base.resolve("indexes")));
    assertTrue(Files.isDirectory(base.resolve("topics-and-qrels")));
    assertTrue(Files.isDirectory(base.resolve("encoders")));
    assertTrue(Files.isDirectory(base.resolve("collections")));
  }

  @Test
  public void testLocalCacheDirectoryBasePath() throws IOException {
    Path cwd = tempFolder.newFolder("local-cache").toPath();
    Files.createDirectory(cwd.resolve(".cache"));
    System.setProperty("user.dir", cwd.toString());

    Path base = cwd.resolve(".cache").resolve("pyserini");
    assertEquals(base, CacheDirectoryResolver.getBasePath());
    assertEquals(base.resolve("indexes"), CacheDirectoryResolver.getIndexCachePath());
    assertEquals(base.resolve("topics-and-qrels"), CacheDirectoryResolver.getTopicsAndQrelsCachePath());
    assertEquals(base.resolve("encoders"), CacheDirectoryResolver.getEncodersCachePath());
    assertEquals(base.resolve("collections"), CacheDirectoryResolver.getCollectionCachePath());
    assertTrue(Files.isDirectory(base));
    assertTrue(Files.isDirectory(base.resolve("indexes")));
    assertTrue(Files.isDirectory(base.resolve("topics-and-qrels")));
    assertTrue(Files.isDirectory(base.resolve("encoders")));
    assertTrue(Files.isDirectory(base.resolve("collections")));
  }

  @Test
  public void testCachePropertyOverride() throws IOException {
    Path cwd = tempFolder.newFolder("override").toPath();
    Files.createDirectory(cwd.resolve(".cache"));
    System.setProperty("user.dir", cwd.toString());

    Path override = tempFolder.newFolder("cache").toPath();
    System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, override.toString());

    assertEquals(override, CacheDirectoryResolver.getBasePath());
    assertEquals(override.resolve("indexes"), CacheDirectoryResolver.getIndexCachePath());
    assertEquals(override.resolve("topics-and-qrels"), CacheDirectoryResolver.getTopicsAndQrelsCachePath());
    assertEquals(override.resolve("encoders"), CacheDirectoryResolver.getEncodersCachePath());
    assertEquals(override.resolve("collections"), CacheDirectoryResolver.getCollectionCachePath());
    assertTrue(Files.isDirectory(override));
    assertTrue(Files.isDirectory(override.resolve("indexes")));
    assertTrue(Files.isDirectory(override.resolve("topics-and-qrels")));
    assertTrue(Files.isDirectory(override.resolve("encoders")));
    assertTrue(Files.isDirectory(override.resolve("collections")));
  }

  @Test(expected = UncheckedIOException.class)
  public void testDirectoryCreationFailure() throws IOException {
    Path cwd = tempFolder.newFolder("creation-failure").toPath();
    System.setProperty("user.dir", cwd.toString());

    Path file = tempFolder.newFile("cache-file").toPath();
    System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, file.toString());

    CacheDirectoryResolver.getBasePath();
  }
}
