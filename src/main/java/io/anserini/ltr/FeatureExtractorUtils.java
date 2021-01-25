/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.ltr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;
import io.anserini.ltr.feature.base.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Feature extractor class that exposed in Pyserini
 */
public class FeatureExtractorUtils {
  private IndexReader reader;
  private IndexSearcher searcher;
  private Set<String> featureNames = new HashSet<>();
  private List<FeatureExtractor> extractors = new ArrayList<>();
  private Set<String> fieldsToLoad = new HashSet<>();
  private Set<String> qfieldsToLoad = new HashSet<>();
  private ExecutorService pool;
  private Map<String, Future<byte[]>> tasks = new HashMap<>();
  private Map<String, Future<List<debugOutput>>> debugTasks = new HashMap<>();
  private Boolean executeBM25Stat = false;

  /**
   * set up the feature we wish to extract
   * @param extractor initialized FeatureExtractor instance
   * @return
   */
  public FeatureExtractorUtils add(FeatureExtractor extractor) throws IOException {
    if(featureNames.contains(extractor.getName())){
      throw new IOException("feature extractor already exist");
    }
    featureNames.add(extractor.getName());
    extractors.add(extractor);
    String field = extractor.getField();
    String qfield = extractor.getQField();
    if(field!=null)
      fieldsToLoad.add(field);
    if(qfield != null)
      qfieldsToLoad.add(qfield);
    if(extractor instanceof BM25HMean)
      executeBM25Stat = true;
    if(extractor instanceof BM25Max)
      executeBM25Stat = true;
    if(extractor instanceof BM25Mean)
      executeBM25Stat = true;
    if(extractor instanceof BM25Min)
      executeBM25Stat = true;
    if(extractor instanceof BM25Quartile)
      executeBM25Stat = true;
    if(extractor instanceof BM25Var)
      executeBM25Stat = true;
    return this;
  }

  public List<String> list() {
    List<String> nameList = new ArrayList<>();
    for (int i = 0; i < extractors.size(); i++) {
      nameList.add(extractors.get(i).getName());
    }
    return nameList;
  }

  /**
   * mainly used for testing
   * @param docIds external document ids that you wish to collect; users need to make sure it is present
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   * @throws JsonProcessingException
   */
  public List<debugOutput> extract(String qid, List<String> docIds, List<String> queryTokens) throws ExecutionException, InterruptedException, JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> json = new HashMap();
    json.put("qid", qid);
    json.put("docIds", docIds);
    json.put("analyzed", queryTokens);
    this.debugExtract(mapper.writeValueAsString(json));
    return this.getDebugResult(qid);
  }

  /**
   * submit tasks to workers
   * @param qid unique query id; users need to make sure it is not duplicated
   * @param docIds external document ids that you wish to collect; users need to make sure it is present
   */
  public void addDebugTask(String qid, List<String> docIds, JsonNode jsonQuery) {
    if(debugTasks.containsKey(qid))
      throw new IllegalArgumentException("existed qid");
    debugTasks.put(qid, pool.submit(() -> {
      List<FeatureExtractor> localExtractors = new ArrayList<>();
      for(FeatureExtractor e: extractors){
        localExtractors.add(e.clone());
      }
      ObjectMapper mapper = new ObjectMapper();
      DocumentContext documentContext = new DocumentContext(reader, searcher, fieldsToLoad);
      QueryContext queryContext = new QueryContext(qid, qfieldsToLoad, jsonQuery);
      List<debugOutput> result = new ArrayList<>();
      if (executeBM25Stat){
        QueryFieldContext qcontext = queryContext.fieldContexts.get("analyzed");
        documentContext.generateBM25Stat(qcontext.queryTokens);
      }

      for(String docId: docIds) {
        Query q = new TermQuery(new Term(IndexArgs.ID, docId));
        TopDocs topDocs = searcher.search(q, 1);
        if (topDocs.totalHits.value == 0) {
          throw new IOException(String.format("Document Id %s expected but not found in index", docId));
        }
        ScoreDoc hit = topDocs.scoreDocs[0];
        documentContext.updateDoc(docId, hit.doc);
        List<Float> features = new ArrayList<>();
        List<Long> time = new ArrayList<>();
        for(int i = 0; i < localExtractors.size(); i++){
          time.add(0L);
        }
        for (int i = 0; i < localExtractors.size(); i++) {
          long start = System.nanoTime();
          float extractedFeature = localExtractors.get(i).extract(documentContext, queryContext);
          assert extractedFeature == extractedFeature;
          features.add(extractedFeature);
          long end = System.nanoTime();
          time.set(i, time.get(i) + end - start);
        }
        result.add(new debugOutput(docId,features, time));
      }
      return result;
    }));
  }

  public void addTask(String qid, List<String> docIds, JsonNode jsonQuery) {
    if(tasks.containsKey(qid))
      throw new IllegalArgumentException("existed qid");
    tasks.put(qid, pool.submit(() -> {
      List<FeatureExtractor> localExtractors = new ArrayList<>();
      for(FeatureExtractor e: extractors){
        localExtractors.add(e.clone());
      }
      ObjectMapper mapper = new ObjectMapper();
      DocumentContext documentContext = new DocumentContext(reader, searcher, fieldsToLoad);
      QueryContext queryContext = new QueryContext(qid, qfieldsToLoad, jsonQuery);

      if (executeBM25Stat){
        QueryFieldContext qcontext = queryContext.fieldContexts.get("analyzed");
        documentContext.generateBM25Stat(qcontext.queryTokens);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      //strict follow doc id order
      for(String docId: docIds) {
          Query q = new TermQuery(new Term(IndexArgs.ID, docId));
          TopDocs topDocs = searcher.search(q, 1);
          if (topDocs.totalHits.value == 0) {
            throw new IOException(String.format("Document Id %s expected but not found in index", docId));
          }

          ScoreDoc hit = topDocs.scoreDocs[0];
          documentContext.updateDoc(docId, hit.doc);

          for (int i = 0; i < localExtractors.size(); i++) {
            dos.writeFloat(localExtractors.get(i).extract(documentContext, queryContext));
          }
      }

      dos.flush();
      return baos.toByteArray();
    }));
  }



  /**
   * submit tasks to workers, exposed in Pyserini
   * @throws JsonProcessingException
   */
  public String lazyExtract(String jsonInput) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readValue(jsonInput, JsonNode.class);
    String qid = root.get("qid").asText();
    List<String> docIds = mapper.convertValue(root.get("docIds"), ArrayList.class);
    this.addTask(qid, docIds, root);
    return qid;
  }

  /**
   * submit tasks to workers, exposed in Pyserini
   * @throws JsonProcessingException
   */
  public String debugExtract(String jsonInput) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readValue(jsonInput, JsonNode.class);
    String qid = root.get("qid").asText();
    List<String> docIds = mapper.convertValue(root.get("docIds"), ArrayList.class);
    this.addDebugTask(qid, docIds, root);
    this.addTask(qid, docIds, root);
    return qid;
  }

  /**
   * blocked until the result is ready
   * @param qid the query id you wise to fetch the result
   * @return
   * @throws ExecutionException
   * @throws InterruptedException
   */
  public byte[] getResult(String qid) throws ExecutionException, InterruptedException {
    return tasks.remove(qid).get();
  }

  public List<debugOutput> getDebugResult(String qid) throws ExecutionException, InterruptedException {
    List<debugOutput> debugRes = debugTasks.remove(qid).get();
    byte[] res =  tasks.remove(qid).get();
    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(res));
    int numElement = res.length/4;
    int numCol = featureNames.size();
    int numRow = numElement/numCol;
    float[][] features = new float[numRow][numCol];
    for(int idx=0;idx*4<res.length;idx++){
      try {
        int rowIdx = idx/numCol;
        int colIdx = idx%numCol;
        features[rowIdx][colIdx] = dis.readFloat();
      } catch (IOException e) {
        int rowIdx = idx/numCol;
        int colIdx = idx%numCol;
        features[rowIdx][colIdx]= 0;
        e.printStackTrace();
      }
    }
    float[][] debugFeatures = new float[numRow][numCol];
    for(int rowIdx=0;rowIdx<debugRes.size();rowIdx++){
      debugOutput output = debugRes.get(rowIdx);
      for(int colIdx=0;colIdx<output.features.size();colIdx++){
        debugFeatures[rowIdx][colIdx] = output.features.get(colIdx);
      }
    }
    for(int rowIdx=0;rowIdx<numRow;rowIdx++){
      for(int colIdx=0;colIdx<numCol;colIdx++){
        assert debugFeatures[rowIdx][colIdx] == features[rowIdx][colIdx];
      }
    }
    return debugRes;
  }

  /**
   * @param indexDir index path to work on
   * @throws IOException
   */
  public FeatureExtractorUtils(String indexDir) throws IOException {
    Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
    reader = DirectoryReader.open(indexDirectory);
    searcher = new IndexSearcher(reader);
    pool = Executors.newFixedThreadPool(1);
  }

  /**
   * @param indexDir index path to work on
   * @param workNum worker threads number
   * @throws IOException
   */
  public FeatureExtractorUtils(String indexDir, int workNum) throws IOException {
    Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
    reader = DirectoryReader.open(indexDirectory);
    searcher = new IndexSearcher(reader);
    pool = Executors.newFixedThreadPool(workNum);
  }

  /**
   * for testing purpose
   * @param reader initialized indexreader
   * @throws IOException
   */
  public FeatureExtractorUtils(IndexReader reader) throws IOException {
    this.reader = reader;
    searcher = new IndexSearcher(reader);
    pool = Executors.newFixedThreadPool(1);
  }

  /**
   * @param reader
   * @param workNum
   * @throws IOException
   */
  public FeatureExtractorUtils(IndexReader reader, int workNum) throws IOException {
    this.reader = reader;
    searcher = new IndexSearcher(reader);
    pool = Executors.newFixedThreadPool(workNum);
  }

  /**
   * close to avoid theadleaking warning during test
   * @throws IOException
   */
  public void close() throws IOException {
    pool.shutdown();
    reader.close();
  }

}

class debugOutput{
  String pid;
  List<Float> features;
  List<Long> time;

  debugOutput(){}

  debugOutput(String pid, List<Float> features, List<Long> time){
    this.pid = pid;
    this.features = features;
    this.time = time;
  }

  public String getPid() {
    return pid;
  }

  public List<Float> getFeatures() {
    return features;
  }

  public List<Long> getTime() { return time; }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public void setFeatures(List<Float> features) {
    this.features = features;
  }

  public void setTime(List<Long> time) { this.time = time; }
}
