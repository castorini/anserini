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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Mini utility class for handling cache directories in Anserini.
 * Fallback to user's home directory for cache.
 */
public class CacheUtils {
  private static final String BASE_CACHE_DIR = Path.of(System.getProperty("user.home"), ".cache", "pyserini").toString();
  
  public static final String INDEXES_DIR = "indexes";
  public static final String INDEXES_CACHE_PROPERTY = "anserini.index.cache";
  public static final String INDEXES_CACHE_ENV = "ANSERINI_INDEX_CACHE";
  
  public static final String ENCODERS_DIR = "encoders";
  public static final String ENCODERS_CACHE_PROPERTY = "anserini.encoder.cache";
  public static final String ENCODERS_CACHE_ENV = "ANSERINI_ENCODER_CACHE";
  
  public static final String TOPICS_QRELS_DIR = "topics-and-qrels";
  public static final String TOPICS_QRELS_CACHE_PROPERTY = "anserini.topics.cache";
  public static final String TOPICS_QRELS_CACHE_ENV = "ANSERINI_TOPICS_CACHE";
  
  /**
   * Gets the cache directory for indexes.
   * @return Path to the indexes cache directory
   * @throws IOException if the cache directory cannot be created
   */
  public static String getIndexesCache() throws IOException {
    return getCacheDir(INDEXES_CACHE_PROPERTY, INDEXES_CACHE_ENV, Path.of(BASE_CACHE_DIR, INDEXES_DIR).toString());
  }
  
  /**
   * Gets the cache directory for encoders.
   * @return Path to the encoders cache directory
   * @throws IOException if the cache directory cannot be created
   */
  public static String getEncodersCache() throws IOException {
    return getCacheDir(ENCODERS_CACHE_PROPERTY, ENCODERS_CACHE_ENV, Path.of(BASE_CACHE_DIR, ENCODERS_DIR).toString());
  }
  
  /**
   * Gets the cache directory for topics and qrels.
   * @return Path to the topics and qrels cache directory
   * @throws IOException if the cache directory cannot be created
   */
  public static String getTopicsAndQrelsCache() throws IOException {
    return getCacheDir(TOPICS_QRELS_CACHE_PROPERTY, TOPICS_QRELS_CACHE_ENV,
                       Path.of(BASE_CACHE_DIR, TOPICS_QRELS_DIR).toString());
  }
  
  /**
   * Generic method to get a cache directory with fallback options.
   * @param propertyName System property name to check first
   * @param envVarName Environment variable name to check second
   * @param defaultPath Default path to use if neither property nor env var is set
   * @return The resolved cache directory path
   * @throws IOException if the cache directory cannot be created
   */
  private static String getCacheDir(String propertyName, String envVarName, String defaultPath) throws IOException {
    String cacheDir = System.getProperty(propertyName);
    
    if (cacheDir == null || cacheDir.isEmpty()) {
      cacheDir = System.getenv(envVarName);
    }
    
    if (cacheDir == null || cacheDir.isEmpty()) {
      cacheDir = defaultPath;
    }
    
    File cacheDirFile = new File(cacheDir);
    if (!cacheDirFile.exists()) {
      if (!cacheDirFile.mkdirs() && !cacheDirFile.exists()) {
        throw new IOException("Failed to create cache directory: " + cacheDir + "\n Check that you have write permissions to the directory.");
      }
    }
    
    return cacheDir;
  }
} 