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
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.FeatureExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

/**
 * Feature extractor class that forms the base for other feature extractors
 */
public class FeatureExtractorUtils {
    private static final Logger LOG = LogManager.getLogger(FeatureExtractorUtils.class);
    private IndexReader reader;
    private IndexSearcher searcher;
    private List<FeatureExtractor> extractors = new ArrayList<>();
    private Set<String> fieldsToLoad = new HashSet<>();
    private ExecutorService pool;
    private Map<String, Future<String>> tasks = new HashMap<>();

    public FeatureExtractorUtils add(FeatureExtractor extractor) {
        extractors.add(extractor);
        if((extractor.getField()!=null)&&(!fieldsToLoad.contains(extractor.getField())))
            fieldsToLoad.add(extractor.getField());
        return this;
    }

    public ArrayList<output> extract(List<String> queryTokens, List<String> docIds) throws ExecutionException, InterruptedException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        input root = new input();
        root.qid = "-1";
        root.queryTokens = queryTokens;
        root.docIds = docIds;
        this.lazyExtract(mapper.writeValueAsString(root));
        String res = this.getResult(root.qid);
        TypeReference<ArrayList<output>> typeref = new TypeReference<ArrayList<output>>() {};
        return mapper.readValue(res, typeref);
    }

    public void addTask(String qid, List<String> queryTokens, List<String> docIds) {
        if(tasks.containsKey(qid))
            throw new IllegalArgumentException("existed qid");
        tasks.put(qid, pool.submit(() -> {
            ObjectMapper mapper = new ObjectMapper();
            List<output> result = new ArrayList<>();
            for(String docId: docIds) {
                Query q = new TermQuery(new Term(IndexArgs.ID, docId));
                TopDocs topDocs = searcher.search(q, 1);
                if (topDocs.totalHits.value == 0) {
                    LOG.warn(String.format("Document Id %s expected but not found in index, skipping...", docId));
                    continue;
                }

                ScoreDoc hit = topDocs.scoreDocs[0];
                Document doc = reader.document(hit.doc, fieldsToLoad);

                Terms terms = reader.getTermVector(hit.doc, IndexArgs.CONTENTS);
                List<Float> features = new ArrayList<>();
                for (int i = 0; i < extractors.size(); i++) {
                    features.add(extractors.get(i).extract(doc, terms, String.join(",", queryTokens), queryTokens, reader));
                }
                result.add(new output(docId,features));
            }
            return mapper.writeValueAsString(result);
        }));
    }

    public void lazyExtract(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        input root = mapper.readValue(jsonString, input.class);
        this.addTask(root.qid, root.queryTokens, root.docIds);
    }

    public String getResult(String qid) throws ExecutionException, InterruptedException {
        return tasks.remove(qid).get();
    }

    public FeatureExtractorUtils(String indexDir) throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
        reader = DirectoryReader.open(indexDirectory);
        searcher = new IndexSearcher(reader);
        fieldsToLoad.add(IndexArgs.ID);
        pool = Executors.newFixedThreadPool(1);
    }

    public FeatureExtractorUtils(String indexDir, int workNum) throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
        reader = DirectoryReader.open(indexDirectory);
        searcher = new IndexSearcher(reader);
        fieldsToLoad.add(IndexArgs.ID);
        pool = Executors.newFixedThreadPool(workNum);
    }

    public FeatureExtractorUtils(IndexReader reader) throws IOException {
        this.reader = reader;
        searcher = new IndexSearcher(reader);
        fieldsToLoad.add(IndexArgs.ID);
        pool = Executors.newFixedThreadPool(1);
    }

    public FeatureExtractorUtils(IndexReader reader, int workNum) throws IOException {
        this.reader = reader;
        searcher = new IndexSearcher(reader);
        fieldsToLoad.add(IndexArgs.ID);
        pool = Executors.newFixedThreadPool(workNum);
    }

    public void close() throws IOException {
        pool.shutdown();
        reader.close();
    }

}

class input{
    String qid;
    List<String> queryTokens;
    List<String> docIds;

    input(){}

    public String getQid() {
        return qid;
    }

    public List<String> getDocIds() {
        return docIds;
    }

    public List<String> getQueryTokens() {
        return queryTokens;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public void setDocIds(List<String> docIds) {
        this.docIds = docIds;
    }

    public void setQueryTokens(List<String> queryTokens) {
        this.queryTokens = queryTokens;
    }
}

class output{
    String pid;
    List<Float> features;

    output(){}

    output(String pid, List<Float> features){
        this.pid = pid;
        this.features = features;
    }

    public String getPid() {
        return pid;
    }

    public List<Float> getFeatures() {
        return features;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setFeatures(List<Float> features) {
        this.features = features;
    }
}