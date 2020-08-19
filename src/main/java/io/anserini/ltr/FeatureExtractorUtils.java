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

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
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
    private List<FeatureExtractor<String>> extractors = new ArrayList<>();
    private Set<String> fieldsToLoad = new HashSet<>();
    private ExecutorService pool;
    private Map<String, Future<Map<String, List<Float>>>> tasks = new HashMap<>();

    public FeatureExtractorUtils add(FeatureExtractor<String> extractor) {
        extractors.add(extractor);
        if(!fieldsToLoad.contains(extractor.getField()))
            fieldsToLoad.add(extractor.getField());
        return this;
    }

    public Map<String, List<Float>> extract(List<String> queryTokens, List<String> docIds) throws Exception {
        String qid = "-1";
        this.lazyExtract(qid,queryTokens, docIds);
        return this.getResult(qid);
    }

    public void lazyExtract(String qid, List<String> queryTokens, List<String> docIds) {
        tasks.put(qid, pool.submit(() -> {
            Map<String, List<Float>> result = new HashMap<>();
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
                // Construct the reranker context
                RerankerContext<String> context = new RerankerContext<>(searcher, "-1",
                        null, null, String.join(" ", queryTokens),
                        queryTokens,
                        null, null);
                List<Float> features = new ArrayList<>();
                for (int i = 0; i < extractors.size(); i++) {
                    features.add(extractors.get(i).extract(doc, terms, context));
                }
                result.put(docId,features);
            }
            return result;
        }));
    }

    public Map<String, List<Float>> getResult(String qid) throws ExecutionException, InterruptedException {
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

}