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

package io.anserini.rerank.lib;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.index.Constants;
import io.anserini.index.IndexReaderUtils;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.search.ScoredDocs;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.anserini.index.generator.WashingtonPostGenerator.WashingtonPostField.PUBLISHED_DATE;

/*
 * TREC News Track Background Linking task postprocessing.
 * Near-duplicate documents (similar/same with the query docid) will be removed by comparing
 * their cosine similarity with the query docid.
 */
public class NewsBackgroundLinkingReranker implements Reranker {
  private final Analyzer analyzer;
  private final Class parser;

  public NewsBackgroundLinkingReranker(Analyzer analyzer, Class parser) {
    assert analyzer != null;
    assert parser != null;

    this.analyzer = analyzer;
    this.parser = parser;
  }

  @Override
  public ScoredDocs rerank(ScoredDocs docs, RerankerContext context) {
    assert docs != null;
    assert context != null;

    IndexReader reader = context.getIndexSearcher().getIndexReader();
    String queryDocId = context.getQueryDocId();
    final Map<String, Long> queryTermsMap = convertDocVectorToMap(reader, queryDocId);

    List<Map<String, Long>> docsVectorsMap = new ArrayList<>();
    for (int i = 0; i < docs.lucene_documents.length; i++) {
      String docid = docs.lucene_documents[i].getField(Constants.ID).stringValue();
      docsVectorsMap.add(convertDocVectorToMap(reader, docid));
    }

    // remove the duplicates: 1. the same doc with the query doc 2. duplicated docs in the results
    Set<Integer> toRemove = new HashSet<>();
    for (int i = 0; i < docs.lucene_documents.length; i++) {
      if (toRemove.contains(i)) continue;
      if (computeCosineSimilarity(queryTermsMap, docsVectorsMap.get(i)) >= 0.9) {
        toRemove.add(i);
        continue;
      }
      for (int j = i + 1; j < docs.lucene_documents.length; j++) {
        if (computeCosineSimilarity(docsVectorsMap.get(i), docsVectorsMap.get(j)) >= 0.9) {
          toRemove.add(j);
        }
      }
    }

    if (context.getSearchArgs().backgroundLinkingDatefilter) {
      try {
        int luceneId = IndexReaderUtils.convertDocidToLuceneDocid(reader, queryDocId);
        Document queryDoc = reader.storedFields().document(luceneId);
        long queryDocDate = Long.parseLong(queryDoc.getField(PUBLISHED_DATE.name).stringValue());
        for (int i = 0; i < docs.lucene_documents.length; i++) {
          long date = Long.parseLong(docs.lucene_documents[i].getField(PUBLISHED_DATE.name).stringValue());
          if (date > queryDocDate) {
            toRemove.add(i);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    ScoredDocs scoredDocs = new ScoredDocs();
    int resSize = docs.lucene_documents.length - toRemove.size();
    scoredDocs.lucene_documents = new Document[resSize];
    scoredDocs.docids = new String[resSize];
    scoredDocs.lucene_docids = new int[resSize];
    scoredDocs.scores = new float[resSize];
    int idx = 0;
    for (int i = 0; i < docs.lucene_documents.length; i++) {
      if (!toRemove.contains(i)) {
        scoredDocs.lucene_documents[idx] = docs.lucene_documents[i];
        scoredDocs.docids[idx] = docs.docids[i];
        scoredDocs.scores[idx] = docs.scores[i];
        scoredDocs.lucene_docids[idx] = docs.lucene_docids[i];
        idx++;
      }
    }

    return scoredDocs;
  }

  private Map<String, Long> convertDocVectorToMap(IndexReader reader, String docid) {
    Map<String, Long> m = new HashMap<>();
    try {
      StoredFields storedFields = reader.storedFields();
      Terms terms = reader.termVectors().get(
          IndexReaderUtils.convertDocidToLuceneDocid(reader, docid), Constants.CONTENTS);
      if (terms != null) {
        TermsEnum it = terms.iterator();
        while (it.next() != null) {
          String term = it.term().utf8ToString();
          long tf = it.totalTermFreq();
          m.put(term, tf);
        }
      } else {
        if (parser == null) {
          throw new NullPointerException("Please provide an index with stored doc vectors or input -collection param");
        }
        Map<String, Long> termFreqMap = AnalyzerUtils.computeDocumentVector(analyzer, parser,
            storedFields.document(IndexReaderUtils.convertDocidToLuceneDocid(reader, docid)).getField(Constants.RAW).stringValue());
        return termFreqMap;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return m;
  }

  private double dotProduct(Map<String, Long> profile1, Map<String, Long> profile2) {
    // Loop over the smallest map
    Map<String, Long> small_profile = profile2;
    Map<String, Long> large_profile = profile1;
    if (profile1.size() < profile2.size()) {
      small_profile = profile1;
      large_profile = profile2;
    }

    double agg = 0;
    for (Map.Entry<String, Long> entry : small_profile.entrySet()) {
      long i = large_profile.getOrDefault(entry.getKey(), 0L);
      agg += 1.0 * entry.getValue() * i;
    }

    return agg;
  }

  private double computeCosineSimilarity(Map<String, Long> profile1, Map<String, Long> profile2) {
    return dotProduct(profile1, profile2) / (computeL2Norm(profile1) * computeL2Norm(profile2));
  }

  /**
   * Compute the norm L2 : sqrt(Sum_i( v_i²)).
   *
   * @param profile
   * @return L2 norm
   */
  private double computeL2Norm(final Map<String, Long> profile) {
    double agg = 0;

    for (Map.Entry<String, Long> entry : profile.entrySet()) {
      agg += 1.0 * entry.getValue() * entry.getValue();
    }

    return Math.sqrt(agg);
  }

  @Override
  public String tag() {
    return "";
  }
}
