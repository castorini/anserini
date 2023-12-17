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

import io.anserini.index.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ScoredDocuments object that converts TopDocs from the searcher into an Anserini format
 */
public class ScoredDocuments {
  private static final Logger LOG = LogManager.getLogger(ScoredDocuments.class);

  public String[] docids;
  public int[] lucene_docids;
  public Document[] lucene_documents;
  public float[] scores;

  public static ScoredDocuments fromTopDocs(TopDocs rs, IndexSearcher searcher) {
    ScoredDocuments scoredDocs = new ScoredDocuments();
    scoredDocs.docids = new String[rs.scoreDocs.length];
    scoredDocs.lucene_documents = new Document[rs.scoreDocs.length];
    scoredDocs.lucene_docids = new int[rs.scoreDocs.length];
    scoredDocs.scores = new float[rs.scoreDocs.length];

    for (int i=0; i<rs.scoreDocs.length; i++) {
      try {
        scoredDocs.lucene_documents[i] = searcher.storedFields().document(rs.scoreDocs[i].doc);
        scoredDocs.docids[i] = scoredDocs.lucene_documents[i].get(Constants.ID);
      } catch (IOException e) {
        throw new RuntimeException();
      }
      scoredDocs.scores[i] = rs.scoreDocs[i].score;
      scoredDocs.lucene_docids[i] = rs.scoreDocs[i].doc;
    }

    return scoredDocs;
  }

  public static ScoredDocuments fromQrels(Map<String, Integer> qrels, IndexReader reader) throws IOException {
    ScoredDocuments scoredDocs = new ScoredDocuments();

    List<Document> documentList = new ArrayList<>();
    List<Integer> idList = new ArrayList<>();
    List<Float> scoreList = new ArrayList<>();

    IndexSearcher searcher = new IndexSearcher(reader);
    StoredFields storedFields = searcher.storedFields();
    for (Map.Entry<String, Integer> qrelsDocScorePair : qrels.entrySet()) {
      String externalDocid = qrelsDocScorePair.getKey();
      Query q = new TermQuery(new Term(Constants.ID, externalDocid));
      TopDocs rs = searcher.search(q, 1);
      try {
        documentList.add(storedFields.document(rs.scoreDocs[0].doc));
        idList.add(rs.scoreDocs[0].doc);
        scoreList.add(Float.valueOf(qrelsDocScorePair.getValue().floatValue()));
      } catch (IOException e) {
        e.printStackTrace();
        documentList.add(null);
      } catch (ArrayIndexOutOfBoundsException e){
        // e.printStackTrace();
        LOG.warn("Cannot find document " + externalDocid);
      }
    }

    int length = documentList.size();
    scoredDocs.lucene_documents = new Document[length];
    scoredDocs.lucene_docids = new int[length];
    scoredDocs.scores = new float[length];
    scoredDocs.lucene_documents = documentList.toArray(scoredDocs.lucene_documents);
    scoredDocs.lucene_docids = ArrayUtils.toPrimitive(idList.toArray(new Integer[length]));
    scoredDocs.scores = ArrayUtils.toPrimitive(scoreList.toArray(new Float[length]), Float.NaN);

    return scoredDocs;
  }

}
