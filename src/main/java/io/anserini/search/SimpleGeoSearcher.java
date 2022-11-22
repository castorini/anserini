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
import io.anserini.rerank.ScoredDocuments;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
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
  private IndexSearcher searcher = null;

  public Result[] searchGeo(Query query, int k) throws IOException {
    return searchGeo(query, k, null);
  }

  public Result[] searchGeo(Query query, int k, Sort sort) throws IOException {
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
    }

    TopDocs rs;
    if (sort == null) {
      rs = searcher.search(query, k);
    } else {
      rs = searcher.search(query, k, sort);
    }
    ScoredDocuments hits = ScoredDocuments.fromTopDocs(rs, searcher);
    Result[] results = new Result[hits.ids.length];

    for (int i = 0; i < hits.ids.length; i++) {
      Document doc = hits.documents[i];
      String docId = doc.getField(Constants.ID).stringValue();

      IndexableField field;
      field = doc.getField(Constants.CONTENTS);
      String contents = field == null ? null : field.stringValue();

      field = doc.getField(Constants.RAW);
      String raw = field == null ? null : field.stringValue();

      results[i] = new Result(docId, hits.ids[i], hits.scores[i], contents, raw, doc);
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
