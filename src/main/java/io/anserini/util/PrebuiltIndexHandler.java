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

import me.tongfei.progressbar.ProgressBar;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.anserini.index.IndexInfo;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class PrebuiltIndexHandler {
  private static final Logger LOG = LogManager.getLogger(PrebuiltIndexHandler.class);

  private static final String DEFAULT_CACHE_DIR = Path.of(System.getProperty("user.home"), ".cache", "pyserini", "indexes").toString();
  private static final String CACHE_DIR_PROPERTY = "anserini.index.cache";
  private static final String CACHE_DIR_ENV = "ANSERINI_INDEX_CACHE";
  private static final int MAX_DOWNLOAD_ATTEMPTS = 3;
  private static final int DOWNLOAD_BUFFER_SIZE = 1 << 16; // 64 KB
  private static final int CONNECT_TIMEOUT_MS = 60_000;
  private static final int READ_TIMEOUT_MS = 120_000;

  private String indexName;
  private IndexInfo indexInfo;

  private String cacheDirectory;
  private Path downloadFilePath;
  private Path indexFolderPath;

  /**
   * Creates a <tt>PrebuiltIndexHandler</tt>.
   * By default, the default cache directory is at <tt>~/.cache/pyserini/indexes</tt>.
   * Alternatively, a custom, user-specified cache directory can be specified via the environment variable <tt>$ANSERINI_INDEX_CACHE</tt>
   * or the system property <tt>anserini.index.cache</tt>.
   *
   * @param indexName the name of the index
   */
  public PrebuiltIndexHandler(String indexName) {
    this(indexName, getCache());
  }
  
  /**
   * Creates a <tt>PrebuiltIndexHandler</tt> with a custom, user-specified cache directory.
   *
   * @param indexName the name of the index
   * @param cacheDirectory the custom cache directory to use
   */
  public PrebuiltIndexHandler(String indexName, String cacheDirectory) {
    this.indexName = indexName;
    this.cacheDirectory = cacheDirectory;

    try {
      indexInfo = IndexInfo.get(indexName);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Index not found!" + e.getMessage());
    }

    if (!Path.of(cacheDirectory).toFile().exists()) {
      try {
        Files.createDirectories(Path.of(cacheDirectory));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    downloadFilePath = Path.of(cacheDirectory, indexInfo.filename);
    indexFolderPath = Path.of(downloadFilePath.toString().replace(".tar.gz", "") + "." + indexInfo.md5); 
  }

  private static String getCache() {
    String cacheDirectory = System.getProperty(CACHE_DIR_PROPERTY);
    
    if (cacheDirectory == null || cacheDirectory.isEmpty()) {
      cacheDirectory = System.getenv(CACHE_DIR_ENV);
    }
    
    if (cacheDirectory == null || cacheDirectory.isEmpty()) {
      cacheDirectory = DEFAULT_CACHE_DIR;
    }
    
    return cacheDirectory;
  }

  private static boolean verifyChecksum(Path path, String md5) throws IOException {
    try (InputStream is = Files.newInputStream(path)) {
      String generatedChecksum = DigestUtils.md5Hex(is);
      return generatedChecksum.equals(md5);
    }
  }

  public void fetch() throws IOException, URISyntaxException, InterruptedException {
    if (indexFolderPath.toFile().exists()) {
      LOG.info(String.format("Index already exists at %s: skipping downloading.", indexFolderPath));
      return;
    }

    download();
    decompress();
  }

  private void download() throws IOException, URISyntaxException {
    for (String urlString : indexInfo.urls) {
      for (int attempt = 1; attempt <= MAX_DOWNLOAD_ATTEMPTS; attempt++) {
        LOG.info("Downloading index from: " + urlString + " (attempt " + attempt + "/" + MAX_DOWNLOAD_ATTEMPTS + ")");
        try {
          downloadFromUrl(urlString);
          verifyChecksum(downloadFilePath, indexInfo.md5);
          return;
        } catch (IOException e) {
          LOG.error("Download failed: " + e.getMessage());
          try {
            Files.deleteIfExists(downloadFilePath);
          } catch (IOException deleteException) {
            LOG.error("Unable to remove incomplete download: " + deleteException.getMessage());
          }
        }
      }
    }
  }

  private void downloadFromUrl(String urlString) throws IOException, URISyntaxException {
    URL url = new URI(urlString).toURL();
    HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
    httpConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
    httpConnection.setReadTimeout(READ_TIMEOUT_MS);
    long completeFileSize = httpConnection.getContentLengthLong();
    boolean hasKnownSize = completeFileSize > 0;
    long progressBarMax = hasKnownSize ? Math.max(1, Math.floorDiv(completeFileSize, 1000)) : 1;

    try (InputStream inputStream = new BufferedInputStream(httpConnection.getInputStream());
        FileOutputStream fileOS = new FileOutputStream(downloadFilePath.toFile());
        ProgressBar pb = new ProgressBar(indexName, progressBarMax)) {

      pb.setExtraMessage("Downloading...");

      byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
      long downloaded = 0L;
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        fileOS.write(buffer, 0, bytesRead);
        downloaded += bytesRead;
        if (hasKnownSize) {
          pb.stepTo(Math.min(progressBarMax, Math.floorDiv(downloaded, 1000)));
        }
      }

      if (hasKnownSize) {
        pb.stepTo(progressBarMax);
      }

    } finally {
      httpConnection.disconnect();
    }
  }

  private void decompress() throws IOException, InterruptedException {
    LOG.info(String.format("Decompressing index at %s...", downloadFilePath));

    if (!downloadFilePath.toFile().exists())
      throw new IOException(String.format("Unexpected error: %s not found.", downloadFilePath));

    ProcessBuilder pbGZIP = new ProcessBuilder("gzip", "-d", downloadFilePath.toString());
    Process pGZIP = pbGZIP.start();
    pGZIP.waitFor();

    ProcessBuilder pbTAR = new ProcessBuilder("tar", "-xvf",
            downloadFilePath.toString().substring(0, downloadFilePath.toString().length() - 3), "-C", cacheDirectory);
    Process pTar = pbTAR.start();
    pTar.waitFor();

    // Delete the tar file
    Files.delete(Path.of(downloadFilePath.toString().replace(".gz", "")));
    LOG.info("Index decompressed successfully!");

    Files.move(Path.of(downloadFilePath.toString().replace(".tar.gz", "")), this.indexFolderPath);
    LOG.info(String.format("Final index location at %s", indexFolderPath));
  }

  public Path getIndexFolderPath() {
    return this.indexFolderPath;
  }
}
