package io.anserini.index;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Record class for a document from the CACM collection
 */
public class IndexCacmRecords {
  private static final Logger LOG = LogManager.getLogger(IndexCacmRecords.class);

  //******************************************
  private static final String INDEX_TAG = ".I";
  private static final String TITLE_TAG = ".T";
  private static final String DATE_TAG = ".B";
  private static final String AUTHOR_TAG = ".A";
  private static final String NUM_TAG = ".N";
  private static final String CITATION_TAG = ".X";
  private static final String SEPARATOR = " ";
  /**
   * Citations has the following format
   * 4 as type means Y shares w citations with this one
   * 5 means that Y cites this or this cites Y, or this is Y
   * 6 means this and the other document are cited together in w documents (w is not provided)
   */
  private static final String CITATION_SEPARATOR = ":";

  public static final String FIELD_INDEX = "index";
  public static final String FIELD_TITLE = "title";
  public static final String FIELD_DATE = "date";
  public static final String FIELD_AUTHOR = "author";
  public static final String FIELD_NUM = "num";
  public static final String FIELD_CITATION = "citation";
  public static final Analyzer ANALYZER = new EnglishAnalyzer();
  //******************************************

  private static class ParserArgs {
    @Option(name = "-input", metaVar = "[Path]", required = true, usage = "Input file path")
    String inputFile;
    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "Directory to write index")
    String indexDir;
    @Option(name = "-optimize", usage = "Boolean switch to optimize index (force merge)")
    boolean optimize = false;

  }

  public static void main(String[] args) throws IOException {
    ParserArgs parsedArgs = new ParserArgs();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    Directory directory = FSDirectory.open(Paths.get(parsedArgs.indexDir));
    IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
    IndexWriter writer = new IndexWriter(directory, config);
    indexRecordsFromFile(parsedArgs.inputFile, writer);

    if (parsedArgs.optimize) {
      writer.forceMerge(1);
    }
    writer.close();

  }

  public static void indexRecordsFromFile(String filePath, IndexWriter writer) throws IOException {
    FileInputStream inputStream = new FileInputStream(new File(filePath));

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    String line = reader.readLine();
    FieldType titleFieldType = new FieldType();
    titleFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    titleFieldType.setStoreTermVectors(true);
    titleFieldType.setStored(true);

    // Now we will read one line at a time
    while (line != null) {
      // Start of parse, we expect
      Document doc = new Document();

      int docNum;
      if (line.contains(INDEX_TAG)) {
        docNum = Integer.parseInt(line.trim().split(SEPARATOR)[1]);
        System.out.println(String.format("Doc num: %d", docNum));
        doc.add(new IntField(FIELD_INDEX, docNum, Field.Store.YES));
      } else {
        System.err.println(String.format("Invalid CACM record encountered. %s", line));
        LOG.warn("Error while parsing CACM record, skipping");
        return;
      }

      line = reader.readLine();
      if (!line.trim().equals(TITLE_TAG)) {
        System.err.println(String.format("Title tag expected but %s found", line));
        return;
      }
      line = parseTitle(doc, reader, titleFieldType);
      if (!line.trim().equals(DATE_TAG)) {
        System.err.println(String.format("Date tag expected but %s found", line));
        return;
      }

      // All of the methods below will pass back the last line it read
      line = parseDate(doc, reader);
      line = parseAuthors(doc, reader, titleFieldType);
      line = parseNum(doc, reader);
      // parseCitations will get new line to parse
      line = parseCitations(doc, reader);

      writer.addDocument(doc);
      LOG.debug(String.format("Finished Adding document %d", docNum));
      LOG.debug(String.format("Current Line: %s", line));
    }
  }

  /**
   * Parses the Title section of a CACM record
   * it is expected that the title is ONLY 1 line
   */
  private static String parseTitle(Document doc, BufferedReader reader, FieldType titleFieldType) throws IOException {
    String line = reader.readLine().trim();
    StringBuilder sb = new StringBuilder(line);
    line = reader.readLine().trim();
    while (!line.equals(DATE_TAG)) {
      sb.append(SEPARATOR);
      sb.append(line);
      line = reader.readLine().trim();
    }
    doc.add(new Field(FIELD_TITLE, sb.toString(), titleFieldType));
    return line;
  }

  private static String parseDate(Document doc, BufferedReader reader) throws IOException {
    String line = reader.readLine();
    doc.add(new StringField(FIELD_DATE, line.trim(), Field.Store.YES));
    return line;
  }

  private static String parseNum(Document doc, BufferedReader reader) throws IOException {
    String line = reader.readLine();
    doc.add(new StringField(FIELD_NUM, line.trim(), Field.Store.YES));
    return line;
  }

  private static String parseAuthors(Document doc, BufferedReader reader, FieldType fieldType) throws IOException {
    // Not checking for nulls here because input is expected to have content
    // fail otherwise because content corrupted
    String line = reader.readLine().trim();
    while (!line.equals(NUM_TAG)) {
      if (line.equals(AUTHOR_TAG)) {
        line = reader.readLine().trim();
        continue;
      }

      doc.add(new Field(FIELD_AUTHOR, line, fieldType));
      line = reader.readLine().trim();
    }
    return line;
  }

  private static String parseCitations(Document doc, BufferedReader reader) throws IOException {
    String line = reader.readLine();
    // There are duplicates in this section, dedup
    Map<String, String> citationVectors = new HashMap<>();
    // Line could be null in this instance
    while (line != null) {
      // We check if the line contains the index tag, because if it does
      // return from this and the loop for processing a document starts again
      if (line.contains(INDEX_TAG)) {
        break;
      }

      if (line.trim().equals(CITATION_TAG)) {
        line = reader.readLine();
        continue;
      }

      String[] pieces = line.trim().replaceAll("\\s*", SEPARATOR).split(SEPARATOR);
      citationVectors.put(pieces[0].trim(), pieces[1].trim());
      line = reader.readLine();
    }

    // Process the citation vectors
    for (Map.Entry<String, String> entry : citationVectors.entrySet()) {
      doc.add(new StringField(FIELD_CITATION, entry.getKey() + CITATION_SEPARATOR + entry.getValue(), Field.Store.YES));
    }
    return line;
  }
}
