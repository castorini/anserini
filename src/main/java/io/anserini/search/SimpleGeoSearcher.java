package io.anserini.search;
import io.anserini.index.IndexArgs;
import io.anserini.rerank.ScoredDocuments;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LatLonShape;
import org.apache.lucene.document.ShapeField;
import org.apache.lucene.geo.LatLonGeometry;
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
      String docId = doc.getField(IndexArgs.ID).stringValue();

      IndexableField field;
      field = doc.getField(IndexArgs.CONTENTS);
      String contents = field == null ? null : field.stringValue();

      field = doc.getField(IndexArgs.RAW);
      String raw = field == null ? null : field.stringValue();

      System.out.println(rs.scoreDocs[i].score);

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
