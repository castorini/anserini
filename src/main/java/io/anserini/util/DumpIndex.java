package io.anserini.util;

import io.anserini.index.IndexWebCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DumpIndex {
  private static final Logger LOG = LogManager.getLogger(DumpIndex.class);

  class RawDocNotStoredException extends Exception {
    public RawDocNotStoredException(String message) {
      super(message);
    }
  }
  class DocVectorNotStoredException extends Exception {
    public DocVectorNotStoredException(String message) {
      super(message);
    }
  }

  String print_repository_stats( DirectoryReader reader ) throws IOException {
    int docCount = reader.numDocs();
    int contentsCount = reader.getDocCount(IndexWebCollection.FIELD_BODY);
    long termCount = reader.getSumTotalTermFreq(IndexWebCollection.FIELD_BODY);

    StringBuilder sb = new StringBuilder();
    sb.append("Repository statistics:\n");
    sb.append("documents:\t" + docCount + "\n");
    sb.append("contentsCount(doc with contents):\t" + contentsCount + "\n");
    //sb.append("unique terms:\t" + uniqueTermCount);
    sb.append("total terms:\t" + termCount + "\n");
    sb.append("stored fields:\t\t");

    Document d = reader.document(1);
    List<IndexableField> fields = d.getFields();
    for (IndexableField f : fields) {
      sb.append(f.name()+" ");
    }
    sb.append("\n");
    return sb.toString();
  }

  String print_term_counts( DirectoryReader reader, String termStr )
          throws IOException, ParseException {
    StringBuilder sb = new StringBuilder();
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(IndexWebCollection.FIELD_BODY, ea);
    TermQuery q = (TermQuery)qp.parse(termStr);
    long termFreq = reader.totalTermFreq(q.getTerm());
    long docCount = reader.docFreq(q.getTerm());
    sb.append(termStr+" ")
      .append(q.toString(IndexWebCollection.FIELD_BODY)+" ")
      .append(termFreq+" ")
      .append(docCount+" ")
      .append("\n");
    return sb.toString();
  }

  /*
  * print the internal id
  */
  String print_document_id( DirectoryReader reader, String externalId ) throws IOException {
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < reader.maxDoc()+1; i++) {
      Document d = reader.document(i);
      IndexableField id = d.getField(IndexWebCollection.FIELD_ID);
      if (externalId.equals(id.stringValue())) {
        sb.append(i);
        break;
      }
    }
    sb.append("\n");
    return sb.toString();
  }

  String print_document_name( DirectoryReader reader, int number ) throws IOException {
    StringBuilder sb = new StringBuilder();
    Document d = reader.document(number);
    IndexableField id = d.getField(IndexWebCollection.FIELD_ID);
    sb.append(id.stringValue())
      .append("\n");
    return sb.toString();
  }

  String print_document_text( DirectoryReader reader, int number ) throws IOException, RawDocNotStoredException {
    StringBuilder sb = new StringBuilder();
    Document d = reader.document(number);
    IndexableField id = d.getField(IndexWebCollection.FIELD_BODY);
    if (id == null) {
      throw new RawDocNotStoredException("Raw Contents not Stored!");
    }
    sb.append(id.stringValue())
      .append("\n");
    return sb.toString();
  }

  String print_document_vector( DirectoryReader reader, int number ) throws IOException, DocVectorNotStoredException {
    StringBuilder sb = new StringBuilder();
    Terms terms = reader.getTermVector(number, IndexWebCollection.FIELD_BODY);
    if (terms == null) {
      throw new DocVectorNotStoredException("Doc Vector not Stored!");
    }
    TermsEnum te = terms.iterator();
    if (te == null) {
      throw new DocVectorNotStoredException("Doc Vector not Stored!");
    }
    while((te.next()) != null){
      sb.append(te.term().utf8ToString()+" "+te.totalTermFreq()+"\n");
    }
    return sb.toString();
  }

  boolean argsEnough(int n, int required) {
    if (n < required) {
      printUsage();
      return false;
    }
    return true;
  }

  void printUsage() {
    StringBuilder sb = new StringBuilder();
    sb.append("DumpIndex <repository> <command> [ <argument> ]*\n")
      .append("These commands retrieve data from the repository: \n")
      .append("    Command              Argument       Description\n")
      .append("    term (t)             Term text      Print inverted list for a term\n")
      .append("    dxcount (dx)         Expression     Print document count of occurrences of an Indri expression\n")
      .append("    documentid (di)      Field, Value   Print the document IDs of documents having a metadata field matching this value\n")
      .append("    documentname (dn)    Document ID    Print the text representation of a document ID\n")
      .append("    documenttext (dt)    Document ID    Print the text of a document\n")
      .append("    documentvector (dv)  Document ID    Print the document vector of a document\n")
      .append("    stats (s)                           Print statistics for the Repository\n");
    System.out.println(sb.toString());
  }

  public static void main(String[] clArgs) throws IOException{
    int argc = clArgs.length;
    final DumpIndex ic = new DumpIndex();
    try {
      String indexPath = clArgs[0];
      String command = clArgs[1];

      FSDirectory dir = FSDirectory.open(new File(indexPath).toPath());
      DirectoryReader reader = DirectoryReader.open(dir);

      String dump = null;
      try {
        if (command.equals("s") || command.equals("stats")) {
          ic.argsEnough(2, argc);
          dump = ic.print_repository_stats(reader);
        } else if (command.equals("t") || command.equals("term")) {
          ic.argsEnough(3, argc);
          String termString = clArgs[2];
          dump = ic.print_term_counts(reader, termString);
        } else if (command.equals("di") || command.equals("documentid")) {
          ic.argsEnough(3, argc);
          String externalId = clArgs[2];
          dump = ic.print_document_id(reader, externalId);
        } else if (command.equals("dn") || command.equals("documentname")) {
          ic.argsEnough(3, argc);
          int number = Integer.parseInt(clArgs[2]);
          dump = ic.print_document_name(reader, number);
        } else if (command.equals("dt") || command.equals("documenttext")) {
          ic.argsEnough(3, argc);
          int number = Integer.parseInt(clArgs[2]);
          dump = ic.print_document_text(reader, number);
        } else if (command.equals("dv") || command.equals("documentvector")) {
          ic.argsEnough(3, argc);
          int number = Integer.parseInt(clArgs[2]);
          dump = ic.print_document_vector(reader, number);
        }
        System.out.println(dump);
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } catch (IndexOutOfBoundsException e) {
      ic.printUsage();
    }
  }
}
