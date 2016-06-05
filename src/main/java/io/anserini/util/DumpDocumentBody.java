package io.anserini.util;

import io.anserini.index.IndexWebCollection;
import io.anserini.search.SearchArgs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.TokenStreamFromTermVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Will consume a document id, and then dump the document body both as the token stream, as well
 * as what is retrieved from the document field
 */
public class DumpDocumentBody {
  private static final Logger LOG = LogManager.getLogger(DumpDocumentBody.class);

  private static class ParseArgs extends SearchArgs {
    @Option(name = "-docId", required = true, usage = "DocId of the document to dump")
    public String docId;
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index")
    public String index;
  }

  public static void main(String[] args) throws CmdLineException, IOException {
    ParseArgs parseArgs = new ParseArgs();
    CmdLineParser parser = new CmdLineParser(parseArgs, ParserProperties.defaults().withUsageWidth(90));

    parser.parseArgument(args);

    Directory directory = FSDirectory.open(Paths.get(parseArgs.index));

    Query query = new TermQuery(new Term(IndexWebCollection.FIELD_ID, parseArgs.docId));

    IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));

    ScoreDoc[] docs = searcher.search(query, 1).scoreDocs;
    if(docs.length < 1) {
      LOG.error("Document could not be retieved, exiting");
      return;
    }
    ScoreDoc retrievedDoc = docs[0];
    Document document = searcher.doc(retrievedDoc.doc);
    System.out.println("Document body:");
    System.out.println(document.get(IndexWebCollection.FIELD_BODY));

    IndexReader reader = searcher.getIndexReader();
    Terms terms = reader.getTermVector(retrievedDoc.doc, IndexWebCollection.FIELD_BODY);

    TokenStream stream = new TokenStreamFromTermVector(terms, 0);
    CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);

    while (stream.incrementToken()) {
      System.out.print(termAttribute.toString());
      System.out.print(" ");
    }
    System.out.println("");
  }
}
