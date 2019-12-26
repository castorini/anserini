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

package io.anserini.ann;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.ann.fw.FakeWordsEncoderAnalyzer;
import io.anserini.ann.lexlsh.LexicalLshAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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

    @Option(name = "-lexlsh.n", metaVar = "[int]", usage = "n-grams")
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
    public float cutoff = 0.999f;

    @Option(name = "-msm", metaVar = "[float]", usage = "minimum should match")
    public float msm = 0f;
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
      vectorAnalyzer = new LexicalLshAnalyzer(indexArgs.decimals, indexArgs.ngrams, indexArgs.hashCount,
          indexArgs.bucketCount, indexArgs.hashSetSize);
    } else {
      parser.printUsage(System.err);
      System.err.println("Example: " + ApproximateNearestNeighborSearch.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    System.out.println(String.format("Loading model %s", indexArgs.input));

    Map<String, float[]> wordVectors = IndexVectors.readGloVe(indexArgs.input);

    Path indexDir = indexArgs.path;
    if (!Files.exists(indexDir)) {
      Files.createDirectories(indexDir);
    }

    System.out.println(String.format("Reading index at %s", indexArgs.path));

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
    if (indexArgs.msm > 0) {
      simQuery.setHighFreqMinimumNumberShouldMatch(indexArgs.msm);
      simQuery.setLowFreqMinimumNumberShouldMatch(indexArgs.msm);
    }

    long start = System.currentTimeMillis();
    TopScoreDocCollector results = TopScoreDocCollector.create(indexArgs.depth, Integer.MAX_VALUE);
    searcher.search(simQuery, results);
    long time = System.currentTimeMillis() - start;

    System.out.println(String.format("%d nearest neighbors of '%s':", indexArgs.depth, indexArgs.word));

    int rank = 1;
    for (ScoreDoc sd : results.topDocs().scoreDocs) {
      Document document = reader.document(sd.doc);
      String word = document.get(IndexVectors.FIELD_WORD);
      System.out.println(String.format("%d. %s (%.3f)", rank, word, sd.score));
      rank++;
    }
    System.out.println(String.format("Search time: %dms", time));

    reader.close();
    d.close();
  }
}
