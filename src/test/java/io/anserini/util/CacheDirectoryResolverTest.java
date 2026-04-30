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

import java.io.IOException;
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
  private String cacheProperty;

  @Before
  public void setUp() {
    userDir = System.getProperty("user.dir");
    cacheProperty = System.getProperty(CacheDirectoryResolver.CACHE_PROPERTY);
    System.clearProperty(CacheDirectoryResolver.CACHE_PROPERTY);
  }

  @After
  public void tearDown() {
    System.setProperty("user.dir", userDir);
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

    assertEquals(Path.of(System.getProperty("user.home"), ".cache", "pyserini"), CacheDirectoryResolver.getBasePath());
  }

  @Test
  public void testDefaultCacheSubpaths() throws IOException {
    Path cwd = tempFolder.newFolder("default-subpaths").toPath();
    System.setProperty("user.dir", cwd.toString());

    Path base = Path.of(System.getProperty("user.home"), ".cache", "pyserini");
    assertEquals(base.resolve("indexes"), CacheDirectoryResolver.getIndexCachePath());
    assertEquals(base.resolve("topics-and-qrels"), CacheDirectoryResolver.getTopicsAndQrelsCachePath());
    assertEquals(base.resolve("encoders"), CacheDirectoryResolver.getEncodersCachePath());
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
  }

  @Test
  public void testLocalIndexDirectory() throws IOException {
    Path cwd = tempFolder.newFolder("local-index").toPath();
    Files.createDirectory(cwd.resolve("index"));
    System.setProperty("user.dir", cwd.toString());

    assertEquals(cwd.resolve("index"), CacheDirectoryResolver.getIndexCachePath());
  }

  @Test
  public void testLocalIndexDirectoryOverridesCacheProperty() throws IOException {
    Path cwd = tempFolder.newFolder("local-index-with-override").toPath();
    Files.createDirectory(cwd.resolve("index"));
    System.setProperty("user.dir", cwd.toString());

    Path override = tempFolder.newFolder("cache").toPath();
    System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, override.toString());

    assertEquals(cwd.resolve("index"), CacheDirectoryResolver.getIndexCachePath());
    assertEquals(override, CacheDirectoryResolver.getBasePath());
    assertEquals(override.resolve("topics-and-qrels"), CacheDirectoryResolver.getTopicsAndQrelsCachePath());
    assertEquals(override.resolve("encoders"), CacheDirectoryResolver.getEncodersCachePath());
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
  }
}
