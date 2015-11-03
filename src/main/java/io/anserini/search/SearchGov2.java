package io.anserini.search;

import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.rm3.Rm3Reranker;
import io.anserini.util.AnalyzerUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

public class SearchGov2 {
  private static final Logger LOG = LogManager.getLogger(SearchGov2.class);

  public static void main(String[] args) throws Exception {
    long curTime = System.nanoTime();
    SearchArgs searchArgs = new SearchArgs();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SearchGov2" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    Path topicsFile = Paths.get(searchArgs.topics);

    LOG.info("Reading index at " + searchArgs.index);
    Directory dir;
    if (searchArgs.inmem) {
      LOG.info("Using MMapDirectory with preload");
      dir = new MMapDirectory(Paths.get(searchArgs.index));
      ((MMapDirectory) dir).setPreload(true);
    } else {
      LOG.info("Using default FSDirectory");
      dir = FSDirectory.open(Paths.get(searchArgs.index));
    }

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    if (searchArgs.ql) {
      LOG.info("Using QL scoring model");
      searcher.setSimilarity(new LMDirichletSimilarity(2500.0f));
    } else if (searchArgs.rm3) {
      LOG.info("Using RM3 query expansion");
      searcher.setSimilarity(new BM25Similarity(0.9f, 0.4f));
    } else if (searchArgs.bm25) {
      LOG.info("Using BM25 scoring model");
      searcher.setSimilarity(new BM25Similarity(0.9f, 0.4f));
    } else {
      LOG.error("Error: Must specify scoring model!");
      System.exit(-1);
    }

    int maxResults = 1000;

    TrecTopicsReader qReader = new TrecTopicsReader();
    QualityQuery qqs[] = qReader.readQueries(Files.newBufferedReader(topicsFile, StandardCharsets.UTF_8));

    PrintStream out = new PrintStream(new FileOutputStream(new File(searchArgs.output)));
    LOG.info("Writing output to " + searchArgs.output);

    LOG.info("Initialized complete! (elapsed time = " + (System.nanoTime()-curTime)/1000000 + "ms)");
    long totalTime = 0;
    for (QualityQuery qq : qqs) {
      long curQueryTime = System.nanoTime();
      Query query = AnalyzerUtils.buildBagOfWordsQuery("body", new EnglishAnalyzer(), qq.getValue("title"));
      TopDocs rs = searcher.search(query, maxResults);

      RerankerContext context = new RerankerContext(searcher, query, qq.getValue("title"), null);
      RerankerCascade cascade = new RerankerCascade(context);

      if (searchArgs.rm3) {
        cascade.add(new Rm3Reranker(new EnglishAnalyzer(), "body"));
      } else {
        cascade.add(new IdentityReranker());
      }

      ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher));

      for (int i=0; i<docs.documents.length; i++) {
        String qid = qq.getQueryID();
        out.println(String.format("%s Q0 %s %d %f %s", qid,
            docs.documents[i].getField("docid").stringValue(), (i+1), docs.scores[i], "Lucene"));
      }
      long qtime = (System.nanoTime()-curQueryTime)/1000000;
      LOG.info("Query " + qq.getQueryID() + " (elapsed time = " + qtime + "ms)");
      totalTime += qtime;
    }

    LOG.info("All queries completed!");
    LOG.info("Total elapsed time = " + totalTime + "ms");
    LOG.info("Average query latency = " + (totalTime/qqs.length) + "ms");

    reader.close();
    dir.close();
    out.close();
  }
}
