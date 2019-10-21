package io.anserini.analysis.vectors;

import io.anserini.analysis.vectors.fw.FakeWordsEncoderAnalyzer;
import io.anserini.analysis.vectors.lexlsh.LexicalLocalitySensitiveHashingAnalyzer;
import io.anserini.util.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class ApproximateNearestNeighborSearch {

  private static final String FW = "fw";
  private static final String LEXLSH = "lexlsh";

  public static final class Args {
    @Option(name = "-input", metaVar = "[file]", required = true, usage = "word vectors model")
    public File input;

    @Option(name = "-path", metaVar = "[path]", required = true, usage = "index path")
    public Path path;

    @Option(name = "-word", metaVar = "[word]", required = true, usage = "input word")
    public String word;

    @Option(name = "-encoding", metaVar = "[word]", required = true, usage = "encoding must be one of {fw, lexlsh}")
    public String encoding;

    @Option(name = "-depth", metaVar = "[int]", usage = "retrieval depth")
    public int depth = 10;

    @Option(name = "-lexlsh.n", metaVar = "[int]", usage = "ngrams")
    public int ngrams = 2;

    @Option(name = "-lexlsh.d", metaVar = "[int]", usage = "decimals")
    public int decimals = 1;

    @Option(name = "-lexlsh.hsize", metaVar = "[int]", usage = "hash set size")
    public int hashSetSize = 1;

    @Option(name = "-lexlsh.h", metaVar = "[int]", usage = "hash count")
    public int hashCount = 1;

    @Option(name = "-lexlsh.b", metaVar = "[int]", usage = "bucket count")
    public int bucketCount = 300;

    @Option(name = "-fw.q", metaVar = "[int]", usage = "quantization factor")
    public int q = 60;

    @Option(name = "-cutoff", metaVar = "[float]", usage = "tf cutoff factor")
    public float cutoff = 0.1f;
  }

  public static void main(String[] args) throws Exception {

    ApproximateNearestNeighborSearch.Args indexArgs = new ApproximateNearestNeighborSearch.Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: " + ApproximateNearestNeighborSearch.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }
    Analyzer vectorAnalyzer;
    if (indexArgs.encoding.equalsIgnoreCase(FW)) {
      vectorAnalyzer = new FakeWordsEncoderAnalyzer(indexArgs.q);
    } else if (indexArgs.encoding.equalsIgnoreCase(LEXLSH)) {
      vectorAnalyzer = new LexicalLocalitySensitiveHashingAnalyzer(indexArgs.decimals, indexArgs.ngrams, indexArgs.hashCount,
          indexArgs.bucketCount, indexArgs.hashSetSize);
    } else {
      parser.printUsage(System.err);
      System.err.println("Example: " + ApproximateNearestNeighborSearch.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    System.out.printf("loading model %s\n", indexArgs.input);

    Map<String, float[]> wordVectors = IndexVectors.readTxtModel(indexArgs.input);

    Path indexDir = indexArgs.path;
    if (!Files.exists(indexDir)) {
      Files.createDirectories(indexDir);
    }

    System.out.printf("reading index at %s\n", indexArgs.path);

    Directory d = FSDirectory.open(indexDir);
    DirectoryReader reader = DirectoryReader.open(d);
    IndexSearcher searcher = new IndexSearcher(reader);
    if (indexArgs.encoding.equalsIgnoreCase(FW)) {
      searcher.setSimilarity(new ClassicSimilarity());
    }

    float[] vector = wordVectors.get(indexArgs.word);
    StringBuilder sb = new StringBuilder();
    for (double fv : vector) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(fv);
    }
    CommonTermsQuery simQuery = new CommonTermsQuery(SHOULD, SHOULD, indexArgs.cutoff);
    for (String token : AnalyzerUtils.tokenize(vectorAnalyzer, sb.toString())) {
      simQuery.add(new Term(IndexVectors.FIELD_VECTOR, token));
    }
    long start = System.currentTimeMillis();

    TopDocs topDocs = searcher.search(simQuery, indexArgs.depth);

    long time = System.currentTimeMillis() - start;
    Set<String> observations = new HashSet<>();
    for (ScoreDoc sd : topDocs.scoreDocs) {
      Document document = reader.document(sd.doc);
      String wordValue = document.get(IndexVectors.FIELD_WORD);
      observations.add(wordValue);
    }

    System.out.printf("top %d neighbors of '%s' (%dms): %s\n", indexArgs.depth, indexArgs.word, time, observations.toString());

    reader.close();
    d.close();

  }

}
