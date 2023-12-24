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
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleGeoSearcher extends SimpleSearcher implements Closeable {
  private IndexReader reader;
  private IndexSearcher searcher;

  public ScoredDoc[] searchGeo(Query query, int k) throws IOException {
    return searchGeo(query, k, null);
  }

  public ScoredDoc[] searchGeo(Query query, int k, Sort sort) throws IOException {
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
    }

    TopDocs rs;
    if (sort == null) {
      rs = searcher.search(query, k);
    } else {
      rs = searcher.search(query, k, sort);
    }
    ScoredDocs hits = ScoredDocs.fromTopDocs(rs, searcher);
    ScoredDoc[] results = new ScoredDoc[hits.lucene_docids.length];

    for (int i = 0; i < hits.lucene_docids.length; i++) {
      Document doc = hits.lucene_documents[i];
      String docid = doc.getField(Constants.ID).stringValue();

      results[i] = new ScoredDoc(docid, hits.lucene_docids[i], hits.scores[i], doc);
    }

    return results;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public SimpleGeoSearcher(String indexDir) throws IOException {
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    reader = DirectoryReader.open(FSDirectory.open(indexPath));
    searcher = new IndexSearcher(reader);
  }
}
