package io.anserini.collection;

import io.anserini.document.RDFDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

/**
 * Class representing an instance of an RDF collection.
 * RDF datasets can represent knowledge bases such as Freebase.
 * They often come in a single .gz file.
 */
public class RDFCollection extends Collection<RDFDocument> {

  public static AtomicInteger linesCounter = new AtomicInteger(0);

  public class CompressedFileSegment extends FileSegment {
    protected BufferedReader bufferedReader;
    protected final int BUFFER_SIZE = 1 << 16; //64K

    protected CompressedFileSegment(Path path) throws IOException {
      this.path = path;
      this.bufferedReader = null;
      String fileName = path.toString();
      if (fileName.endsWith(".gz")) { //.gz
        InputStream stream = new GZIPInputStream(
                Files.newInputStream(path, StandardOpenOption.READ), BUFFER_SIZE);
        bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      } else { // in case user had already uncompressed the folder
        bufferedReader = new BufferedReader(new FileReader(fileName));
      }
    }

    @Override
    public void close() throws IOException {
      atEOF = false;
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }

    @Override
    public RDFDocument next() {
      RDFDocument doc = null;

      try {
        String line = bufferedReader.readLine().trim();
        if (line == null) {
          atEOF = true;
          doc = null;
        } else if (line.startsWith("#") || line.equals("")) {
          // Ignore comments and empty lines
          doc = null;
        } else {
          int currentLineIndex = linesCounter.incrementAndGet();
          doc = new RDFDocument(Integer.toString(currentLineIndex), line);
        }
      } catch (IOException e) {
        doc = null;
      }

      return doc;
    }
  }

  /**
   * Do not limit file extensions
   * @return all files
   */
  @Override
  public List<Path> getFileSegmentPaths() {
    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
            EMPTY_SET, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new CompressedFileSegment(p);
  }
}
