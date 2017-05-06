package io.anserini.collection;

import io.anserini.document.RDFDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

  private static final Logger LOG = LogManager.getLogger(RDFCollection.class);

  public class CompressedFileSegment extends FileSegment {
    protected BufferedReader bufferedReader;
    protected final int BUFFER_SIZE = 1 << 16; //64K

    /**
     * Keep track of the current subject document
     * that is being processed to detect new entities.
     */
    private RDFDocument currentDoc = null;

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

      while (true) { // Keep reading file
        try {
          String line = bufferedReader.readLine();

          if (line == null) {
            // End of file, we set doc to the current doc.
            // If the file was empty, currentDoc would be null,
            // and if we were processing a subject document, it will
            // be returned correctly
            doc = currentDoc;

            // Finish processing
            atEOF = true;
            break;
          } else if (line.startsWith("#") || line.equals("")) {
            // Ignore comments and empty lines
            continue;
          } else {
            // Process the line
            String[] triple = line.split(RDFDocument.TRIPLE_SPLITTER);

            if (triple.length != 4) {
              // Ignore invalid lines
              LOG.warn("Ignoring invalid NT triple line: {}", line);
              continue;
            }

            if (currentDoc == null) {
              // First line with a valid triple, create a new doc
              currentDoc = new RDFDocument(triple[0], triple[1], triple[2]);
              continue;
            }

            if (triple[0].equals(currentDoc.getSubject())) {
              // Still processing the same entity subject
              currentDoc.addPredicateAndValue(triple[1], triple[2]);
            } else {
              // Encountered a new subject. We set doc to return to
              // the current doc, which is the previous entity that was being processed.
              doc = currentDoc;

              // Set the current document to a new document with the new subject
              currentDoc = new RDFDocument(triple[0], triple[1], triple[2]);

              // Break from loop to return doc
              break;
            }
          }
        } catch (IOException e) {
          LOG.error("Cannot read next line from reader");
        }
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
