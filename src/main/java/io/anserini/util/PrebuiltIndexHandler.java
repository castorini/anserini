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
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;

import io.anserini.index.IndexInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PrebuiltIndexHandler {
  private String indexName;
  private String saveRootPath;
  private IndexInfo info = null;
  private Path indexFolderPath = null;
  private boolean initialized = false;
  private Path savePath;

  public PrebuiltIndexHandler(String indexName) {
    this.indexName = indexName;
    this.saveRootPath = getCache();
  }

  private String getCache() {
    /*
     * Get the pyserini cache path firs to avoid double downloads. If the pyserini
     * cache path does not exist, use the anserini cache path.
     */
    final Path PyseriniPath = Paths.get(System.getProperty("user.home"), ".cache", "pyserini", "indexes");
    final Path AnseriniPath = Paths.get(System.getProperty("user.home"), ".cache", "anserini", "indexes");
    if (checkFileExist(PyseriniPath)) {
      return PyseriniPath.toString();
    } else {
      return AnseriniPath.toString();
    }
  }

  private static boolean checkFileExist(Path path) {
    return path.toFile().exists();
  }

  private boolean checkIndexFileExist() {
    /*
     * Check if the index file exists. If the index file exists, return true.
     * Otherwise, return false.
     */
    if (checkFileExist(savePath) || checkFileExist(Paths.get(savePath.toString().replace(".gz", "")))
        || checkFileExist(Paths.get(savePath.toString().replace(".tar.gz", "")))
        || checkFileExist(Paths.get(savePath.toString() + "." + info.md5))
        || checkFileExist(Paths.get(savePath.toString().replace(".gz", "") + "." + info.md5))
        || checkFileExist(Paths.get(savePath.toString().replace(".tar.gz", "") + "." + info.md5))) {
      return true;
    }
    return false;
  }

  private static IndexInfo getIndexInfo(String indexName) {
    /*
     * Get the index info from the index name.
     */
    try {
      IndexInfo info = IndexInfo.get(indexName);
      return info;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Index not found!" + e.getMessage());
    }
  }

  private static boolean checkMD5(InputStream st, String md5) throws IOException {
    /*
     * Check the MD5 checksum of the index file.
     */
    String generatedChecksum = DigestUtils.md5Hex(st);
    return generatedChecksum.equals(md5);
  }

  public void initialize() {
    if (initialized) {
      return;
    }
    info = getIndexInfo(indexName);
    // check if saveRootPath exists
    if (!checkFileExist(Paths.get(saveRootPath))) {
      try {
        Files.createDirectories(Paths.get(saveRootPath));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    savePath = Paths.get(saveRootPath, info.filename);
    initialized = true;
  }

  public void download() throws IOException {
    /*
     * Download the index file to the save path. If the file already exists, do
     * nothing. If the file does not exist, download the file and check the MD5
     * checksum.
     */
    if (!initialized) {
      throw new IllegalStateException("Handler not initialized!");
    }
    if (checkIndexFileExist()) {
      System.out.println("Index file already exists! Skip downloading.");
      return;
    }

    URL url = new URL(info.urls[0]);
    HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
    long completeFileSize = httpConnection.getContentLengthLong();

    try (InputStream inputStream = url.openStream();
        CountingInputStream cis = new CountingInputStream(inputStream);
        FileOutputStream fileOS = new FileOutputStream(savePath.toFile());
        ProgressBar pb = new ProgressBar(indexName, Math.floorDiv(completeFileSize, 1000))) {

      pb.setExtraMessage("Downloading...");

      new Thread(() -> {
        try {
          IOUtils.copyLarge(cis, fileOS);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();

      while (cis.getByteCount() < completeFileSize) {
        pb.stepTo(Math.floorDiv(cis.getByteCount(), 1000));
      }
      
      pb.stepTo(Math.floorDiv(cis.getByteCount(), 1000));
      pb.close();

      InputStream is = Files.newInputStream(savePath);
      if (!checkMD5(is, info.md5)) {
        throw new IOException("MD5 check failed!");
      }
    }
  }

  public String decompressIndex() throws Exception {
    /*
     * Decompress the tar.gz or tar index file to an archive folder. If the folder
     * already exists, do nothing.
     */
    if (!initialized) {
      throw new IllegalStateException("Handler not initialized!");
    }
    if (!checkIndexFileExist()) {
      throw new Exception("Index file does not exist!");
    }

    String indexFolder = savePath.toString().replace(".tar.gz", "");
    if (checkFileExist(Paths.get(indexFolder + "." + info.md5))) {
      System.out.println("Index folder already exists!");
      return indexFolder + "." + info.md5;
    }
    System.out.println("Decompressing index...");

    if (checkFileExist(Paths.get(savePath.toString()))) {
      ProcessBuilder pbGZIP = new ProcessBuilder("gzip", "-d", savePath.toString());
      Process pGZIP = pbGZIP.start();
      pGZIP.waitFor();
    }

    if (checkFileExist(Paths.get(savePath.toString().replace(".gz", "")))) {
      ProcessBuilder pbTAR = new ProcessBuilder("tar", "-xvf",
          savePath.toString().substring(0, savePath.toString().length() - 3), "-C", saveRootPath);
      Process pTar = pbTAR.start();
      pTar.waitFor();

      // detele the tar file for saving space
      Files.delete(Path.of(savePath.toString().replace(".gz", "")));
    }

    System.out.println("Index decompressed successfully!");

    // postpend md5 to decompressed index
    Path oldIndexPath = Paths.get(indexFolder);
    indexFolder += "." + info.md5;
    this.indexFolderPath = Paths.get(indexFolder);
    if (!checkFileExist(this.indexFolderPath)) {
      Files.move(oldIndexPath, this.indexFolderPath);
    } else if (checkFileExist(oldIndexPath)) {
      Files.delete(oldIndexPath);
    }
    return indexFolder;
  }

  public Path getIndexFolderPath() {
    return this.indexFolderPath;
  }
}
