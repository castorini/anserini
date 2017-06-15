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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

/**
 * Class for reading the Freebase dump in N-Triples RDF format. Allows a client to iterate over
 * nodes in the Freebase knowledge graph.
 */
public class Freebase implements Iterable<FreebaseNode>, Closeable {
  private static final Logger LOG = LogManager.getLogger(Freebase.class);
  private static final int BUFFER_SIZE = 1 << 16; //64K

  private final BufferedReader bufferedReader;

  public Freebase(Path path) throws IOException {
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
  public Iterator<FreebaseNode> iterator() {
    return new Iterator<FreebaseNode>() {
      private boolean atEOF = false;
      private FreebaseNode currentDoc = null;

      @Override
      public FreebaseNode next() {
        FreebaseNode doc = null;

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
              String[] triple = line.split(FreebaseNode.TRIPLE_SPLITTER);

              if (triple.length != 4) {
                // Ignore invalid lines
                LOG.warn("Ignoring invalid NT triple line: {}", line);
                continue;
              }

              if (currentDoc == null) {
                // First line with a valid triple, create a new doc
                currentDoc = new FreebaseNode(triple[0], triple[1], triple[2]);
                continue;
              }

              if (triple[0].equals(currentDoc.mid())) {
                // Still processing the same entity subject
                currentDoc.addPredicateAndValue(triple[1], triple[2]);
              } else {
                // Encountered a new subject. We set doc to return to
                // the current doc, which is the previous entity that was being processed.
                doc = currentDoc;

                // Set the current document to a new document with the new subject
                currentDoc = new FreebaseNode(triple[0], triple[1], triple[2]);

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
    };
  }

  @Override
  public void close() throws IOException {
    if (bufferedReader != null) {
      bufferedReader.close();
    }
  }

  public Stream<FreebaseNode> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }
}
