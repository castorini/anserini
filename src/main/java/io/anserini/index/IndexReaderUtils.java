package io.anserini.index;

import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public class IndexReaderUtils {
  private static final Logger LOG = LogManager.getLogger(IndexUtils.class);

  public static int convertDocidToLuceneDocid(IndexReader reader, String docid) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q = new TermQuery(new Term(LuceneDocumentGenerator.FIELD_ID, docid));
    TopDocs rs = searcher.search(q, 1);
    ScoreDoc[] hits = rs.scoreDocs;

    if (hits == null || hits.length == 0) {
      LOG.warn(String.format("Docid %s not found!", docid));
      return -1;
    }

    return hits[0].doc;
  }

  public static String convertLuceneDocidToDocid(IndexReader reader, int docid) throws IOException {
    Document d = reader.document(docid);
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_ID);
    if (doc == null) {
      // Really shouldn't happen!
      throw new RuntimeException();
    }
    return doc.stringValue();
  }
}
