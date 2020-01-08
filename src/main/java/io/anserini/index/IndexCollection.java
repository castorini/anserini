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

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import io.anserini.analysis.EnglishStemmingAnalyzer;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.collection.DocumentCollection;
import io.anserini.collection.FileSegment;
import io.anserini.collection.SourceDocument;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.index.generator.WapoGenerator;
import io.anserini.search.similarity.AccurateBM25Similarity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class IndexCollection {
  private static final Logger LOG = LogManager.getLogger(IndexCollection.class);

  private static final int TIMEOUT = 600 * 1000;
  // This is the default analyzer used, unless another stemming algorithm or language is specified.
  public static final Analyzer DEFAULT_ANALYZER = new EnglishStemmingAnalyzer("porter");

  // When duplicates of these fields are attempted to be indexed in Solr, they are ignored. This allows some fields to be multi-valued, but not others.
  // Stored vs. indexed vs. doc values vs. multi-valued vs. ... are controlled via config, rather than code, in Solr.
  private static final List<String> IGNORED_DUPLICATE_FIELDS = Lists.newArrayList(WapoGenerator.WapoField.PUBLISHED_DATE.name);

  public final class Counters {
    /**
     * Counter for successfully indexed documents.
     */
    public AtomicLong indexed = new AtomicLong();

    /**
     * Counter for empty documents that are not indexed. Empty documents are not necessary errors;
     * it could be the case, for example, that a document is comprised solely of stopwords.
     */
    public AtomicLong empty = new AtomicLong();

    /**
     * Counter for unindexable documents. These are cases where {@link SourceDocument#indexable()}
     * returns false.
     */
    public AtomicLong unindexable = new AtomicLong();

    /**
     * Counter for skipped documents. These are cases documents are skipped as part of normal
     * processing logic, e.g., using a whitelist, not indexing retweets or deleted tweets.
     */
    public AtomicLong skipped = new AtomicLong();

    /**
     * Counter for unexpected errors.
     */
    public AtomicLong errors = new AtomicLong();
  }

  private final class LocalIndexerThread extends Thread {
    final private Path inputFile;
    final private IndexWriter writer;
    final private DocumentCollection collection;
    private FileSegment fileSegment;

    private LocalIndexerThread(IndexWriter writer, DocumentCollection collection, Path inputFile) {
      this.writer = writer;
      this.collection = collection;
      this.inputFile = inputFile;
      setName(inputFile.getFileName().toString());
    }

    @Override
    public void run() {
      try {
        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator generator =
            (LuceneDocumentGenerator) generatorClass
                .getDeclaredConstructor(IndexArgs.class, Counters.class)
                .newInstance(args, counters);

        // We keep track of two separate counts: the total count of documents in this file segment (cnt),
        // and the number of documents in this current "batch" (batch). We update the global counter every
        // 10k documents: this is so that we get intermediate updates, which is informative if a collection
        // has only one file segment; see https://github.com/castorini/anserini/issues/683
        int cnt = 0;
        int batch = 0;

        @SuppressWarnings("unchecked")
        FileSegment<SourceDocument> segment =
            (FileSegment) collection.createFileSegment(inputFile);
        // in order to call close() and clean up resources in case of exception
        this.fileSegment = segment;

        for (Object document : segment) {
          SourceDocument d = (SourceDocument) document;

          if (!d.indexable()) {
            counters.unindexable.incrementAndGet();
            continue;
          }

          // Used for indexing distinct shardCount of a collection
          if (args.shardCount > 1) {
            int hash = Hashing.sha1().hashString(d.id(), Charsets.UTF_8).asInt() % args.shardCount;
            if (hash != args.shardCurrent) {
              counters.skipped.incrementAndGet();
              continue;
            }
          }

          // Yes, we know what we're doing here.
          @SuppressWarnings("unchecked")
          Document doc = generator.createDocument(d);
          if (doc == null) {
            continue;
          }

          if (whitelistDocids != null && !whitelistDocids.contains(d.id())) {
            counters.skipped.incrementAndGet();
            continue;
          }

          if (args.uniqueDocid) {
            writer.updateDocument(new Term("id", d.id()), doc);
          } else {
            writer.addDocument(doc);
          }
          cnt++;
          batch++;

          // And the counts from this batch, reset batch counter.
          if (batch % 10000 == 0) {
            counters.indexed.addAndGet(batch);
            batch = 0;
          }
        }

        // Add the remaining documents.
        counters.indexed.addAndGet(batch);

        int skipped = segment.getSkippedCount();
        if (skipped > 0) {
          // When indexing tweets, this is normal, because there are delete messages that are skipped over.
          counters.skipped.addAndGet(skipped);
          LOG.warn(inputFile.getParent().getFileName().toString() + File.separator +
              inputFile.getFileName().toString() + ": " + skipped + " docs skipped.");
        }

        if (segment.getErrorStatus()) {
          counters.errors.incrementAndGet();
          LOG.error(inputFile.getParent().getFileName().toString() + File.separator +
              inputFile.getFileName().toString() + ": error iterating through segment.");
        }

        // Log at the debug level because this can be quite noisy if there are lots of file segments.
        LOG.debug(inputFile.getParent().getFileName().toString() + File.separator +
            inputFile.getFileName().toString() + ": " + cnt + " docs added.");
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      } finally {
        // clean up resources
        try {
          if (fileSegment != null){
            fileSegment.close();
          }
        } catch (IOException io) {
          LOG.error("IOException closing segment: " + io.getMessage());
        }
      }
    }
  }

  private final class SolrIndexerThread implements Runnable {
    private final Path input;
    private final DocumentCollection collection;
    private final List<SolrInputDocument> buffer = new ArrayList<>(args.solrBatch);
    private FileSegment fileSegment;

    private SolrIndexerThread(DocumentCollection collection, Path input) {
      this.input = input;
      this.collection = collection;
    }

    @Override
    public void run() {
      try {
        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator generator =
            (LuceneDocumentGenerator) generatorClass
                .getDeclaredConstructor(IndexArgs.class, Counters.class)
                .newInstance(args, counters);

        int cnt = 0;

        @SuppressWarnings("unchecked")
        FileSegment<SourceDocument> segment =
            (FileSegment) collection.createFileSegment(input);
        // in order to call close() and clean up resources in case of exception
        this.fileSegment = segment;

        for (Object d : segment) {
          SourceDocument sourceDocument = (SourceDocument) d;

          if (!sourceDocument.indexable()) {
            counters.unindexable.incrementAndGet();
            continue;
          }

          // Used for indexing distinct shardCount of a collection
          if (args.shardCount > 1) {
            int hash = Hashing.sha1().hashString(sourceDocument.id(), Charsets.UTF_8).asInt() % args.shardCount;
            if (hash != args.shardCurrent) {
              counters.skipped.incrementAndGet();
              continue;
            }
          }

          // Yes, we know what we're doing here.
          @SuppressWarnings("unchecked")
          Document document = generator.createDocument(sourceDocument);
          if (document == null) {
            continue;
          }

          if (whitelistDocids != null && !whitelistDocids.contains(sourceDocument.id())) {
            counters.skipped.incrementAndGet();
            continue;
          }

          SolrInputDocument solrDocument = new SolrInputDocument();

          // Copy all Lucene Document fields to Solr document
          for (IndexableField field : document.getFields()) {
            // Skip docValues fields - this is done via Solr config.
            if (field.fieldType().docValuesType() != DocValuesType.NONE) {
              continue;
            }
            // If the field is already in the doc, skip it.
            // This fixes an issue with WaPo where published_date is in the Lucene doc as LongPoint and StoredField. Solr needs one copy, more fine-grained control in config.
            if (solrDocument.containsKey(field.name()) && IGNORED_DUPLICATE_FIELDS.contains(field.name())) {
              continue;
            }
            if (field.numericValue() != null) {
              solrDocument.addField(field.name(), field.numericValue());
            } else if (field.stringValue() != null) { // For some reason, id is multi-valued with null as one of the values
              solrDocument.addField(field.name(), field.stringValue());
            }
          }

          buffer.add(solrDocument);
          if (buffer.size() == args.solrBatch) {
            flush();
          }

          cnt++;
        }

        // If we have docs in the buffer, flush them.
        if (!buffer.isEmpty()) {
          flush();
        }

        int skipped = segment.getSkippedCount();
        if (skipped > 0) {
          // When indexing tweets, this is normal, because there are delete messages that are skipped over.
          counters.skipped.addAndGet(skipped);
          LOG.warn(input.getParent().getFileName().toString() + File.separator +
              input.getFileName().toString() + ": " + skipped + " docs skipped.");
        }

        if (segment.getErrorStatus()) {
          counters.errors.incrementAndGet();
          LOG.error(input.getParent().getFileName().toString() + File.separator +
              input.getFileName().toString() + ": error iterating through segment.");
        }

        // Log at the debug level because this can be quite noisy if there are lots of file segments.
        LOG.debug(input.getParent().getFileName().toString() + File.separator +
            input.getFileName().toString() + ": " + cnt + " docs added.");
        counters.indexed.addAndGet(cnt);
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      } finally {
        // clean up resources
        try {
          if (fileSegment != null){
            fileSegment.close();
          }
        } catch (IOException io) {
          LOG.error("IOException closing segment: " + io.getMessage());
        }
      }

    }

    private void flush() {
      if (!buffer.isEmpty()) {
        SolrClient solrClient = null;
        try {
          solrClient = solrPool.borrowObject();
          solrClient.add(args.solrIndex, buffer, args.solrCommitWithin * 1000);
          buffer.clear();
        } catch (Exception e) {
          LOG.error("Error flushing documents to Solr", e);
        } finally {
          if (solrClient != null) {
            try {
              solrPool.returnObject(solrClient);
            } catch (Exception e) {
              LOG.error("Error returning SolrClient to pool", e);
            }
          }
        }
      }
    }
  }

  private class SolrClientFactory extends BasePooledObjectFactory<SolrClient> {
    @Override
    public SolrClient create() {
      return new CloudSolrClient.Builder(Splitter.on(',').splitToList(args.zkUrl), Optional.of(args.zkChroot))
          .withConnectionTimeout(TIMEOUT)
          .withSocketTimeout(TIMEOUT)
          .build();
    }

    @Override
    public PooledObject<SolrClient> wrap(SolrClient solrClient) {
      return new DefaultPooledObject<>(solrClient);
    }

    @Override
    public void destroyObject(PooledObject<SolrClient> pooled) throws Exception {
      pooled.getObject().close();
    }
  }

  private final class ESIndexerThread implements Runnable {
    private final Path input;
    private final DocumentCollection collection;
    private BulkRequest bulkRequest;
    private FileSegment fileSegment;

    private ESIndexerThread(DocumentCollection collection, Path input) {
      this.input = input;
      this.collection = collection;
      this.bulkRequest = new BulkRequest();
    }

    @Override
    public void run() {
      try {

        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator generator =
            (LuceneDocumentGenerator) generatorClass
                .getDeclaredConstructor(IndexArgs.class, Counters.class)
                .newInstance(args, counters);

        @SuppressWarnings("unchecked")
        FileSegment<SourceDocument> segment =
            (FileSegment) collection.createFileSegment(input);
        // in order to call close() and clean up resources in case of exception
        this.fileSegment = segment;

        int cnt = 0;

        for (Object d : segment) {
          SourceDocument sourceDocument = (SourceDocument) d;

          if (!sourceDocument.indexable()) {
            counters.unindexable.incrementAndGet();
            continue;
          }

          // Used for indexing distinct shardCount of a collection
          if (args.shardCount > 1) {
            int hash = Hashing.sha1().hashString(sourceDocument.id(), Charsets.UTF_8).asInt() % args.shardCount;
            if (hash != args.shardCurrent) {
              counters.skipped.incrementAndGet();
              continue;
            }
          }

          // Yes, we know what we're doing here.
          @SuppressWarnings("unchecked")
          Document document = generator.createDocument(sourceDocument);
          if (document == null) {
            continue;
          }

          if (whitelistDocids != null && !whitelistDocids.contains(sourceDocument.id())) {
            counters.skipped.incrementAndGet();
            continue;
          }

          // Get distinct field names
          List<String> fields = document.getFields().stream().map(field -> field.name()).distinct().collect(Collectors.toList());

          XContentBuilder builder = XContentFactory.jsonBuilder().startObject();

          for (String field : fields) {

            // Skip docValues fields
            if (document.getField(field).fieldType().docValuesType() != DocValuesType.NONE) continue;

            // Get field objects for current field name (could be multiple, such as WaPo's fullCaption)
            IndexableField[] indexableFields = document.getFields(field);

            if (field.equalsIgnoreCase("id") || indexableFields.length == 1) {
              // Single value fields or "id" field
              Object value = document.getField(field).stringValue() != null ? document.getField(field).stringValue() : document.getField(field).numericValue();
              builder.field(field, value);
            } else {
              // Multi-valued fields
              Object[] values = Stream.of(indexableFields).map(f -> f.stringValue()).toArray();
              builder.array(field, values);
            }
          }

          builder.endObject();

          String indexName = (args.esIndex != null) ? args.esIndex : input.getFileName().toString();
          bulkRequest.add(new IndexRequest(indexName).id(sourceDocument.id()).source(builder));
          if (bulkRequest.numberOfActions() == args.esBatch) {
            sendBulkRequest();
          }

          cnt++;
        }

        if (bulkRequest.numberOfActions() != 0) {
          sendBulkRequest();
        }

        int skipped = segment.getSkippedCount();
        if (skipped > 0) {
          // When indexing tweets, this is normal, because there are delete messages that are skipped over.
          counters.skipped.addAndGet(skipped);
          LOG.warn(input.getParent().getFileName().toString() + File.separator +
              input.getFileName().toString() + ": " + skipped + " docs skipped.");
        }

        if (segment.getErrorStatus()) {
          counters.errors.incrementAndGet();
          LOG.error(input.getParent().getFileName().toString() + File.separator +
              input.getFileName().toString() + ": error iterating through segment.");
        }

        // Log at the debug level because this can be quite noisy if there are lots of file segments.
        LOG.debug(input.getParent().getFileName().toString() + File.separator +
            input.getFileName().toString() + ": " + cnt + " docs added.");
        counters.indexed.addAndGet(cnt);
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      } finally {
        // clean up resources
        try {
          if (fileSegment != null){
            fileSegment.close();
          }
        } catch (IOException io) {
          LOG.error("IOException closing segment: " + io.getMessage());
        }
      }
    }

    private void sendBulkRequest() {
      if (bulkRequest.numberOfActions() == 0) {
        return;
      }

      RestHighLevelClient esClient = null;
      try {
        esClient = esPool.borrowObject();
        esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        bulkRequest = new BulkRequest();
      } catch (Exception e) {
        LOG.error("Error sending bulk requests to Elasticsearch", e);
      } finally {
        if (esClient != null) {
          try {
            esPool.returnObject(esClient);
          } catch (Exception e) {
            LOG.error("Error returning ES client to pool", e);
          }
        }
      }
    }
  }

  private class ESClientFactory extends BasePooledObjectFactory<RestHighLevelClient> {
    @Override
    public RestHighLevelClient create() {
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(args.esUser, args.esPassword));
      return new RestHighLevelClient(
          RestClient.builder(new HttpHost(args.esHostname, args.esPort, "http"))
              .setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider))
              .setRequestConfigCallback(builder -> builder.setConnectTimeout(args.esConnectTimeout).setSocketTimeout(args.esSocketTimeout))
      );
    }

    @Override
    public PooledObject<RestHighLevelClient> wrap(RestHighLevelClient esClient) {
      return new DefaultPooledObject<>(esClient);
    }

    @Override
    public void destroyObject(PooledObject<RestHighLevelClient> pooled) throws Exception {
      pooled.getObject().close();
    }
  }

  private final IndexArgs args;
  private final Path collectionPath;
  private final Set whitelistDocids;
  private final Class collectionClass;
  private final Class generatorClass;
  private final DocumentCollection collection;
  private final Counters counters;
  private Path indexPath;
  private ObjectPool<SolrClient> solrPool;
  private ObjectPool<RestHighLevelClient> esPool;

  public IndexCollection(IndexArgs args) throws Exception {
    this.args = args;

    if (args.verbose) {
      // If verbose logging enabled, changed default log level to DEBUG so we get per-thread logging messages.
      Configurator.setRootLevel(Level.DEBUG);
      LOG.info("Setting log level to " + Level.DEBUG);
    } else if (args.quiet) {
      // If quiet mode enabled, only report warnings and above.
      Configurator.setRootLevel(Level.WARN);
    } else {
      // Otherwise, we get the standard set of log messages.
      Configurator.setRootLevel(Level.INFO);
      LOG.info("Setting log level to " + Level.INFO);
    }

    LOG.info("Starting indexer...");
    LOG.info("============ Loading Parameters ============");
    LOG.info("DocumentCollection path: " + args.input);
    LOG.info("CollectionClass: " + args.collectionClass);
    LOG.info("Generator: " + args.generatorClass);
    LOG.info("Threads: " + args.threads);
    LOG.info("Stemmer: " + args.stemmer);
    LOG.info("Keep stopwords? " + args.keepStopwords);
    LOG.info("Store positions? " + args.storePositions);
    LOG.info("Store docvectors? " + args.storeDocvectors);
    LOG.info("Store transformed docs? " + args.storeTransformedDocs);
    LOG.info("Store raw docs? " + args.storeRawDocs);
    LOG.info("Optimize (merge segments)? " + args.optimize);
    LOG.info("Whitelist: " + args.whitelist);

    if (args.solr) {
      LOG.info("Indexing into Solr...");
      LOG.info("Solr batch size: " + args.solrBatch);
      LOG.info("Solr commitWithin: " + args.solrCommitWithin);
      LOG.info("Solr index: " + args.solrIndex);
      LOG.info("Solr ZooKeeper URL: " + args.zkUrl);
      LOG.info("SolrClient pool size: " + args.solrPoolSize);
    } else if (args.es) {
      LOG.info("Indexing into Elasticsearch...");
      LOG.info("Elasticsearch batch size: " + args.esBatch);
      LOG.info("Elasticsearch index: " + args.esIndex);
      LOG.info("Elasticsearch hostname: " + args.esHostname);
      LOG.info("Elasticsearch host port: " + args.esPort);
      LOG.info("Elasticsearch client connect timeout (in ms): " + args.esConnectTimeout);
      LOG.info("Elasticsearch client socket timeout (in ms): " + args.esSocketTimeout);
      LOG.info("Elasticsearch pool size: " + args.esPoolSize);
      LOG.info("Elasticsearch user: " + args.esUser);
    } else {
      LOG.info("Directly building Lucene indexes...");
      LOG.info("Index path: " + args.index);
    }

    if (args.index == null && !args.solr && !args.es) {
      throw new IllegalArgumentException("Must specify one of -index, -solr, or -es");
    }

    if (args.index != null) {
      this.indexPath = Paths.get(args.index);
      if (!Files.exists(this.indexPath)) {
        Files.createDirectories(this.indexPath);
      }
    }

    collectionPath = Paths.get(args.input);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new RuntimeException("Document directory " + collectionPath.toString() + " does not exist or is not readable, please check the path");
    }

    this.generatorClass = Class.forName("io.anserini.index.generator." + args.generatorClass);
    this.collectionClass = Class.forName("io.anserini.collection." + args.collectionClass);

    // There's only one constructor, so this is safe-ish... skipping any sort of error checking.
    collection = (DocumentCollection) this.collectionClass.getDeclaredConstructors()[0].newInstance();
    collection.setCollectionPath(collectionPath);

    if (args.whitelist != null) {
      List<String> lines = FileUtils.readLines(new File(args.whitelist), "utf-8");
      this.whitelistDocids = new HashSet<>(lines);
    } else {
      this.whitelistDocids = null;
    }

    if (args.solr) {
      GenericObjectPoolConfig<SolrClient> config = new GenericObjectPoolConfig<>();
      config.setMaxTotal(args.solrPoolSize);
      config.setMinIdle(args.solrPoolSize); // To guard against premature discarding of solrClients
      this.solrPool = new GenericObjectPool<>(new SolrClientFactory(), config);
    } else if (args.es) {
      GenericObjectPoolConfig<RestHighLevelClient> config = new GenericObjectPoolConfig<>();
      config.setMaxTotal(args.esPoolSize);
      config.setMinIdle(args.esPoolSize);
      this.esPool = new GenericObjectPool<>(new ESClientFactory(), config);
    }

    this.counters = new Counters();
  }

  public Counters run() throws IOException {
    final long start = System.nanoTime();
    LOG.info("============ Indexing Collection ============");

    int numThreads = args.threads;
    IndexWriter writer = null;

    // Used for LocalIndexThread
    if (indexPath != null) {
      final Directory dir = FSDirectory.open(indexPath);
      final CJKAnalyzer chineseAnalyzer = new CJKAnalyzer();
      final ArabicAnalyzer arabicAnalyzer = new ArabicAnalyzer();
      final FrenchAnalyzer frenchAnalyzer = new FrenchAnalyzer();
      final HindiAnalyzer hindiAnalyzer = new HindiAnalyzer();
      final BengaliAnalyzer bengaliAnalyzer = new BengaliAnalyzer();
      final GermanAnalyzer germanAnalyzer = new GermanAnalyzer();
      final SpanishAnalyzer spanishAnalyzer = new SpanishAnalyzer();
      final EnglishStemmingAnalyzer analyzer = args.keepStopwords ?
          new EnglishStemmingAnalyzer(args.stemmer, CharArraySet.EMPTY_SET) : new EnglishStemmingAnalyzer(args.stemmer);
      final TweetAnalyzer tweetAnalyzer = new TweetAnalyzer(args.tweetStemming);

      final IndexWriterConfig config;
      if (args.collectionClass.equals("TweetCollection")) {
        config = new IndexWriterConfig(tweetAnalyzer);
      } else if (args.language.equals("zh")) {
        config = new IndexWriterConfig(chineseAnalyzer);
      } else if (args.language.equals("ar")) {
        config = new IndexWriterConfig(arabicAnalyzer);
      } else if (args.language.equals("fr")) {
        config = new IndexWriterConfig(frenchAnalyzer);
      } else if (args.language.equals("hi")) {
        config = new IndexWriterConfig(hindiAnalyzer);
      } else if (args.language.equals("bn")) {
        config = new IndexWriterConfig(bengaliAnalyzer);
      } else if (args.language.equals("de")) {
        config = new IndexWriterConfig(germanAnalyzer);
      } else if (args.language.equals("es")) {
        config = new IndexWriterConfig(spanishAnalyzer);
      } else {
        config = new IndexWriterConfig(analyzer);
      }
      if (args.bm25Accurate) {
        config.setSimilarity(new AccurateBM25Similarity()); // necessary during indexing as the norm used in BM25 is already determined at index time.
      } else {
        config.setSimilarity(new BM25Similarity());
      }
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      config.setRAMBufferSizeMB(args.memorybufferSize);
      config.setUseCompoundFile(false);
      config.setMergeScheduler(new ConcurrentMergeScheduler());

      writer = new IndexWriter(dir, config);
    }

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    LOG.info("Thread pool with " + numThreads + " threads initialized.");

    LOG.info("Initializing collection in " + collectionPath.toString());
    final List segmentPaths = collection.discover(collection.getCollectionPath());
    final int segmentCnt = segmentPaths.size();
    LOG.info(String.format("%,d %s found", segmentCnt, (segmentCnt == 1 ? "file" : "files" )));
    LOG.info("Starting to index...");

    for (int i = 0; i < segmentCnt; i++) {
      if (args.solr) {
        executor.execute(new SolrIndexerThread(collection, (Path) segmentPaths.get(i)));
      } else if (args.es) {
        executor.execute(new ESIndexerThread(collection, (Path) segmentPaths.get(i)));
      } else {
        executor.execute(new LocalIndexerThread(writer, collection, (Path) segmentPaths.get(i)));
      }
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        if (segmentCnt == 1) {
          LOG.info(String.format("%,d documents indexed", counters.indexed.get()));
        } else {
          LOG.info(String.format("%.2f%% of files completed, %,d documents indexed",
              (double) executor.getCompletedTaskCount() / segmentCnt * 100.0d, counters.indexed.get()));
        }
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    if (segmentCnt != executor.getCompletedTaskCount()) {
      throw new RuntimeException("totalFiles = " + segmentCnt +
          " is not equal to completedTaskCount =  " + executor.getCompletedTaskCount());
    }

    long numIndexed;

    if (args.solr || args.es) {
      numIndexed = counters.indexed.get();
    } else {
      numIndexed = writer.getDocStats().maxDoc;
    }

    // Do a final commit
    if (args.solr) {
      try {
        SolrClient client = solrPool.borrowObject();
        client.commit(args.solrIndex);
        // Needed for orderly shutdown so the SolrClient executor does not delay main thread exit
        solrPool.returnObject(client);
        solrPool.close();
      } catch (Exception e) {
        LOG.error("Exception during final Solr commit: ", e);
      }
    }

    if (args.es) {
      esPool.close();
    }

    try {
      if (writer != null) {
        writer.commit();
        if (args.optimize) {
          writer.forceMerge(1);
        }
      }
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        // It is possible that this happens... but nothing much we can do at this point,
        // so just log the error and move on.
        LOG.error(e);
      }
    }

    if (numIndexed != counters.indexed.get()) {
      LOG.warn("Unexpected difference between number of indexed documents and index maxDoc.");
    }

    LOG.info(String.format("Indexing Complete! %,d documents indexed", numIndexed));
    LOG.info("============ Final Counter Values ============");
    LOG.info(String.format("indexed:     %,12d", counters.indexed.get()));
    LOG.info(String.format("unindexable: %,12d", counters.unindexable.get()));
    LOG.info(String.format("empty:       %,12d", counters.empty.get()));
    LOG.info(String.format("skipped:     %,12d", counters.skipped.get()));
    LOG.info(String.format("errors:      %,12d", counters.errors.get()));

    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info(String.format("Total %,d documents indexed in %s", numIndexed,
        DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss")));

    return counters;
  }

  public static void main(String[] args) throws Exception {
    IndexArgs indexCollectionArgs = new IndexArgs();
    CmdLineParser parser = new CmdLineParser(indexCollectionArgs, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: " + IndexCollection.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexCollection(indexCollectionArgs).run();
  }
}