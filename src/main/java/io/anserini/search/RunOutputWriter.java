package io.anserini.search;

import io.anserini.collection.JsonCollection;
import io.anserini.index.Constants;
import io.anserini.rerank.ScoredDocuments;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RunOutputWriter {
  public static <K> Result[] generateRunOutput(IndexSearcher searcher, TopDocs docs,
                                               K qid,
                                               boolean removedups,
                                               boolean removeQuery,
                                               boolean selectMaxPassage,
                                               String selectMaxPassage_delimiter,
                                               int selectMaxPassage_hits) throws IOException  {
    List<Result> results = new ArrayList<>();
    // For removing duplicate docids.
    Set<String> docids = new HashSet<>();

    int rank = 1;
    for (int i = 0; i < docs.scoreDocs.length; i++) {
      int lucene_docid = docs.scoreDocs[i].doc;
      Document lucene_document = searcher.storedFields().document(docs.scoreDocs[i].doc);
      String docid = lucene_document.get(Constants.ID);

      if (selectMaxPassage) {
        docid = docid.split(selectMaxPassage_delimiter)[0];
      }

      if (docids.contains(docid))
        continue;

      // Remove docids that are identical to the query id if flag is set.
      if (removeQuery && docid.equals(qid))
        continue;

      results.add(new Result(docid, lucene_docid, docs.scoreDocs[i].score, lucene_document));

      // Note that this option is set to false by default because duplicate documents usually indicate some
      // underlying indexing issues, and we don't want to just eat errors silently.
      //
      // However, when we're performing passage retrieval, i.e., with "selectMaxPassage", we *do* want to remove
      // duplicates.
      if (removedups || selectMaxPassage) {
        docids.add(docid);
      }

      rank++;

      if (selectMaxPassage && rank > selectMaxPassage_hits) {
        break;
      }
    }

    return results.toArray(new Result[results.size()]);
  }
}
