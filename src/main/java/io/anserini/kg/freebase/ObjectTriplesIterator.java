package io.anserini.kg.freebase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

/**
 * Class for reading the Freebase dump in N-Triples RDF format. Provides an iterator over
 * {@link ObjectTriples} objects.
 */
public class ObjectTriplesIterator implements Iterator<ObjectTriples>, Closeable {
  private static final Logger LOG = LogManager.getLogger(ObjectTriplesIterator.class);
  private static final int BUFFER_SIZE = 1 << 16; //64K

  private BufferedReader bufferedReader;
  private boolean atEOF = false;

  private ObjectTriples currentDoc = null;

  public ObjectTriplesIterator(Path path) throws IOException {
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
  public ObjectTriples next() {
    ObjectTriples doc = null;

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
          String[] triple = line.split(ObjectTriples.TRIPLE_SPLITTER);

          if (triple.length != 4) {
            // Ignore invalid lines
            LOG.warn("Ignoring invalid NT triple line: {}", line);
            continue;
          }

          if (currentDoc == null) {
            // First line with a valid triple, create a new doc
            currentDoc = new ObjectTriples(triple[0], triple[1], triple[2]);
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
            currentDoc = new ObjectTriples(triple[0], triple[1], triple[2]);

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

  @Override
  public boolean hasNext() {
    return !atEOF;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws IOException {
    atEOF = false;
    if (bufferedReader != null) {
      bufferedReader.close();
    }
  }
}
