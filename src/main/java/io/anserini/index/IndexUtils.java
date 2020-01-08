/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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
import io.anserini.index.generator.TweetGenerator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.DocValuesFieldExistsQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;
import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_TWEETID;
import static java.util.stream.Collectors.joining;

public class IndexUtils {
  private static final Logger LOG = LogManager.getLogger(IndexUtils.class);

  public enum Compression { NONE, GZ, BZ2, ZIP }
  public enum DocumentVectorWeight {NONE, TF_IDF}

  public static final class Args {
    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    String index;

    @Option(name = "-stats", usage = "print index statistics")
    boolean stats;

    @Option(name = "-printTermInfo", metaVar = "term", usage = "prints term info (stemmed, total counts, doc counts, etc.)")
    String term;

    @Option(name = "-dumpDocVector", metaVar = "docid", usage = "prints the document vector of a document")
    String docvectorDocid;

    @Option(name = "-dumpDocVectors", metaVar = "[Path]", usage = "dumps the document vector for all documents from input file")
    String docVectors;

    @Option(name = "-docVectorWeight", metaVar = "[str]",
            usage = "the weight for dumped document vector(s), NONE or TF_IDF")
    DocumentVectorWeight docVectorWeight;

    @Option(name = "-dumpAllDocids", usage = "dumps all docids in sorted order. For non-tweet collection the order is " +
            "in ascending of String docid; For tweets collection the order is in descending of Long tweet id" +
            "please provide the compression scheme for the output")
    Compression dumpAllDocids;

    @Option(name = "-dumpRawDoc", metaVar = "docid", usage = "dumps raw document (if stored in the index)")
    String rawDoc;

    @Option(name = "-dumpRawDocs", metaVar = "[Path]", usage = "dumps raw documents from the input file")
    String rawDocs;

    @Option(name = "-dumpRawDocsWithDocid", metaVar = "[Path]", usage = "By default there is no <DOCNO>docid<DOCNO> " +
            "stored in the raw docs. By prepending <DOCNO>docid<DOCNO> in front of the raw docs we can directly index them")
    String rawDocsWithDocid;

    @Option(name = "-dumpTransformedDoc", metaVar = "docid", usage = "dumps transformed document (if stored in the index)")
    String transformedDoc;

    @Option(name = "-convertDocidToLuceneDocid", metaVar = "docid", usage = "converts a collection lookupDocid to a Lucene internal lookupDocid")
    String lookupDocid;

    @Option(name = "-convertLuceneDocidToDocid", metaVar = "docid", usage = "converts to a Lucene internal lookupDocid to a collection lookupDocid ")
    int lookupLuceneDocid;
  }

  private final FSDirectory directory;
  private final DirectoryReader reader;

  public IndexUtils(String indexPath) throws IOException {
    this.directory = FSDirectory.open(new File(indexPath).toPath());
    this.reader = DirectoryReader.open(directory);
  }

  public InputStream getReadFileStream(String path) throws IOException {
    InputStream fin = Files.newInputStream(Paths.get(path), StandardOpenOption.READ);
    BufferedInputStream in = new BufferedInputStream(fin);
    if (path.endsWith(".bz2")) {
      BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
      return bzIn;
    } else if (path.endsWith(".gz")) {
      GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
      return gzIn;
    } else if (path.endsWith(".zip")) {
      GzipCompressorInputStream zipIn = new GzipCompressorInputStream(in);
      return zipIn;
    }
    return in;
  }

  void printIndexStats() throws IOException {
    Terms terms = MultiTerms.getTerms(reader, LuceneDocumentGenerator.FIELD_BODY);

    System.out.println("Index statistics");
    System.out.println("----------------");
    System.out.println("documents:             " + reader.numDocs());
    System.out.println("documents (non-empty): " + reader.getDocCount(LuceneDocumentGenerator.FIELD_BODY));
    System.out.println("unique terms:          " + terms.size());
    System.out.println("total terms:           " + reader.getSumTotalTermFreq(LuceneDocumentGenerator.FIELD_BODY));

    System.out.println("stored fields:");

    FieldInfos fieldInfos = FieldInfos.getMergedFieldInfos(reader);
    for (FieldInfo fi : fieldInfos) {
      System.out.println("  " + fi.name + " (" + "indexOption: " + fi.getIndexOptions() +
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

    PostingsEnum postingsEnum = MultiTerms.getTermPostingsEnum(reader, LuceneDocumentGenerator.FIELD_BODY, t.bytes());
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

  public void dumpDocumentVectors(String reqDocidsPath, DocumentVectorWeight weight) throws IOException {
    String outFileName = weight == null ? reqDocidsPath+".docvector.tar.gz" : reqDocidsPath+".docvector." + weight +".tar.gz";
    LOG.info("Start dump document vectors with weight " + weight);

    InputStream in = getReadFileStream(reqDocidsPath);
    BufferedReader bRdr = new BufferedReader(new InputStreamReader(in));
    FileOutputStream fOut = new FileOutputStream(new File(outFileName));
    BufferedOutputStream bOut = new BufferedOutputStream(fOut);
    GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bOut);
    TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);

    Map<Term, Integer> docFreqMap = new HashMap<>();

    int numNonEmptyDocs = reader.getDocCount(LuceneDocumentGenerator.FIELD_BODY);

    String docid;
    int counter = 0;
    while ((docid = bRdr.readLine()) != null) {
      counter++;

      int internalDocid = convertDocidToLuceneDocid(docid);
      if (internalDocid == -1) {
        continue;
      }

      // get term frequency
      Terms terms = reader.getTermVector(internalDocid, LuceneDocumentGenerator.FIELD_BODY);
      if (terms == null) {
        // We do not throw exception here because there are some
        //  collections in which part of documents don't have document vectors
        LOG.warn("Document vector not stored for doc " + docid);
        continue;
      }

      TermsEnum te = terms.iterator();
      if (te == null) {
        LOG.warn("Document vector not stored for doc " + docid);
        continue;
      }

      Term term;
      long freq;

      // iterate every term and write and store in Map
      Map<String, String> docVectors = new HashMap<>();
      while ((te.next()) != null) {
        term = new Term(LuceneDocumentGenerator.FIELD_BODY, te.term());
        freq = te.totalTermFreq();

        switch (weight) {
          case NONE:
            docVectors.put(term.bytes().utf8ToString(), String.valueOf(freq));
            break;

          case TF_IDF:
            int docFreq;
            if (docFreqMap.containsKey(term)) {
              docFreq = docFreqMap.get(term);
            } else {
              try {
                docFreq = reader.docFreq(term);
              } catch (Exception e) {
                LOG.error("Cannot find term " + term.toString() + " in indexing file.");
                continue;
              }
              docFreqMap.put(term, docFreq);
            }
            float tfIdf = (float) (freq * Math.log(numNonEmptyDocs * 1.0 / docFreq));
            docVectors.put(term.bytes().utf8ToString(), String.format("%.6f", tfIdf));
            break;
        }
      }

      // Count size and write
      byte[] bytesOut = docVectors.entrySet()
              .stream()
              .map(e -> e.getKey()+" "+e.getValue())
              .collect(joining("\n"))
              .getBytes(StandardCharsets.UTF_8);

      TarArchiveEntry tarEntry = new TarArchiveEntry(new File(docid));
      tarEntry.setSize(bytesOut.length + String.format("<DOCNO>%s</DOCNO>\n", docid).length());
      tOut.putArchiveEntry(tarEntry);
      tOut.write(String.format("<DOCNO>%s</DOCNO>\n", docid).getBytes());
      tOut.write(bytesOut);
      tOut.closeArchiveEntry();

      if (counter % 100000 == 0) {
        LOG.info(counter + " files have been dumped.");
      }
    }
    tOut.close();
    LOG.info("Document Vectors are output to: " + outFileName);
  }

  public void getAllDocids(Compression compression) throws IOException {
    Query q = new DocValuesFieldExistsQuery(LuceneDocumentGenerator.FIELD_ID);
    IndexSearcher searcher = new IndexSearcher(reader);
    ScoreDoc[] scoreDocs;
    try {
      scoreDocs = searcher.search(new DocValuesFieldExistsQuery(LuceneDocumentGenerator.FIELD_ID), reader.maxDoc(),
          BREAK_SCORE_TIES_BY_DOCID).scoreDocs;
    } catch (IllegalStateException e) { // because this is tweets collection
      scoreDocs = searcher.search(new DocValuesFieldExistsQuery(TweetGenerator.StatusField.ID_LONG.name), reader.maxDoc(),
          BREAK_SCORE_TIES_BY_TWEETID).scoreDocs;
    }

    String basePath = directory.getDirectory().getFileName().toString() + ".allDocids";
    OutputStream outStream = null;
    String outputPath = "";
    switch (compression) {
      case NONE:
        outputPath = basePath+".txt";
        outStream = Files.newOutputStream(Paths.get(outputPath));
        break;
      case GZ:
        outputPath = basePath+".gz";
        outStream = new GzipCompressorOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(outputPath))));
        break;
      case ZIP:
        outputPath = basePath+".zip";
        outStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(outputPath))));
        ((ZipOutputStream) outStream).putNextEntry(new ZipEntry(basePath));
        break;
      case BZ2:
        outputPath = basePath+".bz2";
        outStream = new BZip2CompressorOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(outputPath))));
        break;
    }
    for (int i = 0; i < scoreDocs.length; i++) {
      StringBuilder builder = new StringBuilder();
      builder.append(searcher.doc(scoreDocs[i].doc).getField(LuceneDocumentGenerator.FIELD_ID).stringValue()).append("\n");
      outStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
    }
    outStream.close();
    System.out.println(String.format("All Documents IDs are output to: %s", outputPath));
  }

  public String getRawDocument(String docid) throws IOException, NotStoredException {
    Document d = reader.document(convertDocidToLuceneDocid(docid));
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_RAW);
    if (doc == null) {
      throw new NotStoredException("Raw documents not stored!");
    }
    return doc.stringValue();
  }

  public void dumpRawDocuments(String reqDocidsPath, boolean prependDocid) throws IOException, NotStoredException {
    LOG.info("Start dump raw documents" + (prependDocid ? " with Docid prepended" : "."));

    InputStream in = getReadFileStream(reqDocidsPath);
    BufferedReader bRdr = new BufferedReader(new InputStreamReader(in));
    
    String outputDir = Paths.get(reqDocidsPath).getFileName()+"_rawdocs.dump";
    boolean mkdirStatus = new File(outputDir).mkdirs();
    if (!mkdirStatus) {
      LOG.info("Create dump directory failed: "+ outputDir);
      return;
    }
    
    final class DumpThread extends Thread {
      final private IndexReader reader;
      final private String docid;
      final private String outputDir;
      final private boolean prependDocid;
      
      public DumpThread(IndexReader reader, String docid, String outputDir, boolean prependDocid) throws IOException {
        this.reader = reader;
        this.docid = docid;
        this.outputDir = outputDir;
        this.prependDocid = prependDocid;
      }
      
      @Override
      public void run() {
        try {
          Document d = reader.document(convertDocidToLuceneDocid(docid));
          IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_RAW);
          if (doc == null) {
            LOG.error("Raw documents not stored: " + docid);
          }
          FileUtils.writeStringToFile(
              Paths.get(outputDir, docid).toFile(),
              prependDocid ? String.format("<DOCNO>%s</DOCNO>\n", docid)+doc.stringValue() : doc.stringValue(),
              StandardCharsets.UTF_8);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    List<String> docids = new ArrayList<>();
    String docid;
    while ((docid = bRdr.readLine()) != null) {
      docids.add(docid);
    }
    int cores = Runtime.getRuntime().availableProcessors();
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.max(cores/2, 1));
    LOG.info(String.format("Using %d threads to dump raw documents", Math.max(cores/2, 1)));
    for (int i = 0; i < docids.size(); i++) {
      executor.execute(new DumpThread(reader, docids.get(i), outputDir, prependDocid));
    }
  
    executor.shutdown();
  
    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        LOG.info(String.format("%.2f percent completed",
            (double) executor.getCompletedTaskCount() / docids.size() * 100.0d));
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    LOG.info(String.format("Raw documents are output to: %s", outputDir));
  }

  public String getTransformedDocument(String docid) throws IOException, NotStoredException {
    Document d = reader.document(convertDocidToLuceneDocid(docid));
    IndexableField doc = d.getField(LuceneDocumentGenerator.FIELD_BODY);
    if (doc == null) {
      throw new NotStoredException("Transformed documents not stored!");
    }
    return doc.stringValue();
  }

  public int convertDocidToLuceneDocid(String docid) throws IOException {
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

    if (args.docVectors != null) {
      if (args.docVectorWeight == null) {
        args.docVectorWeight = DocumentVectorWeight.NONE;
      }
      util.dumpDocumentVectors(args.docVectors, args.docVectorWeight);
    }

    if (args.dumpAllDocids != null) {
      util.getAllDocids(args.dumpAllDocids);
    }

    if (args.rawDoc != null) {
      System.out.println(util.getRawDocument(args.rawDoc));
    }

    if (args.rawDocs != null) {
      util.dumpRawDocuments(args.rawDocs, false);
    }

    if (args.rawDocsWithDocid != null) {
      util.dumpRawDocuments(args.rawDocs, true);
    }

    if (args.transformedDoc != null) {
      System.out.println(util.getTransformedDocument(args.transformedDoc));
    }

    if (args.lookupDocid != null) {
      System.out.println(util.convertDocidToLuceneDocid(args.lookupDocid));
    }

    if (args.lookupLuceneDocid > 0) {
      System.out.println(util.convertLuceneDocidToDocid(args.lookupLuceneDocid));
    }
  }
}
