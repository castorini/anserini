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

import io.anserini.ann.fw.FakeWordsEncoderAnalyzer;
import io.anserini.ann.lexlsh.LexicalLshAnalyzer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexVectors {
  static final String FIELD_WORD = "word";
  static final String FIELD_VECTOR = "vector";

  private static final String FW = "fw";
  private static final String LEXLSH = "lexlsh";

  public static final class Args {
    @Option(name = "-input", metaVar = "[file]", required = true, usage = "word vectors model")
    public File input;

    @Option(name = "-path", metaVar = "[path]", required = true, usage = "index path")
    public Path path;

    @Option(name = "-encoding", metaVar = "[word]", required = true, usage = "encoding must be one of {fw, lexlsh}")
    public String encoding;

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
  }

  public static void main(String[] args) throws Exception {
    IndexVectors.Args indexArgs = new IndexVectors.Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: " + IndexVectors.class.getSimpleName() +
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
      System.err.println("Example: " + IndexVectors.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    System.out.println(String.format("Loading model %s", indexArgs.input));

    Map<String, float[]> wordVectors = readGloVe(indexArgs.input);

    Path indexDir = indexArgs.path;
    if (!Files.exists(indexDir)) {
      Files.createDirectories(indexDir);
    }

    System.out.println(String.format("Creating index at %s...", indexArgs.path));

    Directory d = FSDirectory.open(indexDir);
    Map<String, Analyzer> map = new HashMap<>();
    map.put(FIELD_VECTOR, vectorAnalyzer);
    Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), map);

    IndexWriterConfig conf = new IndexWriterConfig(analyzer);
    IndexWriter indexWriter = new IndexWriter(d, conf);
    final AtomicInteger cnt = new AtomicInteger();

    for (Map.Entry<String, float[]> entry : wordVectors.entrySet()) {
      Document doc = new Document();

      doc.add(new StringField(FIELD_WORD, entry.getKey(), Field.Store.YES));
      float[] vector = entry.getValue();
      StringBuilder sb = new StringBuilder();
      for (double fv : vector) {
        if (sb.length() > 0) {
          sb.append(' ');
        }
        sb.append(fv);
      }
      doc.add(new TextField(FIELD_VECTOR, sb.toString(), Field.Store.NO));
      try {
        indexWriter.addDocument(doc);
        int cur = cnt.incrementAndGet();
        if (cur % 100000 == 0) {
          System.out.println(String.format("%s words added", cnt));
        }
      } catch (IOException e) {
        System.err.println("Error while indexing: " + e.getLocalizedMessage());
      }
    }

    indexWriter.commit();
    System.out.println(String.format("%s words indexed", cnt.get()));
    long space = FileUtils.sizeOfDirectory(indexDir.toFile()) / (1024L * 1024L);
    System.out.println(String.format("Index size: %dMB", space));
    indexWriter.close();
    d.close();

    final long durationMillis =
        TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    System.out.println(String.format("Total time: %s",
        DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss")));
  }

  static Map<String, float[]> readGloVe(File input) throws IOException {
    Map<String, float[]> vectors = new HashMap<>();
    for (String line : IOUtils.readLines(new FileReader(input))) {
      String[] s = line.split(" ");
      if (s.length > 2) {
        String key = s[0];
        float[] vector = new float[s.length - 1];
        float norm = 0f;
        for (int i = 1; i < s.length; i++) {
          float f = Float.parseFloat(s[i]);
          vector[i - 1] = f;
          norm += Math.pow(f, 2);
        }
        norm = (float) Math.sqrt(norm);
        for (int i = 0; i < vector.length; i++) {
          vector[i] = vector[i] / norm;
        }
        vectors.put(key, vector);
      }
    }
    return vectors;
  }
}
