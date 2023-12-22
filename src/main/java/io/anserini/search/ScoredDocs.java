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

package io.anserini.search;

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
 * This class, {@link ScoredDocs} and its cousin {@link ScoredDoc} are closely related and should be discussed in
 * the same context. Both are designed to be wrappers around Lucene's {@link TopDocs} object, which is the raw results
 * from a search. Both <code>ScoredDocs</code> and <code>ScoredDoc[]</code> hold exactly the same information, except
 * that the first is an object of arrays, whereas the second is an array of objects. In the development of Anserini,
 * <code>ScoredDocs</code> seemed more natural for reranking, but when passing results over to Python,
 * <code>ScoredDoc[]</code> seemed more natural.
 */
public class ScoredDocs {
  private static final Logger LOG = LogManager.getLogger(ScoredDocs.class);

  public String[] docids;
  public int[] lucene_docids;
  public Document[] lucene_documents;
  public float[] scores;

  public static ScoredDocs fromTopDocs(TopDocs rs, IndexSearcher searcher) {
    ScoredDocs scoredDocs = new ScoredDocs();
    scoredDocs.docids = new String[rs.scoreDocs.length];
    scoredDocs.lucene_documents = new Document[rs.scoreDocs.length];
    scoredDocs.lucene_docids = new int[rs.scoreDocs.length];
    scoredDocs.scores = new float[rs.scoreDocs.length];

    for (int i=0; i<rs.scoreDocs.length; i++) {
      try {
        scoredDocs.lucene_documents[i] = searcher.storedFields().document(rs.scoreDocs[i].doc);
        scoredDocs.docids[i] = scoredDocs.lucene_documents[i].get(Constants.ID);
      } catch (NullPointerException | IOException e) {
        throw new RuntimeException(String.format("Cannot find lucene document %d.", rs.scoreDocs[i].doc));
      }
      scoredDocs.scores[i] = rs.scoreDocs[i].score;
      scoredDocs.lucene_docids[i] = rs.scoreDocs[i].doc;
    }

    return scoredDocs;
  }

  public static ScoredDocs fromQrels(Map<String, Integer> qrels, IndexReader reader) {
    ScoredDocs scoredDocs = new ScoredDocs();

    List<Document> lucene_documents = new ArrayList<>();
    List<Integer> lucene_docids = new ArrayList<>();
    List<String> docids = new ArrayList<>();
    List<Float> score = new ArrayList<>();

    try {
      IndexSearcher searcher = new IndexSearcher(reader);
      StoredFields storedFields = searcher.storedFields();
      for (Map.Entry<String, Integer> qrelsDocScorePair : qrels.entrySet()) {
        String externalDocid = qrelsDocScorePair.getKey();
        Query q = new TermQuery(new Term(Constants.ID, externalDocid));
        TopDocs rs = searcher.search(q, 1);
        lucene_documents.add(storedFields.document(rs.scoreDocs[0].doc));
        lucene_docids.add(rs.scoreDocs[0].doc);
        score.add(Float.valueOf(qrelsDocScorePair.getValue().floatValue()));
        docids.add(storedFields.document(rs.scoreDocs[0].doc).get(Constants.ID));
      }
    } catch (IOException | ArrayIndexOutOfBoundsException | NullPointerException e) {
      throw new RuntimeException("Error loading qrels.");
    }

    int length = lucene_documents.size();
    scoredDocs.lucene_documents = new Document[length];
    scoredDocs.lucene_docids = new int[length];
    scoredDocs.docids = new String[length];
    scoredDocs.scores = new float[length];

    scoredDocs.lucene_documents = lucene_documents.toArray(scoredDocs.lucene_documents);
    scoredDocs.lucene_docids = ArrayUtils.toPrimitive(lucene_docids.toArray(new Integer[length]));
    scoredDocs.docids = docids.toArray(new String[0]);
    scoredDocs.scores = ArrayUtils.toPrimitive(score.toArray(new Float[length]), Float.NaN);

    return scoredDocs;
  }
}
