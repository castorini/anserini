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
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

/**
 * Class for reading the Freebase dump in N-Triples RDF format. Allows a client to iterate over
 * nodes in the Freebase knowledge graph.
 */
public class Freebase implements Iterable<FreebaseNode>, Closeable {
  private static final Logger LOG = LogManager.getLogger(Freebase.class);
  private static final String TRIPLE_SPLITTER = "\t";
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
      private FreebaseNode currentNode = null;

      @Override
      public FreebaseNode next() {
        if (atEOF) {
          throw new NoSuchElementException();
        }

        FreebaseNode node;
        while (true) { // Keep reading file
          try {
            String line = bufferedReader.readLine();

            if (line == null) {
              // We've reached the end of file, set node to the current node.
              // If the file was empty, currentNode would be null, and if we were processing a
              // current node, it will be returned correctly.
              node = currentNode;
              atEOF = true;
              break;
            } else if (!line.startsWith("#") && !line.equals("")) {
              // Ignore comments and empty lines.
              String[] triple = line.split(TRIPLE_SPLITTER);

              if (triple.length != 4) {
                // Ignore invalid lines.
                LOG.warn("Ignoring invalid NT triple line: {}", line);
                continue;
              }

              if (currentNode == null) {
                // First line with a valid triple, create a new node.
                currentNode = new FreebaseNode(triple[0]).addPredicateValue(triple[1], triple[2]);
                continue;
              }

              if (triple[0].equals(currentNode.uri())) {
                // Same URI, still processing the same node.
                currentNode.addPredicateValue(triple[1], triple[2]);
              } else {
                // Encountered a new URI. We set node to return the current node, which is the
                // previous URI that was being processed.
                node = currentNode;

                // Set the current node to a new node with the new URI.
                currentNode = new FreebaseNode(triple[0]).addPredicateValue(triple[1], triple[2]);

                // Break from loop to return node.
                break;
              }
            }
          } catch (IOException e) {
            LOG.error("Cannot read next line from reader!");
          }
        }

        return node;
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
