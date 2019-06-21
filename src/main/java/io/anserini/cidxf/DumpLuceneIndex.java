package io.anserini.cidxf;

import com.google.protobuf.CodedOutputStream;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.FileOutputStream;
import java.nio.file.Paths;

import static io.anserini.cidxf.CommonIndexFormat.Posting;
import static io.anserini.cidxf.CommonIndexFormat.PostingsList;
import static io.anserini.cidxf.CommonIndexFormat.PostingsListOrBuilder;

public class DumpLuceneIndex {
  public static void main(String[] argv) throws Exception {
    IndexReader reader = DirectoryReader.open(
        FSDirectory.open(Paths.get("lucene-index.robust04.pos+docvectors+rawdocs")));

    FileOutputStream fileOut = new FileOutputStream("postings.dat");
    //CodedOutputStream output = CodedOutputStream.newInstance(fileOut);

    int cnt = 0;
    // This is how you iterate through terms in the postings list.
    LeafReader leafReader = reader.leaves().get(0).reader();
    TermsEnum termsEnum = leafReader.terms("contents").iterator();
    BytesRef bytesRef = termsEnum.next();
    while (bytesRef != null) {
      // This is the current term in the dictionary.
      String token = bytesRef.utf8ToString();
      Term term = new Term("contents", token);
      //System.out.print(token + " (df = " + reader.docFreq(term) + "):");

      long df = reader.docFreq(term);
      long cf = reader.totalTermFreq(term);

      PostingsListOrBuilder plBuilder =
          PostingsList.newBuilder().setTerm(token).setDf(df).setCf(cf);

      int curDocid;
      int prevDocid = -1;
      PostingsEnum postingsEnum = leafReader.postings(term);
      while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        curDocid = postingsEnum.docID();
        // gap (i.e., delta) encoding.
        int code = prevDocid == -1 ? curDocid : curDocid - prevDocid;

        //System.out.print(String.format(" (%s, %s, %s)", postingsEnum.docID(), postingsEnum.freq(), code));
        ((PostingsList.Builder) plBuilder).addPosting(
            Posting.newBuilder().setDocid(code).setTf(postingsEnum.freq()).build());
        prevDocid = curDocid;
      }
      //System.out.println("");
      CommonIndexFormat.PostingsList pl = ((CommonIndexFormat.PostingsList.Builder) plBuilder).build();
      //System.out.println(pl.toString());
      pl.writeDelimitedTo(fileOut);

      bytesRef = termsEnum.next();

      cnt++;
      if ( cnt > Integer.MAX_VALUE) {
        break;
      }
      if (cnt % 1000 == 0) {
        System.out.println("Dumped " + cnt + " postings");
      }
    }

    fileOut.close();

  }
}
