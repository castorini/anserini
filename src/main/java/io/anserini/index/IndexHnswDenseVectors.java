/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.lucene102.Lucene102HnswBinaryQuantizedVectorsFormat;
import org.apache.lucene.codecs.lucene103.Lucene103Codec;
import org.apache.lucene.codecs.lucene99.Lucene99HnswScalarQuantizedVectorsFormat;
import org.apache.lucene.codecs.lucene99.Lucene99HnswVectorsFormat;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.collection.SourceDocument;
import io.anserini.index.codecs.DelegatingKnnVectorsFormat;
import io.anserini.index.generator.DenseVectorDocumentGenerator;
import io.anserini.index.generator.LuceneDocumentGenerator;

public final class IndexHnswDenseVectors extends AbstractIndexer {
  private static final Logger LOG = LogManager.getLogger(IndexHnswDenseVectors.class);

  public static final class Args extends AbstractIndexer.Args {
    @Option(name = "-generator", metaVar = "[class]", usage = "Document generator class in io.anserini.index.generator.")
    public String generatorClass = DenseVectorDocumentGenerator.class.getSimpleName();

    @Option(name = "-M", metaVar = "[num]", usage = "HNSW parameters M.")
    public int M = 16;
  
    @Option(name = "-efC", metaVar = "[num]", usage = "HNSW parameters ef Construction.")
    public int efC = 500;

    @Option(name = "-quantize.sqv", usage = "Quantize vectors using ScalarQuantizedVectors (mutually exclusive with -quantize.bqv).", forbids = "-quantize.bqv")
    public boolean quantizeSQV = false;

    @Option(name = "-quantize.bqv", usage = "Quantize vectors using BinaryQuantizedVectors (mutually exclusive with -quantize.sqv).", forbids = "-quantize.sqv")
    public boolean quantizeBQV = false;
  }

  @SuppressWarnings("unchecked")
  public IndexHnswDenseVectors(Args args) throws Exception {
    super(args);

    try {
      super.generatorClass = (Class<LuceneDocumentGenerator<? extends SourceDocument>>)
          Class.forName("io.anserini.index.generator." + args.generatorClass);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to load generator class \"%s\".", args.generatorClass));
    }

    try {
      final Directory dir = FSDirectory.open(Paths.get(args.index));
      final IndexWriterConfig config;

      if (args.quantizeSQV) {
        config = new IndexWriterConfig().setCodec(
            new Lucene103Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new DelegatingKnnVectorsFormat(new Lucene99HnswScalarQuantizedVectorsFormat(args.M, args.efC), 4096);
              }
            });
      } else if (args.quantizeBQV) {
        config = new IndexWriterConfig().setCodec(
            new Lucene103Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new DelegatingKnnVectorsFormat(new Lucene102HnswBinaryQuantizedVectorsFormat(args.M, args.efC), 4096);
              }
            });
      } else {
        config = new IndexWriterConfig().setCodec(
            new Lucene103Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new DelegatingKnnVectorsFormat(new Lucene99HnswVectorsFormat(args.M, args.efC), 4096);
              }
            });
      }

      this.writer = new IndexWriter(dir, config);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to create IndexWriter: %s.", e.getMessage()));
    }

    LOG.info("IndexHnswDenseVectors settings:");
    LOG.info(" + Generator: " + args.generatorClass);
    LOG.info(" + M: " + args.M);
    LOG.info(" + efC: " + args.efC);
    LOG.info(" + ScalarQuantizedVectors? " + args.quantizeSQV);
    LOG.info(" + BinaryQuantizedVectors? " + args.quantizeBQV);
  }

  public static void main(String[] args) throws Exception {
    Args indexArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (indexArgs.options) {
        System.err.printf("Options for %s:\n\n", IndexHnswDenseVectors.class.getSimpleName());
        parser.printUsage(System.err);

        List<String> required = new ArrayList<>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });

        System.err.printf("\nRequired options are %s\n", required);
      } else {
        System.err.printf("Error: %s. For help, use \"-options\" to print out information about options.\n", e.getMessage());
      }

      return;
    }

    new IndexHnswDenseVectors(indexArgs).run();
  }
}
