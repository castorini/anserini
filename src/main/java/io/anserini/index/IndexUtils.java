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
import edu.stanford.nlp.simple.Sentence;
import org.jsoup.Jsoup;

import java.util.List;
import java.io.File;
import java.io.IOException;

public class IndexUtils {
  private static final Logger LOG = LogManager.getLogger(IndexUtils.class);

  public static final class Args {
    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    String index;

    @Option(name = "-stats", usage = "print index statistics")
    boolean stats;

    @Option(name = "-printTermInfo", metaVar = "term", usage = "prints term info (stemmed, total counts, doc counts, etc.)")
    String term;

    @Option(name = "-printDocvector", metaVar = "docid", usage = "prints the document vector of a document")
    String docvectorDocid;

    @Option(name = "-dumpRawDoc", metaVar = "docid", usage = "dumps raw document (if stored in the index)")
    String rawDoc;

    @Option(name = "-dumpTransformedDoc", metaVar = "docid", usage = "dumps transformed document (if stored in the index)")
    String transformedDoc;

    @Option(name = "-dumpSentences", metaVar = "docid", usage = "splits the fetched document into sentences (if stored in the index)")
    String sentDoc;

    @Option(name = "-convertDocidToLuceneDocid", metaVar = "docid", usage = "converts a collection lookupDocid to a Lucene internal lookupDocid")
    String lookupDocid;

    @Option(name = "-convertLuceneDocidToDocid", metaVar = "docid", usage = "converts to a Lucene internal lookupDocid to a collection lookupDocid ")
    int lookupLuceneDocid;
  }

  public class NotStoredException extends Exception {
    public NotStoredException(String message) {
      super(message);
    }
  }

  private final FSDirectory directory;
  private final DirectoryReader reader;

  public IndexUtils(String indexPath) throws IOException {
    this.directory = FSDirectory.open(new File(indexPath).toPath());
    this.reader = DirectoryReader.open(directory);
  }

  void printIndexStats() throws IOException {
    Fields fields = MultiFields.getFields(reader);
    Terms terms = fields.terms(LuceneDocumentGenerator.FIELD_BODY);

    System.out.println("Index statistics");
    System.out.println("----------------");
    System.out.println("documents:             " + reader.numDocs());
    System.out.println("documents (non-empty): " + reader.getDocCount(LuceneDocumentGenerator.FIELD_BODY));
    System.out.println("unique terms:          " + terms.size());
    System.out.println("total terms:           " + reader.getSumTotalTermFreq(LuceneDocumentGenerator.FIELD_BODY));

    System.out.println("stored fields:");

    FieldInfos fieldInfos = MultiFields.getMergedFieldInfos(reader);
    for (String fd : fields) {
      FieldInfo fi = fieldInfos.fieldInfo(fd);
      System.out.println("  " + fd + " (" + "indexOption: " + fi.getIndexOptions() +
          ", hasVectors: " + fi.hasVectors() + ")");
    }
  }

  public void printTermCounts(String termStr) throws IOException, ParseException {
    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);
    TermQuery q = (TermQuery)qp.parse(termStr);
    Term t = q.getTerm();

    System.out.println("raw term:             " + termStr);
    System.out.println("stemmed term:         " + q.toString(LuceneDocumentGenerator.FIELD_BODY));
    System.out.println("collection frequency: " + reader.totalTermFreq(t));
    System.out.println("document frequency:   " + reader.docFreq(t));

    PostingsEnum postingsEnum = MultiFields.getTermDocsEnum(reader, LuceneDocumentGenerator.FIELD_BODY, t.bytes());
    System.out.println("postings:\n");
    while (postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
      System.out.printf("\t%s, %s\n", postingsEnum.docID(), postingsEnum.freq());
    }
  }

  public void printDocumentVector(String docid) throws IOException, NotStoredException {
    Terms terms = reader.getTermVector(convertDocidToLuceneDocid(docid),
        LuceneDocumentGenerator.FIELD_BODY);
    if (terms == null) {
      throw new NotStoredException("Document vector not stored!");
    }
    TermsEnum te = terms.iterator();
    if (te == null) {
      throw new NotStoredException("Document vector not stored!");
    }
    while ((te.next()) != null) {
      System.out.println(te.term().utf8ToString() + " " + te.totalTermFreq());
    }
  }

  public String getRawDocument(String docid) throws IOException, NotStoredException {
    Document d = reader.document(convertDocidToLuceneDocid(docid));
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_RAW);
    if (doc == null) {
      throw new NotStoredException("Raw documents not stored!");
    }
    return doc.stringValue();
  }

  public String getTransformedDocument(String docid) throws IOException, NotStoredException {
    Document d = reader.document(convertDocidToLuceneDocid(docid));
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_BODY);
    if (doc == null) {
      throw new NotStoredException("Transformed documents not stored!");
    }
    return doc.stringValue();
  }

  public List<Sentence> getSentDocument(String docid) throws IOException, NotStoredException {
    String toSplit;
    try {
      toSplit = getTransformedDocument(docid);
    } catch (NotStoredException e) {
      String rawDoc = getRawDocument(docid);
      org.jsoup.nodes.Document jDoc = Jsoup.parse(rawDoc);
      toSplit = jDoc.text();
    }
    edu.stanford.nlp.simple.Document doc = new edu.stanford.nlp.simple.Document(toSplit);
    return doc.sentences();
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

    if (args.docvectorDocid != null) {
      util.printDocumentVector(args.docvectorDocid);
    }

    if (args.rawDoc != null) {
      System.out.println(util.getRawDocument(args.rawDoc));
    }

    if (args.transformedDoc != null) {
      System.out.println(util.getTransformedDocument(args.transformedDoc));
    }

    if (args.sentDoc != null) {
      for (Sentence sent: util.getSentDocument(args.sentDoc)){
        System.out.println(sent);
      }
    }

    if (args.lookupDocid != null) {
      System.out.println(util.convertDocidToLuceneDocid(args.lookupDocid));
    }

    if (args.lookupLuceneDocid > 0) {
      System.out.println(util.convertLuceneDocidToDocid(args.lookupLuceneDocid));
    }
  }
}
