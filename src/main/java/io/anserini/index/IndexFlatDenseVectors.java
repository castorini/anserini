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
import org.apache.lucene.codecs.lucene102.Lucene102BinaryQuantizedVectorsFormat;
import org.apache.lucene.codecs.lucene103.Lucene103Codec;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.collection.ParquetDenseVectorCollection;
import io.anserini.collection.SourceDocument;
import io.anserini.index.codecs.Anserini20FlatScalarQuantizedVectorsFormat;
import io.anserini.index.codecs.Anserini20FlatVectorsFormat;
import io.anserini.index.codecs.DelegatingKnnVectorsFormat;
import io.anserini.index.generator.DenseVectorDocumentGenerator;
import io.anserini.index.generator.LuceneDocumentGenerator;

public final class IndexFlatDenseVectors extends AbstractIndexer {
  private static final Logger LOG = LogManager.getLogger(IndexFlatDenseVectors.class);

  public static final class Args extends AbstractIndexer.Args {
    @Option(name = "-generator", metaVar = "[class]", usage = "Document generator class in io.anserini.index.generator.")
    public String generatorClass = DenseVectorDocumentGenerator.class.getSimpleName();

    @Option(name = "-quantize.sqv", usage = "Quantize vectors using ScalarQuantizedVectors (mutually exclusive with -quantize.bqv).", forbids = "-quantize.bqv")
    public boolean quantizeSQV = false;

    @Option(name = "-quantize.bqv", usage = "Quantize vectors using BinaryQuantizedVectors (mutually exclusive with -quantize.sqv).", forbids = "-quantize.sqv")
    public boolean quantizeBQV = false;

    @Option(name = "-docidField", metaVar = "[name]", usage = "Name of the document ID field in Parquet files.")
    public String docidField = "docid";

    @Option(name = "-vectorField", metaVar = "[name]", usage = "Name of the vector field in Parquet files.")
    public String vectorField = "vector";

    @Option(name = "-normalizeVectors", usage = "Normalize vectors to unit length.")
    public boolean normalizeVectors = false;
  }

  @SuppressWarnings("unchecked")
  public IndexFlatDenseVectors(Args args) {
    super(args);

    try {
      super.generatorClass = (Class<LuceneDocumentGenerator<? extends SourceDocument>>) Class.forName("io.anserini.index.generator." + args.generatorClass);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to load generator class \"%s\".", args.generatorClass));
    }

    if (collection instanceof ParquetDenseVectorCollection) {
      ((ParquetDenseVectorCollection) collection).withDocidField(args.docidField).withVectorField(args.vectorField).withNormalizeVectors(args.normalizeVectors);
    }

    try {
      final Directory dir = FSDirectory.open(Paths.get(args.index));
      final IndexWriterConfig config;

      if (args.quantizeSQV) {
        config = new IndexWriterConfig().setCodec(
            new Lucene103Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new Anserini20FlatScalarQuantizedVectorsFormat();
              }
            });
      } else if (args.quantizeBQV) {
        config = new IndexWriterConfig().setCodec(
            new Lucene103Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new DelegatingKnnVectorsFormat(new Lucene102BinaryQuantizedVectorsFormat(), 4096);
              }
            });
      } else {
        config = new IndexWriterConfig().setCodec(
            new Lucene103Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new Anserini20FlatVectorsFormat();
              }
            });
      }

      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      config.setRAMBufferSizeMB(args.memoryBuffer);
      config.setUseCompoundFile(false);
      config.setMergeScheduler(new ConcurrentMergeScheduler());

      this.writer = new IndexWriter(dir, config);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to create IndexWriter: %s", e.getMessage()));
    }

    LOG.info("IndexFlatDenseVectors settings:");
    LOG.info(" + Generator: " + args.generatorClass);
    LOG.info(" + ScalarQuantizedVectors? " + args.quantizeSQV);
    LOG.info(" + BinaryQuantizedVectors? " + args.quantizeBQV);
    LOG.info(" + Document ID field: " + args.docidField);
    LOG.info(" + Vector field: " + args.vectorField);
    LOG.info(" + Normalize vectors? " + args.normalizeVectors);
  }

  public static void main(String[] args) throws Exception {
    Args indexArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (indexArgs.options) {
        System.err.printf("Options for %s:\n\n", IndexFlatDenseVectors.class.getSimpleName());
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

    new IndexFlatDenseVectors(indexArgs).run();
  }
}
