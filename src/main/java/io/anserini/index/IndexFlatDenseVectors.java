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

import io.anserini.collection.SourceDocument;
import io.anserini.collection.ParquetDenseVectorCollection;
import io.anserini.index.codecs.AnseriniLucene99FlatVectorFormat;
import io.anserini.index.codecs.AnseriniLucene99ScalarQuantizedVectorsFormat;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.index.generator.DenseVectorDocumentGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.KnnVectorsReader;
import org.apache.lucene.codecs.KnnVectorsWriter;
import org.apache.lucene.codecs.lucene99.Lucene99Codec;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class IndexFlatDenseVectors extends AbstractIndexer {
  private static final Logger LOG = LogManager.getLogger(IndexFlatDenseVectors.class);

  public static final class Args extends AbstractIndexer.Args {
    @Option(name = "-generator", metaVar = "[class]", usage = "Document generator class in io.anserini.index.generator.")
    public String generatorClass = DenseVectorDocumentGenerator.class.getSimpleName();

    @Option(name = "-quantize.int8", usage = "Quantize vectors into int8.")
    public boolean quantizeInt8 = false;

    @Option(name = "-storeVectors", usage = "Boolean switch to store raw raw vectors.")
    public boolean storeVectors = false;

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

      if (args.quantizeInt8) {
        config = new IndexWriterConfig().setCodec(
            new Lucene99Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new DelegatingKnnVectorsFormat(new AnseriniLucene99ScalarQuantizedVectorsFormat(), 4096);
              }
            });
      } else {
        config = new IndexWriterConfig().setCodec(
            new Lucene99Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new DelegatingKnnVectorsFormat(new AnseriniLucene99FlatVectorFormat(), 4096);
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

    LOG.info("FlatDenseVector settings:");
    LOG.info(" + Generator: " + args.generatorClass);
    LOG.info(" + Store document vectors? " + args.storeVectors);
    LOG.info(" + Int8 quantization? " + args.quantizeInt8);
    LOG.info(" + Document ID field: " + args.docidField);
    LOG.info(" + Vector field: " + args.vectorField);
    LOG.info(" + Normalize vectors? " + args.normalizeVectors);
  }

  // Solution provided by Solr, see https://www.mail-archive.com/java-user@lucene.apache.org/msg52149.html
  // This class exists because Lucene95HnswVectorsFormat's getMaxDimensions method is final and we
  // need to workaround that constraint to allow more than the default number of dimensions.
  private static final class DelegatingKnnVectorsFormat extends KnnVectorsFormat {
    private final KnnVectorsFormat delegate;
    private final int maxDimensions;

    public DelegatingKnnVectorsFormat(KnnVectorsFormat delegate, int maxDimensions) {
      super(delegate.getName());
      this.delegate = delegate;
      this.maxDimensions = maxDimensions;
    }

    @Override
    public KnnVectorsWriter fieldsWriter(SegmentWriteState state) throws IOException {
      return delegate.fieldsWriter(state);
    }

    @Override
    public KnnVectorsReader fieldsReader(SegmentReadState state) throws IOException {
      return delegate.fieldsReader(state);
    }

    @Override
    public int getMaxDimensions(String fieldName) {
      return maxDimensions;
    }
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
