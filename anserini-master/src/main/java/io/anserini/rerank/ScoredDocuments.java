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

package io.anserini.rerank;

import io.anserini.index.IndexArgs;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.IOException;

/**
 * ScoredDocuments object that converts TopDocs from the searcher into an Anserini format
 */
public class ScoredDocuments {
  private static final Logger LOG = LogManager.getLogger(ScoredDocuments.class);
  // Array of document objects
  public Document[] documents;
  // The docIds as used by the index reader
  public int[] ids;
  // Scores returned from the searcher's similarity
  public float[] scores;
  
  public static ScoredDocuments fromTopDocs(TopDocs rs, IndexSearcher searcher) {
    ScoredDocuments scoredDocs = new ScoredDocuments();
    scoredDocs.documents = new Document[rs.scoreDocs.length];
    scoredDocs.ids = new int[rs.scoreDocs.length];
    scoredDocs.scores = new float[rs.scoreDocs.length];

    for (int i=0; i<rs.scoreDocs.length; i++) {
      try {
        scoredDocs.documents[i] = searcher.doc(rs.scoreDocs[i].doc);
      } catch (IOException e) {
        e.printStackTrace();
        scoredDocs.documents[i] = null;
      }
      scoredDocs.scores[i] = rs.scoreDocs[i].score;
      scoredDocs.ids[i] = rs.scoreDocs[i].doc;
    }

    return scoredDocs;
  }

  public static ScoredDocuments fromSolrDocs(SolrDocumentList rs) {

    ScoredDocuments scoredDocs = new ScoredDocuments();

    int length = rs.size();
    scoredDocs.documents = new Document[length];
    scoredDocs.ids = new int[length];
    scoredDocs.scores = new float[length];

    for (int i = 0; i < length; i++) {

      SolrDocument d = rs.get(i);

      // Create placeholder copies of Lucene Documents
      // Intention is for compatibility with ScoreTiesAdjusterReranker without disturbing other aspects of reranker code

      Document document = new Document();
      String id = d.getFieldValue("id").toString();
      float score = (float) d.getFieldValue("score");

      // Store the collection docid.
      document.add(new StringField(IndexArgs.ID, id, Field.Store.YES));
      // This is needed to break score ties by docid.
      document.add(new SortedDocValuesField(IndexArgs.ID, new BytesRef(id)));
      scoredDocs.documents[i] = document;
      scoredDocs.scores[i] = score;
      scoredDocs.ids[i] = i; // no internal Lucene ID available, use index as placeholder
    }

    return scoredDocs;
  }

  public static ScoredDocuments fromESDocs(SearchHits rs) {

    ScoredDocuments scoredDocs = new ScoredDocuments();
    SearchHit[] searchHits = rs.getHits();

    int length = searchHits.length;
    scoredDocs.documents = new Document[length];
    scoredDocs.ids = new int[length];
    scoredDocs.scores = new float[length];

    for (int i = 0; i < length; i++) {

      SearchHit hit = searchHits[i];

      // Create placeholder copies of Lucene Documents
      // Intention is for compatibility with ScoreTiesAdjusterReranker without disturbing other aspects of reranker code

      Document document = new Document();
      String id = hit.getId();
      float score = hit.getScore();

      // Store the collection docid.
      document.add(new StringField(IndexArgs.ID, id, Field.Store.YES));
      // This is needed to break score ties by docid.
      document.add(new SortedDocValuesField(IndexArgs.ID, new BytesRef(id)));
      scoredDocs.documents[i] = document;
      scoredDocs.scores[i] = score;
      scoredDocs.ids[i] = i; // no internal Lucene ID available, use index as placeholder
    }

    return scoredDocs;
  }

  public static ScoredDocuments fromQrels(Map<String, Integer> qrels, IndexReader reader) throws IOException {
    ScoredDocuments scoredDocs = new ScoredDocuments();

    List<Document> documentList = new ArrayList<>();
    List<Integer> idList = new ArrayList<>();
    List<Float> scoreList = new ArrayList<>();

    IndexSearcher searcher;
    int i = 0;
    for (Map.Entry<String, Integer> qrelsDocScorePair : qrels.entrySet()) {
      String externalDocid = qrelsDocScorePair.getKey();
      searcher = new IndexSearcher(reader);
      Query q = new TermQuery(new Term(IndexArgs.ID, externalDocid));
      TopDocs rs = searcher.search(q, 1);
      try {
        documentList.add(searcher.doc(rs.scoreDocs[0].doc));
        idList.add(rs.scoreDocs[0].doc);
        scoreList.add(Float.valueOf(qrelsDocScorePair.getValue().floatValue()));
        i++;
      } catch (IOException e) {
        e.printStackTrace();
        documentList.add(null);
      } catch (ArrayIndexOutOfBoundsException e){
        // e.printStackTrace();
        LOG.warn("Cannot find document " + externalDocid);
      }
    }

    int length = documentList.size();
    scoredDocs.documents = new Document[length];
    scoredDocs.ids = new int[length];
    scoredDocs.scores = new float[length];
    scoredDocs.documents = documentList.toArray(scoredDocs.documents);
    scoredDocs.ids = ArrayUtils.toPrimitive(idList.toArray(new Integer[length]));
    scoredDocs.scores = ArrayUtils.toPrimitive(scoreList.toArray(new Float[length]), Float.NaN);

    return scoredDocs;
  }

}
