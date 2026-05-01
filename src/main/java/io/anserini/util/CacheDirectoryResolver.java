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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Resolves cache directories used by Anserini. The base cache path is {@code ~/.cache/pyserini} by default. The
 * {@code pyserini.cache} system property and {@code PYSERINI_CACHE} environment variable override the base cache path.
 * If neither override is set and a {@code .cache} directory exists in the current working directory, the base cache path
 * is {@code <cwd>/.cache/pyserini}. Indexes are stored under {@code <base>/indexes}. Topics and qrels are stored
 * together under {@code <base>/topics-and-qrels}. Encoders are stored under {@code <base>/encoders}. All public
 * accessors create the resolved directory before returning it.
 */
public final class CacheDirectoryResolver {
  public static final String CACHE_PROPERTY = "pyserini.cache";
  public static final String CACHE_ENV = "PYSERINI_CACHE";

  private static final String LOCAL_CACHE_DIR = ".cache";
  private static final String DEFAULT_CACHE_PARENT = ".cache";
  private static final String DEFAULT_CACHE_NAME = "pyserini";
  private static final String INDEXES_DIR = "indexes";
  private static final String TOPICS_AND_QRELS_DIR = "topics-and-qrels";
  private static final String ENCODERS_DIR = "encoders";

  private CacheDirectoryResolver() {
  }

  public static Path getBasePath() {
    return createDirectories(resolveBasePath());
  }

  public static Path getIndexCachePath() {
    return createDirectories(resolveBasePath().resolve(INDEXES_DIR));
  }

  public static Path getTopicsAndQrelsCachePath() {
    return createDirectories(resolveBasePath().resolve(TOPICS_AND_QRELS_DIR));
  }

  public static Path getEncodersCachePath() {
    return createDirectories(resolveBasePath().resolve(ENCODERS_DIR));
  }

  private static Path resolveBasePath() {
    Path configuredBasePath = getConfiguredBasePath();
    if (configuredBasePath != null) {
      return configuredBasePath;
    }

    Path localBasePath = getLocalBasePath();
    if (localBasePath != null) {
      return localBasePath;
    }

    return getDefaultBasePath();
  }

  private static Path getLocalBasePath() {
    Path localCachePath = Path.of(System.getProperty("user.dir"), LOCAL_CACHE_DIR);
    if (Files.isDirectory(localCachePath)) {
      return localCachePath.resolve(DEFAULT_CACHE_NAME);
    }

    return null;
  }

  private static Path getDefaultBasePath() {
    return Path.of(System.getProperty("user.home"), DEFAULT_CACHE_PARENT, DEFAULT_CACHE_NAME);
  }

  private static Path getConfiguredBasePath() {
    String cacheDirectory = System.getProperty(CACHE_PROPERTY);

    if (cacheDirectory == null || cacheDirectory.isEmpty()) {
      cacheDirectory = System.getenv(CACHE_ENV);
    }

    if (cacheDirectory != null && !cacheDirectory.isEmpty()) {
      return Path.of(cacheDirectory);
    }

    return null;
  }

  private static Path createDirectories(Path path) {
    try {
      return Files.createDirectories(path);
    } catch (IOException e) {
      throw new UncheckedIOException("Unable to create cache directory: " + path, e);
    }
  }
}
