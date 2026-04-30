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

import java.nio.file.Path;

/**
 * Resolves cache directories used by Anserini. The base cache path is {@code ~/.cache/pyserini} by default. The
 * {@code anserini.cache} system property and {@code ANSERINI_CACHE} environment variable override the base cache path.
 * If neither override is set and a {@code .cache} directory exists in the current working directory, the base cache path
 * is {@code <cwd>/.cache/pyserini}. If a {@code index} directory exists in the current working directory, indexes are
 * stored in {@code <cwd>/index}; otherwise, indexes are stored in {@code <base>/indexes}. Topics, qrels, and encoders
 * are stored under {@code <base>/topics-and-qrels} and {@code <base>/encoders}.
 */
public final class CacheDirectoryResolver {
  public static final String CACHE_PROPERTY = "anserini.cache";
  public static final String CACHE_ENV = "ANSERINI_CACHE";

  private static final String LOCAL_CACHE_DIR = ".cache";
  private static final String DEFAULT_CACHE_PARENT = ".cache";
  private static final String DEFAULT_CACHE_NAME = "pyserini";
  private static final String LOCAL_INDEX_DIR = "index";
  private static final String INDEXES_DIR = "indexes";
  private static final String TOPICS_AND_QRELS_DIR = "topics-and-qrels";
  private static final String ENCODERS_DIR = "encoders";

  private CacheDirectoryResolver() {
  }

  public static Path getBasePath() {
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

  public static Path getIndexCachePath() {
    if (getLocalIndexPath().toFile().isDirectory()) {
      return getLocalIndexPath();
    }

    Path configuredBasePath = getConfiguredBasePath();
    if (configuredBasePath != null) {
      return configuredBasePath.resolve(INDEXES_DIR);
    }

    return getBasePath().resolve(INDEXES_DIR);
  }

  public static Path getTopicsAndQrelsCachePath() {
    return getBasePath().resolve(TOPICS_AND_QRELS_DIR);
  }

  public static Path getEncodersCachePath() {
    return getBasePath().resolve(ENCODERS_DIR);
  }

  private static Path getLocalIndexPath() {
    return Path.of(System.getProperty("user.dir"), LOCAL_INDEX_DIR);
  }

  private static Path getLocalBasePath() {
    Path localCachePath = Path.of(System.getProperty("user.dir"), LOCAL_CACHE_DIR);
    if (localCachePath.toFile().isDirectory()) {
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
}
