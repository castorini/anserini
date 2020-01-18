package io.anserini.cidxf;

import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.FileOutputStream;
import java.nio.file.Paths;

import static io.anserini.cidxf.CommonIndexFormat.Posting;
import static io.anserini.cidxf.CommonIndexFormat.PostingsList;
import static io.anserini.cidxf.CommonIndexFormat.PostingsListOrBuilder;

public class DumpLuceneIndex {
  public static class Args {
    @Option(name = "-postingsOutput", metaVar = "[file]", required = true, usage = "postings output")
    public String postingsOutput = "";

    @Option(name = "-docidOutput", metaVar = "[file]", required = true, usage = "docid output")
    public String docidOutput = "";

    @Option(name = "-lengthsOutput", metaVar = "[file]", required = true, usage = "lengths output")
    public String lengthsOutput = "";

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    public String index = "";

    @Option(name = "-max", metaVar = "[int]", usage = "maximum number of postings to write")
    public int max = Integer.MAX_VALUE;
  }

  public static void main(String[] argv) throws Exception {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: Eval " + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(args.index)));

    System.out.println("Writing postings...");
    FileOutputStream fileOut = new FileOutputStream(args.postingsOutput);
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
      int postingsWritten = 0;
      PostingsEnum postingsEnum = leafReader.postings(term);
      while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        curDocid = postingsEnum.docID();
        // gap (i.e., delta) encoding.
        int code = prevDocid == -1 ? curDocid : curDocid - prevDocid;

        //System.out.print(String.format(" (%s, %s, %s)", postingsEnum.docID(), postingsEnum.freq(), code));
        ((PostingsList.Builder) plBuilder).addPosting(
            Posting.newBuilder().setDocid(code).setTf(postingsEnum.freq()).build());
        postingsWritten++;
        prevDocid = curDocid;
      }
      // The number of postings written should be the same as the df.
      if (postingsWritten != df) {
        throw new RuntimeException(String.format("Unexpected number of postings! expected %d got %d", df, postingsWritten));
      }

      CommonIndexFormat.PostingsList pl = ((CommonIndexFormat.PostingsList.Builder) plBuilder).build();
      pl.writeDelimitedTo(fileOut);

      bytesRef = termsEnum.next();

      cnt++;
      if ( cnt > args.max) {
        break;
      }
      if (cnt % 10000 == 0) {
        System.out.println("Wrote " + cnt + " postings...");
      }
    }
    System.out.println("Total of " + cnt + " postings written.");
    fileOut.close();

    System.out.println("Writing docids...");
    FileOutputStream docidOut = new FileOutputStream(args.docidOutput);
    for (int i=0; i<reader.maxDoc(); i++) {
      docidOut.write((reader.document(i).getField(LuceneDocumentGenerator.FIELD_ID).stringValue() +  "\n").getBytes());
    }
    docidOut.close();
    System.out.println("Done!");

    System.out.println("Writing doclengths...");
    FileOutputStream lengthsOut = new FileOutputStream(args.lengthsOutput);
    for (LeafReaderContext context : reader.leaves()) {
      leafReader = context.reader();
      NumericDocValues docValues = leafReader.getNormValues("contents");
      while (docValues.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        lengthsOut.write(((docValues.docID() + context.docBase) + "\t" + SmallFloat.byte4ToInt((byte) docValues.longValue()) + "\n").getBytes());
      }
    }
    lengthsOut.close();
    System.out.println("Done!");

    reader.close();
  }
}
