/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.index;

import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;

import java.io.File;
import java.io.IOException;

public class IndexUtils {
  private static final Logger LOG = LogManager.getLogger(IndexUtils.class);

  public static final class Args {

    // required arguments

    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "Lucene index path")
    String index;

    @Option(name = "-s", forbids = {"-t","-di","-dn","-dt","-dv"}, handler=BooleanOptionHandler.class, usage = "Print statistics for the Repository")
    boolean stats;

    @Option(name = "-t", forbids = {"-s","-di","-dn","-dt","-dv"}, usage = "Print term info: stemmed, total counts, doc counts")
    String term;

    @Option(name = "-dv", forbids = {"-s","-t","-di","-dt","-dn"}, handler = IntOptionHandler.class, usage = "Print the document vector of a document")
    int dv;

    @Option(name = "-dumpRawDoc", usage = "dumps raw document (if stored in the index)")
    int rawDoc;

    @Option(name = "-dumpTransformedDoc", usage = "dumps transformed document (if stored in the index)")
    int transformedDoc;

    @Option(name = "-convertDocidToLuceneDocid", usage = "converts a collection docid to a Lucene internal docid")
    String docid;

    @Option(name = "-convertLuceneDocidToDocid", usage = "converts to a Lucene internal docid to a collection docid ")
    int ldocid;
  }

  class NotStoredException extends Exception {
    public NotStoredException(String message) {
      super(message);
    }
  }

  final FSDirectory directory;
  final DirectoryReader reader;

  public IndexUtils(String indexPath) throws IOException {
    this.directory = FSDirectory.open(new File(indexPath).toPath());
    this.reader = DirectoryReader.open(directory);
  }

  void printIndexStats() throws IOException {
    int docCount = reader.numDocs();
    int contentsCount = reader.getDocCount(LuceneDocumentGenerator.FIELD_BODY);
    long termCount = reader.getSumTotalTermFreq(LuceneDocumentGenerator.FIELD_BODY);
    Fields fds = MultiFields.getFields(reader);
    Terms terms = fds.terms(LuceneDocumentGenerator.FIELD_BODY);

    StringBuilder sb = new StringBuilder();
    sb.append("Repository statistics:\n");
    sb.append("documents:\t" + docCount + "\n");
    sb.append("contentsCount(doc with contents):\t" + contentsCount + "\n");
    sb.append("unique terms:\t" + terms.size() + "\n");
    sb.append("total terms:\t" + termCount + "\n");
    sb.append("stored fields:\t\t");

    FieldInfos fis = MultiFields.getMergedFieldInfos(reader);
    for (String fd : fds) {
      FieldInfo fi = fis.fieldInfo(fd);
      sb.append("\n\t" + fd)
          .append(" (")
          .append("indexOption: " + fi.getIndexOptions())
          .append(", hasVectors: " + fi.hasVectors())
          .append(", hasPayloads: " + fi.hasPayloads())
          .append(")");
    }

    System.out.println(sb);
  }

  String printTermCounts(String termStr )
          throws IOException, ParseException {
    StringBuilder sb = new StringBuilder();
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);
    TermQuery q = (TermQuery)qp.parse(termStr);
    Term t = q.getTerm();
    long termFreq = reader.totalTermFreq(t);
    long docCount = reader.docFreq(t);
    sb.append("raw: "+termStr+"\n")
      .append("stemmed: "+q.toString(LuceneDocumentGenerator.FIELD_BODY)+"\n")
      .append("total Frequency: "+termFreq+"\n")
      .append("doc Count: "+docCount+"\n");

    PostingsEnum postingsEnum = MultiFields.getTermDocsEnum(reader, LuceneDocumentGenerator.FIELD_BODY, t.bytes());
    sb.append("posting:\n");
    while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
      sb.append(String.format("\t%s, %s\n", postingsEnum.docID(), postingsEnum.freq()));
    }
    return sb.toString();
  }

  String printDocumentVector(int number) throws IOException, NotStoredException {
    StringBuilder sb = new StringBuilder();
    Terms terms = reader.getTermVector(number, LuceneDocumentGenerator.FIELD_BODY);
    if (terms == null) {
      throw new NotStoredException("Doc Vector not Stored!");
    }
    TermsEnum te = terms.iterator();
    if (te == null) {
      throw new NotStoredException("Doc Vector not Stored!");
    }
    while((te.next()) != null){
      sb.append(te.term().utf8ToString()+" "+te.totalTermFreq()+"\n");
    }
    return sb.toString();
  }

  public String getRawDocument(int number) throws IOException, NotStoredException {
    Document d = reader.document(number);
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_RAW);
    if (doc == null) {
      throw new NotStoredException("Raw document not stored!");
    }
    return doc.stringValue();
  }

  public String getTransformedDocument(int number) throws IOException, NotStoredException {
    Document d = reader.document(number);
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_BODY);
    if (doc == null) {
      throw new NotStoredException("Raw document not stored!");
    }
    return doc.stringValue();
  }

  public int convertDocidToLuceneDocid(String docid) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q = new TermQuery(new Term(LuceneDocumentGenerator.FIELD_ID, docid));
    TopDocs rs = searcher.search(q, 1);
    ScoreDoc[] hits = rs.scoreDocs;

    if (hits == null) {
      throw new RuntimeException("Docid not found!");
    }

    return hits[0].doc;
  }

  public String convertLuceneDocidToDocid(int docid) throws IOException {
    Document d = reader.document(docid);
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_ID);
    if (doc == null) {
      // Really shouldn't happen!
      throw new RuntimeException();
    }
    return doc.stringValue();
  }

  public static void main(String[] argv) throws Exception{
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));
    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    final IndexUtils util = new IndexUtils(args.index);

    if (args.stats) {
      util.printIndexStats();
    }

    if (args.term != null) {
      util.printTermCounts(args.term);
    }

    if (args.dv > 0) {
      util.printDocumentVector(args.dv);
    }

    if (args.rawDoc > 0) {
      System.out.println(util.getRawDocument(args.rawDoc));
    }

    if (args.transformedDoc > 0) {
      System.out.println(util.getTransformedDocument(args.transformedDoc));
    }

    if (args.docid != null) {
      System.out.println(util.convertDocidToLuceneDocid(args.docid));
    }

    if (args.ldocid > 0) {
      System.out.println(util.convertLuceneDocidToDocid(args.ldocid));
    }
  }
}
