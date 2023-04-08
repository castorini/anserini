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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class RelevanceJudgments {
  final private Map<String, Map<String, Integer>> qrels;
  final private static String CACHE_DIR = System.getProperty("user.home") + "/.cache/anserini/topics-and-qrels";
  final private static String CLOUD_PATH = "https://raw.githubusercontent.com/castorini/anserini-tools/master/topics-and-qrels";


  public static RelevanceJudgments fromQrels(Qrels qrels) throws IOException{
    return new RelevanceJudgments("src/main/resources/" + qrels.path);
  }

  public RelevanceJudgments(String file) throws IOException {
    qrels = new HashMap<>();
    Path qrelPath = Path.of(file);
    try{
      qrelPath = getQrelPath(qrelPath);
    }catch (IOException e){
      System.out.println("Qrel file not found");
    }

    try (BufferedReader br = new BufferedReader(new FileReader(qrelPath.toString()))) {
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

    }
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
  private static String getCacheDir() {
    File cacheDir = new File(CACHE_DIR);
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir.getPath();
  }
  
  /**
   * Method will return the qrel file as a string
   * 
   * @param qrelPath path to qrel file
   * @return qrel file as a string
   * @throws IOException
   */
  public static String getQrelsResource(Path qrelPath) throws IOException{
    Path resultPath = qrelPath;
    try{
      resultPath = getQrelPath(qrelPath);
    }catch (Exception e){
      throw new IOException("Could not get qrel file from cloud or local file system");
    }

    InputStream inputStream = Files.newInputStream(resultPath);
    String raw = new String(inputStream.readAllBytes());
    return raw;
  }

  /**
   * Method will look for the absolute qrel path and return it as a Path object
   * 
   * @param qrelPath path to qrel file
   * @return qrel path
   * @throws IOException
   */
  private static Path getQrelPath(Path qrelPath) throws IOException{
    if (!Qrels.contains(qrelPath)) {
      // If the topic file is not in the list of known topics, we assume it is a local file.
      Path tempPath = Path.of(getCacheDir() + "/" + qrelPath.getFileName().toString());
      if (Files.exists(tempPath)) {
        // if it is a unregistred topic in the Topics Enum, but it is in the cache, we use it
        return tempPath;
      }
      return qrelPath;
    }
    
    Path resultPath = getNewQrelAbsPath(qrelPath);
    if (!Files.exists(resultPath)) {
      resultPath = getQrelFromCloud(qrelPath);
    }
    return resultPath;
  }

  public static Path getNewQrelAbsPath(Path qrelPath){
    return Paths.get(getCacheDir(), qrelPath.getFileName().toString());
  }

  /**
   * Method will download the qrel file from the cloud and return the path to the file
   * 
   * @param qrelPath path to qrel file
   * @return path to qrel file
   * @throws IOException
   */
  public static Path getQrelFromCloud(Path qrelPath) throws IOException{
    String qrelURL = CLOUD_PATH + "/" + qrelPath.getFileName().toString();
    System.out.println("Downloading qrel from cloud " + qrelURL.toString());
    File qrelFile = new File(getCacheDir(), qrelPath.getFileName().toString());
    try{
      FileUtils.copyURLToFile(new URL(qrelURL), qrelFile);
    }catch (Exception e){
      System.out.println("Error downloading topics from cloud " + qrelURL.toString());
      throw e;
    }
    return qrelFile.toPath();
  }
}
