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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.anserini.index.prebuilt.PrebuiltFlatIndex;
import io.anserini.index.prebuilt.PrebuiltHnswIndex;
import io.anserini.index.prebuilt.PrebuiltImpactIndex;
import io.anserini.index.prebuilt.PrebuiltIndex;
import io.anserini.index.prebuilt.PrebuiltInvertedIndex;

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

  private static final int MAX_DOWNLOAD_ATTEMPTS = 3;
  private static final int DOWNLOAD_BUFFER_SIZE = 1 << 16; // 64 KB
  private static final int CONNECT_TIMEOUT_MS = 60_000;
  private static final int READ_TIMEOUT_MS = 120_000;
  private static final int DOWNLOAD_LOG_INTERVAL_PERCENT = 10;

  private final PrebuiltIndex.Entry entry;

  private String cacheDirectory;
  private Path downloadFilePath;
  private Path indexPath;

  /**
   * Returns a <tt>PrebuiltIndexHandler</tt> for a prebuilt index given its name, or <tt>null</tt> if it doesn't exist.
   * The default cache directory is <tt>~/.cache/pyserini/indexes</tt>, or <tt>index</tt> in the current working
   * directory if it exists. If <tt>index</tt> does not exist, a custom base cache directory can be specified via the
   * environment variable <tt>$ANSERINI_CACHE</tt> or the system property <tt>anserini.cache</tt>. If no custom base
   * cache directory is specified and <tt>.cache</tt> exists in the current working directory, indexes are stored in
   * <tt>.cache/pyserini/indexes</tt> under the current working directory.
   *
   * @param name the name of the prebuilt index
   * @return a <tt>PrebuiltIndexHandler</tt> for a prebuilt index given its name, or <tt>null</tt> if it doesn't exist.
   */
  public static PrebuiltIndexHandler get(String name) throws IOException {
    try {
      return new PrebuiltIndexHandler(name);
    } catch (Exception e) {
      return null;
    }
  }

  private PrebuiltIndexHandler(String name) throws IOException {
    PrebuiltIndex.Entry entry;

    if ((entry = PrebuiltInvertedIndex.get(name)) != null) {
      this.entry = entry;
    } else if ((entry = PrebuiltImpactIndex.get(name)) != null) {
      this.entry = entry;
    } else if ((entry = PrebuiltFlatIndex.get(name)) != null) {
      this.entry = entry;
    } else if ((entry = PrebuiltHnswIndex.get(name)) != null) {
      this.entry = entry;
    } else if (PrebuiltHnswIndex.get(name) != null) {
      this.entry = entry;
    } else {
      throw new IOException("Index not found!");
    }
  }

  private static boolean verifyChecksum(Path path, String md5) throws IOException {
    try (InputStream is = Files.newInputStream(path)) {
      String generatedChecksum = DigestUtils.md5Hex(is);
      return generatedChecksum.equals(md5);
    }
  }

  public void fetch() throws IOException {
    fetch(CacheDirectoryResolver.getIndexCachePath().toString());
  }

  public void fetch(String cacheDirectory) throws IOException {
    this.cacheDirectory = cacheDirectory;

    if (!Path.of(cacheDirectory).toFile().exists()) {
      try {
        Files.createDirectories(Path.of(cacheDirectory));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    downloadFilePath = Path.of(cacheDirectory, this.entry.filename);
    String downloadFilePathString = downloadFilePath.toString();
    indexPath = Path.of((downloadFilePathString.endsWith(".gz") ?
        downloadFilePathString.substring(0, downloadFilePathString.length() - ".tar.gz".length()) :
        downloadFilePathString.substring(0, downloadFilePathString.length() - ".tar".length())) + "." + this.entry.md5);

    if (indexPath.toFile().exists()) {
      LOG.info(String.format("Index already exists at %s: skipping downloading.", indexPath));
      return;
    }

    try {
      download();
    } catch (URISyntaxException e) {
      throw new IOException(String.format("Invalid URL syntax for index download: %s", e.getMessage()), e);
    }

    if (this.entry.size != -1) {
      long downloadedSize = Files.size(downloadFilePath);
      if (downloadedSize != this.entry.size) {
        throw new IOException(String.format("Downloaded size mismatch: expected %s bytes but got %s bytes.",
            this.entry.size, downloadedSize));
      }
      LOG.info("Verified downloaded size for {}: {}.", this.entry.name, formatSize(downloadedSize));
    }

    try {
      decompress();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException(String.format("Unpacking interrupted: %s", e.getMessage()), e);
    }
  }

  private void download() throws IOException, URISyntaxException {
    IOException lastException = null;
    for (String urlString : this.entry.urls) {
      for (int attempt = 1; attempt <= MAX_DOWNLOAD_ATTEMPTS; attempt++) {
        LOG.info("Downloading index {} from {} to {} (attempt {}/{}).",
            this.entry.name, urlString, downloadFilePath, attempt, MAX_DOWNLOAD_ATTEMPTS);
        try {
          downloadFromUrl(urlString);
          LOG.info("Verifying checksum for {} at {}.", this.entry.name, downloadFilePath);
          if (!verifyChecksum(downloadFilePath, this.entry.md5)) {
            throw new IOException(String.format("Downloaded file checksum mismatch: expected md5 %s.", this.entry.md5));
          }
          LOG.info("Verified checksum for {}: md5 {}.", this.entry.name, this.entry.md5);
          return;
        } catch (IOException e) {
          lastException = e;
          LOG.error("Download failed: " + e.getMessage());
          try {
            Files.deleteIfExists(downloadFilePath);
          } catch (IOException deleteException) {
            LOG.error("Unable to remove incomplete download: " + deleteException.getMessage());
          }
        }
      }
    }

    throw new IOException(String.format("Unable to download index %s after trying %s URL(s) with %s attempt(s) each.",
        this.entry.name, this.entry.urls.length, MAX_DOWNLOAD_ATTEMPTS), lastException);
  }

  private void downloadFromUrl(String urlString) throws IOException, URISyntaxException {
    URL url = new URI(urlString).toURL();
    HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
    httpConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
    httpConnection.setReadTimeout(READ_TIMEOUT_MS);
    long size = httpConnection.getContentLengthLong();
    boolean hasKnownSize = size > 0;

    try (InputStream inputStream = new BufferedInputStream(httpConnection.getInputStream());
        FileOutputStream fileOS = new FileOutputStream(downloadFilePath.toFile())) {

      if (hasKnownSize) {
        LOG.info("Starting download of {} (size: {}).", this.entry.name, formatSize(size));
      } else {
        LOG.info("Starting download of {} (size unknown).", this.entry.name);
      }

      byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
      long downloaded = 0L;
      int nextLoggedPercent = DOWNLOAD_LOG_INTERVAL_PERCENT;
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        fileOS.write(buffer, 0, bytesRead);
        downloaded += bytesRead;
        if (hasKnownSize) {
          nextLoggedPercent = logDownloadProgress(downloaded, size, nextLoggedPercent);
        }
      }

      if (hasKnownSize) {
        LOG.info("Finished downloading {} ({}).", this.entry.name, formatSize(downloaded));
      } else {
        LOG.info("Finished downloading {}.", this.entry.name);
      }

    } finally {
      httpConnection.disconnect();
    }
  }

  private int logDownloadProgress(long downloaded, long size, int nextLoggedPercent) {
    if (size <= 0) {
      return nextLoggedPercent;
    }

    long percent = Math.min(100, downloaded * 100 / size);
    while (percent >= nextLoggedPercent && nextLoggedPercent <= 100) {
      LOG.info("Downloading {}: {}% ({}/{})", this.entry.name, nextLoggedPercent, formatSize(downloaded), formatSize(size));
      nextLoggedPercent += DOWNLOAD_LOG_INTERVAL_PERCENT;
    }

    return nextLoggedPercent;
  }

  private static String formatSize(long bytes) {
    if (bytes < 1024) {
      return bytes + " B";
    }
    if (bytes < 1024 * 1024) {
      return String.format("%.1f KB", bytes / 1024.0);
    }
    if (bytes < 1024L * 1024L * 1024L) {
      return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
  }

  private void decompress() throws IOException, InterruptedException {
    LOG.info("Preparing to unpack {} from {} into cache directory {}.", this.entry.name, downloadFilePath, cacheDirectory);

    if (!downloadFilePath.toFile().exists()) {
      throw new IOException(String.format("Unexpected error: %s not found.", downloadFilePath));
    }

    String downloadFilePathString = downloadFilePath.toString();
    boolean gzipped = downloadFilePathString.endsWith(".gz");
    Path tarFilePath = gzipped ?
        Path.of(downloadFilePathString.substring(0, downloadFilePathString.length() - ".gz".length())) :
        downloadFilePath;

    if (gzipped) {
      LOG.info("Expanding gzip archive {}.", downloadFilePath);
      ProcessBuilder pbGZIP = new ProcessBuilder("gzip", "-d", downloadFilePathString);
      Process pGZIP = pbGZIP.start();
      int exitCode = pGZIP.waitFor();
      if (exitCode != 0) {
        throw new IOException(String.format("gzip failed while expanding %s with exit code %s.", downloadFilePath, exitCode));
      }
      LOG.info("Finished expanding gzip archive to {}.", tarFilePath);
    }

    LOG.info("Extracting tar archive {} into {}.", tarFilePath, cacheDirectory);
    ProcessBuilder pbTAR = new ProcessBuilder("tar", "-xf", tarFilePath.toString(), "-C", cacheDirectory);
    Process pTar = pbTAR.start();
    int exitCode = pTar.waitFor();
    if (exitCode != 0) {
      throw new IOException(String.format("tar failed while extracting %s with exit code %s.", tarFilePath, exitCode));
    }
    LOG.info("Finished extracting tar archive {}.", tarFilePath);

    LOG.info("Removing tar archive {}.", tarFilePath);
    Files.delete(tarFilePath);
    LOG.info("Removed tar archive {}.", tarFilePath);

    String tarFilePathString = tarFilePath.toString();
    Path unpackedIndexPath = Path.of(tarFilePathString.substring(0, tarFilePathString.length() - ".tar".length()));
    LOG.info("Moving unpacked index from {} to {}.", unpackedIndexPath, this.indexPath);
    Files.move(unpackedIndexPath, this.indexPath);
    LOG.info("Finished unpacking {}. Final index location: {}.", this.entry.name, indexPath);
  }

  public Path getIndexPath() {
    return this.indexPath;
  }

  public long getSize() {
    return this.entry.size;
  }

  public String getFilename() {
    return this.entry.filename;
  }

  public String getMD5() {
    return this.entry.md5;
  }
}
