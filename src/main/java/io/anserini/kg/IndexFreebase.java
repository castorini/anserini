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

package io.anserini.kg;

import io.anserini.analysis.FreebaseAnalyzer;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
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
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Builds an index over a Freebase dump in N-Triples RDF format. Two types of indexes are possible:
 *
 * <ul>
 *
 * <li>Each {@link FreebaseNode} object, which represents a group of triples that share the same
 * subject ({@code mid}), is treated as a Lucene "document". This class builds an index for lookup
 * based on {@code mid} as well a free text search over the textual labels of the nodes.</li>
 *
 * <li>Each triple is treated as its own Lucene "document".</li>
 *
 * </ul>
 */
public class IndexFreebase {
  private static final Logger LOG = LogManager.getLogger(IndexFreebase.class);

  public static final class Args {
    @Option(name = "-input", metaVar = "[file]", required = true, usage = "Freebase dump file")
    protected Path input;

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    protected Path index;

    @Option(name = "-triples", usage = "store each triple in its own Lucene document")
    protected boolean storeTriples = false;

    @Option(name = "-langEnOnly", usage = "store only English literals")
    protected boolean langEnOnly = false;
  }

  // For storing each triple in its own Lucene document
  public static final String FIELD_SUBJECT = "s";
  public static final String FIELD_PREDICATE = "p";
  public static final String FIELD_OBJECT = "o";
  private static final int MAX_OBJECT_LENGTH = 16384;

  // For storing each node in its own Lucene document
  public static final String FIELD_ID = "mid";
  public static final String FIELD_LABEL = "label";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_ALIAS = "alias";

  private static final String W3_LABEL_URI = "http://www.w3.org/2000/01/rdf-schema#label";
  private static final String FB_OBJECT_NAME = FreebaseNode.FREEBASE_NS_SHORT + "type.object.name";
  private static final String FB_COMMON_TOPIC_ALIAS = FreebaseNode.FREEBASE_NS_SHORT + "common.topic.alias";

  private final Path indexPath;
  private final Path inputPath;
  private final boolean storeTriples;
  private final boolean langEnOnly;

  private final AtomicInteger docCount = new AtomicInteger();
  private final AtomicLong triplesCount = new AtomicLong();

  public IndexFreebase(Path inputPath, Path indexPath, boolean storeTriples, boolean langEnOnly) {
    this.inputPath = inputPath;
    this.indexPath = indexPath;
    this.storeTriples = storeTriples;
    this.langEnOnly = langEnOnly;

    LOG.info("Input path: " + this.inputPath);
    LOG.info("Index path: " + this.indexPath);

    if (!Files.exists(inputPath) || !Files.isReadable(inputPath)) {
      throw new IllegalArgumentException("Input " + inputPath.toString() +
          " does not exist or is not readable.");
    }

    if ( langEnOnly) {
      LOG.info("Storing only English literals.");
    }
  }

  public void run() throws IOException {
    final long start = System.nanoTime();
    LOG.info("Starting indexer...");

    final Directory dir = FSDirectory.open(indexPath);
    final FreebaseAnalyzer analyzer = new FreebaseAnalyzer();
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

    final IndexWriter writer = new IndexWriter(dir, config);

    Function<FreebaseNode, List<Document>> generator = storeTriples ?
        new LuceneDocumentGeneratorTriples(langEnOnly) : new LuceneDocumentGenerator(langEnOnly);

    try {
      new Freebase(inputPath).stream().map(generator)
          // The generator returns a list of documents, so this is really just a flatMap:
          .forEach(list -> list.forEach(doc -> {
            try {
              writer.addDocument(doc);
              int cur = docCount.incrementAndGet();
              if (cur % 10000000 == 0) {
                LOG.info(String.format("%,d documents added.", cur));
              }
              // This is pretty janky, but the last resort...
              if (cur >= IndexWriter.MAX_DOCS) throw new RuntimeException();
            } catch (IOException e) {
              LOG.error(e);
            }
          }));
    } catch (RuntimeException e) {
      // We've probably exceeded IndexWriter.MAX_DOCS... just eat the exception and continue.
      e.printStackTrace();
    }

    LOG.info(String.format("%,d triples indexed.", triplesCount.get()));
    LOG.info(String.format("%,d documents added.", docCount.get()));
    int numIndexed = writer.getDocStats().maxDoc;

    try {
      writer.commit();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        LOG.error(e);
      }
    }

    long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total " + numIndexed + " documents indexed in " +
        DurationFormatUtils.formatDuration(duration, "HH:mm:ss"));
  }

  public static void main(String[] args) throws Exception {
    Args indexArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ IndexFreebase.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexFreebase(indexArgs.input, indexArgs.index, indexArgs.storeTriples, indexArgs.langEnOnly).run();
  }

  // Needed for the document generators below.
  private static final String LANG_EN = "Optional[en]";
  private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

  private class LuceneDocumentGenerator implements Function<FreebaseNode, List<Document>> {
    private final boolean langEnOnly;

    private LuceneDocumentGenerator(boolean langEnOnly) {
      this.langEnOnly = langEnOnly;
    }

    public List<Document> apply(FreebaseNode src) {
      Document doc = new Document();
      doc.add(new StringField(FIELD_ID, FreebaseNode.cleanUri(src.uri()), Field.Store.YES));

      List<String> names = new ArrayList<>();
      List<String> aliases = new ArrayList<>();
      List<String> labels = new ArrayList<>();

      // Iterate over predicates and object values.
      for (Map.Entry<String, List<String>> entry : src.getPredicateValues().entrySet()) {
        final String predicate = FreebaseNode.cleanUri(entry.getKey());
        // Each predicate/value is a stored field.
        entry.getValue().forEach(value -> {
          if (langEnOnly) {
            // We only want to add English literals, so check the language.
            if (FreebaseNode.getObjectType(value).equals(FreebaseNode.RdfObjectType.TEXT)) {
              Literal parsedLiteral = NTriplesUtil.parseLiteral(value, valueFactory);
              if (parsedLiteral.getLanguage().toString().equals(LANG_EN)) {
                doc.add(new StoredField(predicate, FreebaseNode.normalizeObjectValue(value)));
                triplesCount.incrementAndGet();
              }
            } else {
              // But we still want to add everything else...
              doc.add(new StoredField(predicate, FreebaseNode.normalizeObjectValue(value)));
              triplesCount.incrementAndGet();
            }
          } else {
            doc.add(new StoredField(predicate, FreebaseNode.normalizeObjectValue(value)));
            triplesCount.incrementAndGet();
          }
        });

        List<String> objects = entry.getValue();
        for (String object : objects) {
          if (predicate.startsWith(W3_LABEL_URI)) {
            String label = FreebaseNode.normalizeObjectValue(object).trim();
            if (label.length() > 0) labels.add(label);
          } else if (predicate.startsWith(FB_OBJECT_NAME)) {
            String name = FreebaseNode.normalizeObjectValue(object).trim();
            if (name.length() > 0 ) names.add(name);
          } else if (predicate.startsWith(FB_COMMON_TOPIC_ALIAS)) {
            String alias = FreebaseNode.normalizeObjectValue(object).trim();
            if (alias.length() > 0 ) aliases.add(alias);
          }
        }
      }

      // These are the fields we're going to enable free-text search over.
      Field aliasField = new TextField(FIELD_ALIAS, String.join(" ", aliases), Field.Store.YES);
      doc.add(aliasField);

      Field nameField = new TextField(FIELD_NAME, String.join(" ", names), Field.Store.YES);
      doc.add(nameField);

      Field labelField = new TextField(FIELD_LABEL, String.join(" ", labels), Field.Store.YES);
      doc.add(labelField);

      List<Document> docs = new ArrayList<>();
      docs.add(doc);
      return docs;
    }
  }

  private class LuceneDocumentGeneratorTriples implements Function<FreebaseNode, List<Document>> {
    private final boolean langEnOnly;

    private LuceneDocumentGeneratorTriples(boolean langEnOnly) {
      this.langEnOnly = langEnOnly;
    }

    public List<Document> apply(FreebaseNode src) {
      List<Document> docs = new ArrayList<>();

      String subject = FreebaseNode.cleanUri(src.uri());
      // Iterate over predicates and object values.
      for (Map.Entry<String, List<String>> entry : src.getPredicateValues().entrySet()) {
        final String predicate = FreebaseNode.cleanUri(entry.getKey());
        // Each predicate/value triple forms a separate document.
        entry.getValue().forEach(value -> {
          Document doc = new Document();
          doc.add(new StringField(FIELD_SUBJECT, subject, Field.Store.YES));
          doc.add(new StringField(FIELD_PREDICATE, predicate, Field.Store.YES));

          String object = FreebaseNode.normalizeObjectValue(value);
          if (object.length() > MAX_OBJECT_LENGTH) {
            object = object.substring(0, MAX_OBJECT_LENGTH);
            LOG.warn(String.format(
                "Encountered excessive long object value for subject '%s' predicate '%s' - storing without indexing",
                subject, predicate));
            doc.add(new StoredField(FIELD_OBJECT, object));
          } else {
            doc.add(new StringField(FIELD_OBJECT, object, Field.Store.YES));
          }

          if (langEnOnly) {
            // We only want to add English literals, so check the language.
            if (FreebaseNode.getObjectType(value).equals(FreebaseNode.RdfObjectType.TEXT)) {
              Literal parsedLiteral = NTriplesUtil.parseLiteral(value, valueFactory);
              if (parsedLiteral.getLanguage().toString().equals(LANG_EN)) {
                docs.add(doc);
                triplesCount.incrementAndGet();
              }
            } else {
              // But we still want to add everything else...
              docs.add(doc);
              triplesCount.incrementAndGet();
            }
          } else {
            docs.add(doc);
            triplesCount.incrementAndGet();
          }
        });
      }

      return docs;
    }
  }
}