/**
 * Anserini: A Lucene toolkit for replicable information retrieval research
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.anserini.analysis.vectors;

import com.google.common.collect.Sets;
import io.anserini.analysis.vectors.fw.FakeWordsEncoderAnalyzer;
import io.anserini.analysis.vectors.lexlsh.LexicalLshAnalyzer;
import io.anserini.search.topicreader.TrecTopicReader;
import io.anserini.util.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class ApproximateNearestNeighborEval {

  private static final String FW = "fw";
  private static final String LEXLSH = "lexlsh";

  public static final class Args {
    @Option(name = "-input", metaVar = "[file]", required = true, usage = "vectors model")
    public File input;

    @Option(name = "-path", metaVar = "[path]", required = true, usage = "index path")
    public Path path;

    @Option(name = "-topics", metaVar = "[file]", required = true, usage = "path to TREC topics file")
    public Path topicsPath;

    @Option(name = "-topN", metaVar = "[int]", usage = "topN recall")
    public int topN = 10;

    @Option(name = "-encoding", metaVar = "[word]", required = true, usage = "encoding must be one of {fw, lexlsh}")
    public String encoding;

    @Option(name = "-depth", metaVar = "[int]", usage = "retrieval depth")
    public int depth = 10;

    @Option(name = "-samples", metaVar = "[int]", usage = "no. of samples")
    public int samples = 100;

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
    public int q = 20;

    @Option(name = "-cutoff", metaVar = "[float]", usage = "tf cutoff factor")
    public float cutoff = 0.15f;

    @Option(name = "-msm", metaVar = "[int]", usage = "minimum should match")
    public int msm = 0;
  }

  public static void main(String[] args) throws Exception {

    ApproximateNearestNeighborEval.Args indexArgs = new ApproximateNearestNeighborEval.Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: " + ApproximateNearestNeighborEval.class.getSimpleName() +
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
      System.err.println("Example: " + ApproximateNearestNeighborEval.class.getSimpleName() +
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

    StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
    double recall = 0;
    double time = 0d;
    System.out.println("evaluating at retrieval depth: " + indexArgs.depth);
    TrecTopicReader trecTopicReader = new TrecTopicReader(indexArgs.topicsPath);
    SortedMap<Integer, Map<String, String>> read = trecTopicReader.read();
    int queryCount = 0;
    Collection<Map<String, String>> values = read.values();
    for (Map<String, String> topic : values) {
      for (String word : AnalyzerUtils.tokenize(standardAnalyzer, topic.get("title"))) {
        Set<String> truth = nearestVector(wordVectors, word, indexArgs.topN);
        if (wordVectors.containsKey(word)) {
          try {
            float[] vector = wordVectors.get(word);
            StringBuilder sb = new StringBuilder();
            for (double fv : vector) {
              if (sb.length() > 0) {
                sb.append(' ');
              }
              sb.append(fv);
            }
            String fvString = sb.toString();

            CommonTermsQuery simQuery = new CommonTermsQuery(SHOULD, SHOULD, indexArgs.cutoff);
            if (indexArgs.msm > 0) {
              simQuery.setHighFreqMinimumNumberShouldMatch(indexArgs.msm);
              simQuery.setLowFreqMinimumNumberShouldMatch(indexArgs.msm);
            }
            for (String token : AnalyzerUtils.tokenize(vectorAnalyzer, fvString)) {
              simQuery.add(new Term(IndexVectors.FIELD_VECTOR, token));
            }

            long start = System.currentTimeMillis();

            TopDocs topDocs = searcher.search(simQuery, indexArgs.depth);

            long qt = System.currentTimeMillis() - start;
            time += qt;
            Set<String> observations = new HashSet<>();
            for (ScoreDoc sd : topDocs.scoreDocs) {
              Document document = reader.document(sd.doc);
              String wordValue = document.get(IndexVectors.FIELD_WORD);
              observations.add(wordValue);
            }
            double intersection = Sets.intersection(truth, observations).size();
            double localRecall = intersection / (double) truth.size();
            recall += localRecall;
            queryCount++;
          } catch (IOException e) {
            System.err.println("search for '" + word + "' failed " + e.getLocalizedMessage());
          }
        }
      }
    }
    recall /= queryCount;
    time /= queryCount;

    System.out.printf("R@%s: %s\n", indexArgs.depth, recall);
    System.out.printf("avg query time: %s ms\n", time);

    reader.close();
    d.close();

  }

  /**
   * calculate nearest topN words for a given input word
   *
   * @param vectors vectors, keyed by word
   * @param word    the input word
   * @param topN    the no. of similar word vectors to output
   * @return the {@code topN} similar words of the input word
   */
  private static Set<String> nearestVector(Map<String, float[]> vectors, String word, int topN) {
    Set<String> intermediate = new TreeSet<>();
    float[] input = vectors.get(word);
    String separateToken = "__";
    for (Map.Entry<String, float[]> entry : vectors.entrySet()) {
      float sim = 0;
      float[] value = entry.getValue();
      for (int i = 0; i < value.length; i++) {
        sim += value[i] * input[i];
      }
      // store the words, sorted by decreasing distance using natural order (in the $dist__$word format)
      intermediate.add((1 - sim) + separateToken + entry.getKey());
    }
    Set<String> result = new HashSet<>();
    int i = 0;
    for (String w : intermediate) {
      if (i == topN) {
        break;
      }
      // only add actual word String (not the distance) to the result collection
      result.add(w.substring(w.indexOf(separateToken) + 2));
      i++;
    }
    return result;
  }

}
