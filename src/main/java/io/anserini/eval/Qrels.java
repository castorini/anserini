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

package io.anserini.eval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.util.CacheDirectoryResolver;

public class Qrels {
  public static final String METADATA_URL_PROPERTY = "anserini.qrels.metadata.url";
  public static final String DEFAULT_METADATA_URL =
      "https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/_metadata_qrels.json";
  private static final String SERVER_PATH = "https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels/";

  private static final ObjectMapper mapper = new ObjectMapper();
  private static volatile Map<String, String> symbolFileDict;

  public final String symbol;
  public final String path;
  private final Map<String, Map<String, Integer>> qrels;

  public Qrels(String file) throws IOException {
    this(null, file);
  }

  protected Qrels(String symbol, String path) throws IOException {
    this.symbol = symbol;
    this.path = path;
    this.qrels = loadQrels(path);
  }

  public static Qrels get(String symbol) throws IOException {
    String path = registry().get(symbol);
    if (path == null) {
      throw new IllegalArgumentException("Unknown qrels symbol: " + symbol);
    }
    return new Qrels(symbol, path);
  }

  public static Qrels fromQrels(String symbol) throws IOException {
    return get(symbol);
  }

  public static Qrels fromQrels(Qrels qrels) throws IOException {
    return qrels;
  }

  public static Map<String, String> registry() {
    Map<String, String> registry = symbolFileDict;
    if (registry == null) {
      synchronized (Qrels.class) {
        registry = symbolFileDict;
        if (registry == null) {
          registry = loadRegistry();
          symbolFileDict = registry;
        }
      }
    }
    return registry;
  }

  public static Set<String> symbols() {
    return registry().keySet();
  }

  public static void refresh() {
    synchronized (Qrels.class) {
      symbolFileDict = loadRegistry();
    }
  }

  private static Map<String, String> loadRegistry() {
    String metadataUrl = System.getProperty(METADATA_URL_PROPERTY, DEFAULT_METADATA_URL);
    try (InputStream inputStream = new URI(metadataUrl).toURL().openStream()) {
      Map<String, String> registry = mapper.readValue(inputStream, new TypeReference<>() {});
      return Collections.unmodifiableMap(new TreeMap<>(registry));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load qrels metadata from " + metadataUrl, e);
    }
  }

  public String symbol() {
    return symbol;
  }

  public String path() {
    return path;
  }

  /**
   * Method will return whether this docId for this qid is judged or not
   * Note that if qid is invalid this will always return false
   * 
   * @param qid   qid
   * @param docid docid
   * @return true if docId is judged against qid false otherwise
   */
  public boolean isDocJudged(String qid, String docid) {
    if (!qrels.containsKey(qid)) {
      return false;
    }

    if (!qrels.get(qid).containsKey(docid)) {
      return false;
    } else {
      return true;
    }
  }

  public <K> int getRelevanceGrade(K qid, String docid) {
    if (!qrels.containsKey(qid)) {
      return 0;
    }

    if (!qrels.get(qid).containsKey(docid)) {
      return 0;
    }

    if (qrels.get(qid).get(docid) <= 0)
      return 0;
    return qrels.get(qid).get(docid);
  }

  public Set<String> getQids() {
    return this.qrels.keySet();
  }

  public Map<String, Integer> getDocMap(String qid) {
    if (this.qrels.containsKey(qid)) {
      return this.qrels.get(qid);
    } else {
      return null;
    }
  }

  private static Map<String, Map<String, Integer>> loadQrels(String file) throws IOException {
    Map<String, Map<String, Integer>> qrels = new HashMap<>();
    Path qrelsPath = Path.of(file);
    try {
      qrelsPath = getQrelsPath(qrelsPath);
    } catch (IOException e) {
      System.out.println("Qrels file not found at " + qrelsPath);
    }

    try (BufferedReader br = new BufferedReader(new FileReader(qrelsPath.toString()))) {
      String line;
      String[] arr;
      while ((line = br.readLine()) != null) {
        arr = line.split("[\\s\\t]+");
        String qid = arr[0];
        String docno = arr[2];
        int grade = Integer.parseInt(arr[3]);
        if (qrels.containsKey(qid)) {
          qrels.get(qid).put(docno, grade);
        } else {
          Map<String, Integer> t = new HashMap<>();
          t.put(docno, grade);
          qrels.put(qid, t);
        }
      }
    } catch (IOException e) {
      throw new IOException("Could not read qrels file!");
    }
    return qrels;
  }

  /**
   * Method will return the qrels file as a string
   * 
   * @param qrelsPath path to qrels file
   * @return qrels file as a string
   * @throws IOException if qrels file is not found
   */
  public static String getQrelsResource(Path qrelsPath) throws IOException {
    Path resultPath = qrelsPath;
    try {
      resultPath = getQrelsPath(qrelsPath);
    } catch (Exception e) {
      throw new IOException("Could not get qrels file either from server or local file system!");
    }

    try (InputStream inputStream = Files.newInputStream(resultPath)) {
      String raw = new String(inputStream.readAllBytes());
      return raw;
    }
  }

  /**
   * Method will look for the absolute qrels path and return it as a Path object
   * 
   * @param qrelsPath path to qrels file
   * @return qrels path
   * @throws IOException
   */
  public static Path getQrelsPath(Path qrelsPath) throws IOException {
    boolean isContained = Qrels.contains(qrelsPath);
    boolean isContainedSymbol = false;
    if (!isContained) {
      isContainedSymbol = Qrels.containsSymbol(qrelsPath);
    }
    if (!isContained && !isContainedSymbol) {
      // If the qrels file is not in the list of known qrels, we assume it is a local file.
      Path tempPath = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve(qrelsPath.getFileName());
      if (Files.exists(tempPath)) {
        // if it is an unregistered qrels in the Qrels registry, but it is in the cache, we use it.
        return tempPath;
      }
      return qrelsPath;
    }

    // If qrelsPath is a prefix, we should extend it to a full file name
    if (isContainedSymbol) {
      qrelsPath = Qrels.extendSymbol(qrelsPath);
    }

    Path resultPath = getNewQrelAbsPath(qrelsPath);
    if (!Files.exists(resultPath)) {
      resultPath = downloadQrels(qrelsPath);
    }
    return resultPath;
  }

  public static Path getNewQrelAbsPath(Path qrelsPath) {
    return CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve(qrelsPath.getFileName());
  }

  /**
   * Method will download the qrels file from the cloud and return the path to the
   * file
   * 
   * @param qrelsPath path to qrels file
   * @return path to qrels file
   * @throws IOException if qrels file is not found
   */
  public static Path downloadQrels(Path qrelsPath) throws IOException {
    String qrelsURL = SERVER_PATH + qrelsPath.getFileName().toString();
    System.out.println("Downloading qrels from " + qrelsURL);
    Path localQrelsPath = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve(qrelsPath.getFileName());

    try {
      FileUtils.copyURLToFile(new URI(qrelsURL).toURL(), localQrelsPath.toFile());
    } catch (Exception e) {
      throw new IOException("Error downloading qrels from " + qrelsURL);
    }
    return localQrelsPath;
  }

  public static boolean contains(Path qrelsPath) {
    return registry().containsValue(qrelsPath.getFileName().toString());
  }

  public static boolean containsSymbol(Path qrelsPath) {
    return registry().containsKey(qrelsPath.getFileName().toString());
  }

  public static Path extendSymbol(Path symbol) {
    String returnPath = registry().get(symbol.getFileName().toString());
    if (returnPath == null) {
      return symbol;
    }
    return Path.of(returnPath);
  }
}
