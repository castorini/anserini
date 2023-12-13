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
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.KnnVectorsReader;
import org.apache.lucene.codecs.KnnVectorsWriter;
import org.apache.lucene.codecs.lucene99.Lucene99Codec;
import org.apache.lucene.codecs.lucene99.Lucene99HnswScalarQuantizedVectorsFormat;
import org.apache.lucene.codecs.lucene99.Lucene99HnswVectorsFormat;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.NoMergePolicy;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.index.TieredMergePolicy;
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

public final class IndexHnswDenseVectors extends AbstractIndexer {
  private static final Logger LOG = LogManager.getLogger(IndexHnswDenseVectors.class);

  public static final class Args extends AbstractIndexer.Args {
    @Option(name = "-generator", metaVar = "[class]", usage = "Document generator class in io.anserini.index.generator.")
    public String generatorClass = "HnswDenseVectorDocumentGenerator";

    @Option(name = "-M", metaVar = "[num]", usage = "HNSW parameters M")
    public int M = 16;
  
    @Option(name = "-efC", metaVar = "[num]", usage = "HNSW parameters ef Construction")
    public int efC = 100;

    @Option(name = "-quantize.int8", usage = "Quantize vectors into int8.")
    public boolean quantizeInt8 = false;

    @Option(name = "-storeVectors", usage = "Boolean switch to store raw raw vectors.")
    public boolean storeVectors = false;

    @Option(name = "-noMerge", usage = "Do not merge segments (fast indexing, slow retrieval).")
    public boolean noMerge = false;

    @Option(name = "-maxMergedSegmentSize", metaVar = "[num]", usage = "Maximum sized segment to produce during normal merging (in MB).")
    public int maxMergedSegmentSize = 1024 * 16;

    @Option(name = "-segmentsPerTier", metaVar = "[num]", usage = "Allowed number of segments per tier.")
    public int segmentsPerTier = 10;

    @Option(name = "-maxMergeAtOnce", metaVar = "[num]", usage = "Maximum number of segments to be merged at a time during \"normal\" merging.")
    public int maxMergeAtOnce = 10;
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

      if (args.quantizeInt8) {
        config = new IndexWriterConfig().setCodec(
            new Lucene99Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new DelegatingKnnVectorsFormat(
                    new Lucene99HnswScalarQuantizedVectorsFormat(args.M, args.efC), 4096);
              }
            });
      } else {
        config = new IndexWriterConfig().setCodec(
            new Lucene99Codec() {
              @Override
              public KnnVectorsFormat getKnnVectorsFormatForField(String field) {
                return new DelegatingKnnVectorsFormat(
                    new Lucene99HnswVectorsFormat(args.M, args.efC), 4096);
              }
            });
      }

      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      config.setRAMBufferSizeMB(args.memoryBuffer);
      config.setRAMPerThreadHardLimitMB(2047); // Max possible value.
      config.setUseCompoundFile(false);
      config.setMergeScheduler(new ConcurrentMergeScheduler());

      if (args.noMerge) {
        config.setMergePolicy(NoMergePolicy.INSTANCE);
      } else {
        TieredMergePolicy mergePolicy = new TieredMergePolicy();
        if (args.optimize) {
          // If we're going to merge down into a single segment at the end, skip intermediate merges,
          // since they are a waste of time.
          mergePolicy.setMaxMergeAtOnce(256);
          mergePolicy.setSegmentsPerTier(256);
        } else {
          mergePolicy.setFloorSegmentMB(1024);
          mergePolicy.setMaxMergedSegmentMB(args.maxMergedSegmentSize);
          mergePolicy.setSegmentsPerTier(args.segmentsPerTier);
          mergePolicy.setMaxMergeAtOnce(args.maxMergeAtOnce);
        }
        config.setMergePolicy(mergePolicy);
      }

      this.writer = new IndexWriter(dir, config);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to create IndexWriter: %s.", e.getMessage()));
    }

    LOG.info("HnswIndexer settings:");
    LOG.info(" + Generator: " + args.generatorClass);
    LOG.info(" + M: " + args.M);
    LOG.info(" + efC: " + args.efC);
    LOG.info(" + Store document vectors? " + args.storeVectors);
    LOG.info(" + Codec: " + this.writer.getConfig().getCodec());
    LOG.info(" + MemoryBuffer: " + args.memoryBuffer);

    if (args.noMerge) {
      LOG.info(" + MergePolicy: NoMerge");
    } else {
      LOG.info(" + MergePolicy: TieredMergePolicy");
      LOG.info(" + MaxMergedSegmentSize: " + args.maxMergedSegmentSize);
      LOG.info(" + SegmentsPerTier: " + args.segmentsPerTier);
      LOG.info(" + MaxMergeAtOnce: " + args.maxMergeAtOnce);
    }
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
