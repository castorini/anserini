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

/**
 * Feature extractor class that forms the base for other feature extractors
 */
public class FeatureExtractorUtils<K> {
    private static final Logger LOG = LogManager.getLogger(BaseFeatureExtractor.class);
    private IndexReader reader;
    private IndexSearcher searcher;
    public List<FeatureExtractor> extractors = new ArrayList<>();
    private Set<String> fieldsToLoad = new HashSet<>();

    public FeatureExtractorUtils add(FeatureExtractor extractor) {
        extractors.add(extractor);
        if(!fieldsToLoad.contains(extractor.getField()))
            fieldsToLoad.add(extractor.getField());
        return this;
    }

    public Map<String, List> extract(K queryID, String queryText, List<String> queryTokens, Query query, List<String> doc_ids) throws Exception {
        Map<String, List> result = new HashMap<>();
        for(String docId: doc_ids) {
            Query q = new TermQuery(new Term(IndexArgs.ID, docId));
            TopDocs topDocs = searcher.search(q, 1);
            if (topDocs.totalHits.value == 0) {
                LOG.warn(String.format("Document Id %s expected but not found in index, skipping...", docId));
                continue;
            }

            ScoreDoc hit = topDocs.scoreDocs[0];
            Document doc = reader.document(hit.doc, fieldsToLoad);

            //TODO factor for test
            Terms terms = reader.getTermVector(hit.doc, IndexArgs.CONTENTS);
            // Construct the reranker context
            RerankerContext<K> context = new RerankerContext<>(searcher, queryID,
                    query, null, queryText,
                    queryTokens,
                    null, null);
            List<Object> features = new ArrayList<>();
            for (int i = 0; i < extractors.size(); i++) {
                features.add(extractors.get(i).extract(doc, terms, context));
            }
        }
        return result;
    }

    public FeatureExtractorUtils(String indexDir) throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDir));
        this.reader = DirectoryReader.open(indexDirectory);
        this.searcher = new IndexSearcher(this.reader);
    }


}