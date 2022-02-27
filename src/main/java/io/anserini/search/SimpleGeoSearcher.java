package io.anserini.search;

import io.anserini.index.IndexArgs;
import io.anserini.search.topicreader.TopicReader;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LatLonShape;
import org.apache.lucene.document.ShapeField;
import org.apache.lucene.geo.LatLonGeometry;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class SimpleGeoSearcher extends SimpleSearcher implements Closeable {
  private static final Logger LOG = LogManager.getLogger(SimpleGeoSearcher.class);

  private IndexReader reader;
  private IndexSearcher searcher = null;

//  public static final class Args {
//    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index.")
//    public String index;
//
//    @Option(name = "-input", metaVar = "[file]", required = true, usage = "Queries file.")
//    public String input;
//
//    @Option(name = "-output", metaVar = "[file]", required = true, usage = "Output run file.")
//    public String output;
//
//    @Option(name = "-hits", metaVar = "[number]", usage = "Max number of hits to return.")
//    public int hits = 1000;
//
//    @Option(name = "-threads", metaVar = "[number]", usage = "Number of threads to use.")
//    public int threads = 1;
//  }

  public Result[] searchGeo(String queryString, int hits) throws IOException {
    Query query = new GeoQueryGenerator().buildQuery(queryString);

    if (searcher == null) {
      searcher = new IndexSearcher(reader);
    }

    TopDocs rs = searcher.search(query, hits);
    Result[] results = new Result[rs.scoreDocs.length];

    for (int i = 0; i < rs.scoreDocs.length; i++) {
      Document doc = searcher.doc(rs.scoreDocs[i].doc);
      String docId = doc.getField(IndexArgs.ID).stringValue();

      IndexableField field;
      field = doc.getField(IndexArgs.CONTENTS);
      String contents = field == null ? null : field.stringValue();

      field = doc.getField(IndexArgs.RAW);
      String raw = field == null ? null : field.stringValue();

      results[i] = new Result(docId, , rs.scoreDocs[i].score, contents, raw, doc);
    }
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public SimpleGeoSearcher(String indexDir) throws IOException {
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    reader = DirectoryReader.open(FSDirectory.open(indexPath));
    searcher = new IndexSearcher(reader);
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: GeoSearcher" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(searchArgs.index);
    SortedMap<Object, Map<String, String>> topics = TopicReader.getTopicsByFile(searchArgs.topics);

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(searchArgs.output), StandardCharsets.US_ASCII));

    for (Object id : topics.keySet()) {
      long t = Long.parseLong(topics.get(id).get("time"));
      Result[] results = searcher.searchGeo(topics.get(id).get("title"), 1000, t);

      for (int i=0; i<results.length; i++) {
        out.println(String.format(Locale.US, "%s Q0 %s %d %f Anserini", id, results[i].docid, (i+1), results[i].score));
      }
    }
    out.close();

    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
    }


  }
}
