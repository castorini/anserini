package io.anserini.kg.freebase;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.codecs.lucene50.Lucene50StoredFieldsFormat;
import org.apache.lucene.codecs.lucene62.Lucene62Codec;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Builds a triples lookup index from a Freebase dump in N-Triples RDF format. Each
 * {@link FreebaseNode} object, which represents a group of triples that share the same subject
 * ({@code mid}), is treated as a Lucene "document". This class builds an index for lookup based
 * on {@code mid}.
 */
public class IndexTopics {
  private static final Logger LOG = LogManager.getLogger(IndexTopics.class);

  public static final class Args {
    @Option(name = "-input", metaVar = "[file]", required = true, usage = "Freebase dump file")
    public Path input;

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
    public Path index;
  }

  /**
   * Four fields:
   * topicMid - the MID of the topic
   * title - the object value of the (topicMid, http://rdf.freebase.com/key/wikipedia.en_title)
   * label - the object value of the (topicMid, http://www.w3.org/2000/01/rdf-schema#label)
   * name - the object value of the (topicMid, http://rdf.freebase.com/ns/type.object.name)
   * text - all the values separated by space of the (topicMid, http://rdf.freebase.com/key/wikipedia.en)
   */
  public static final String FIELD_TOPIC_MID = "topicMid";
  public static final String FIELD_TITLE = "title";
  public static final String FIELD_LABEL = "label";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_TEXT = "text";

  /**
   * Predicates for which the literals should be stored
   */
  private static final String WIKI_EN_URI = FreebaseNode.FREEBASE_KEY_SHORT + "wikipedia.en";
  private static final String WIKI_EN_TILE_URI = WIKI_EN_URI + "_title";
  private static final String W3_LABEL_URI = "http://www.w3.org/2000/01/rdf-schema#label";
  private static final String FB_OBJECT_NAME = FreebaseNode.FREEBASE_NS_SHORT + "type.object.name";


  private final Path indexPath;
  private final Path inputPath;

  public IndexTopics(Path inputPath, Path indexPath) throws Exception {
    this.inputPath = inputPath;
    this.indexPath = indexPath;

    LOG.info("Input path: " + this.inputPath);
    LOG.info("Index path: " + this.indexPath);

    if (!Files.exists(inputPath) || !Files.isReadable(inputPath)) {
      throw new IllegalArgumentException("Input " + inputPath.toString() +
              " does not exist or is not readable.");
    }
  }

  public void run() throws IOException, InterruptedException {
    final long start = System.nanoTime();
    LOG.info("Starting indexer...");

    final Directory dir = FSDirectory.open(indexPath);
    final SimpleAnalyzer analyzer = new SimpleAnalyzer();
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setCodec(new Lucene62Codec(Lucene50StoredFieldsFormat.Mode.BEST_SPEED));
    config.setUseCompoundFile(false);

    final IndexWriter writer = new IndexWriter(dir, config);

    final AtomicInteger cnt = new AtomicInteger();
    new Freebase(inputPath).stream().map(new TopicLuceneDocumentGenerator())
        .forEach(doc -> {
          try {
            writer.addDocument(doc);
            int cur = cnt.incrementAndGet();
            if (cur % 10000000 == 0) {
              LOG.info(cnt + " nodes added.");
            }
          } catch (IOException e) {
            LOG.error(e);
          }
        });

    LOG.info(cnt.get() + " nodes added.");
    int numIndexed = writer.maxDoc();

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
      System.err.println("Example: "+ IndexTopics.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexTopics(indexArgs.input, indexArgs.index).run();
  }

  private static class TopicLuceneDocumentGenerator implements Function<FreebaseNode, Document> {
    public Document apply(FreebaseNode src) {
      String topicMid = FreebaseNode.cleanUri(src.uri());
      String title = "";
      String label = "";
      String name = "";
      String text = "";
      Map<String, List<String>> predicateValues = src.getPredicateValues();
      // Iterate over predicates and object values
      for(Map.Entry<String, List<String>> entry: predicateValues.entrySet()) {
        String predicate = FreebaseNode.cleanUri( entry.getKey() );
        List<String> objects = entry.getValue();
        for (String object : objects) {
          if (predicate.startsWith(WIKI_EN_URI)) {
            if (predicate.startsWith(WIKI_EN_TILE_URI)) {
              title = FreebaseNode.normalizeObjectValue(object);
            } else {
              // concatenate other variants with a space
              text += FreebaseNode.normalizeObjectValue((object)) + " ";
            }
          } else if (predicate.startsWith(W3_LABEL_URI)) {
            label += FreebaseNode.normalizeObjectValue(object) + " ";
          } else if (predicate.startsWith(FB_OBJECT_NAME)) {
            name += FreebaseNode.normalizeObjectValue(object) + " ";
          }
        }
      }

      // Convert to a document
      Document doc = new Document();

      // Index subject as a StringField to allow searching
      Field topicMidField = new StringField(FIELD_TOPIC_MID, topicMid, Field.Store.YES);
      doc.add(topicMidField);

      Field titleField = new TextField(FIELD_TITLE, title, Field.Store.YES);
      doc.add(titleField);

      Field nameField = new TextField(FIELD_NAME, name, Field.Store.YES);
      doc.add(nameField);

      Field labelField = new TextField(FIELD_LABEL, label, Field.Store.YES);
      doc.add(labelField);

      Field textField = new TextField(FIELD_TEXT, text, Field.Store.YES);
      doc.add(textField);

      return doc;
    }
  }
}
