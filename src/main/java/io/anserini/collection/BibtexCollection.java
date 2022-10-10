/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.Key;
import org.jbibtex.ObjectResolutionException;
import org.jbibtex.ParseException;
import org.jbibtex.StringValue;
import org.jbibtex.TokenMgrException;
import org.jbibtex.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * A BibTex document collection.
 */
public class BibtexCollection extends DocumentCollection<BibtexCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(BibtexCollection.class);

  public BibtexCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".bib"));
  }

  public BibtexCollection() {
  }

  @Override
  public FileSegment<BibtexCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<BibtexCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment(bufferedReader);
  }

  /**
   * A file in a Bibtex collection, typically containing multiple documents.
   */
  public static class Segment extends FileSegment<BibtexCollection.Document> {
    private Iterator<Map.Entry<Key, BibTeXEntry>> iterator = null; // iterator for JSON document array
    private BibTeXDatabase database;
    private String rawContent = null; // raw content from buffered string

    public Segment(Path path) throws IOException {
      super(path);
      bufferedReader = new BufferedReader(new FileReader(path.toString()));
      BibTeXParser bibtexParser;
      try {
        bibtexParser = new BibTeXParser();
      } catch (TokenMgrException | ParseException e) {
        LOG.error("Error: Could not initialize BibTeX parser" + e.getMessage());
        throw new IOException(e);
      }
      try {
        database = bibtexParser.parse(bufferedReader);
      } catch (ParseException | TokenMgrException | ObjectResolutionException e) {
        LOG.error("Error: Could not parse BibTeX" + e.getMessage());
        throw new IOException(e);
      }
      Map<Key, BibTeXEntry> entryMap = database.getEntries();
      iterator = entryMap.entrySet().iterator();
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
      rawContent = bufferedReader.lines().collect(Collectors.joining("\n"));
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (rawContent != null) {
        bufferedRecord = new BibtexCollection.Document(rawContent);
        rawContent = null;
      } else {
        if (iterator.hasNext()) {
          Map.Entry<Key, BibTeXEntry> entry = iterator.next();
          bufferedRecord = new BibtexCollection.Document(entry);
        } else {
          throw new NoSuchElementException("Reached end of BibtexDatabase Entries iterator");
        }
      }
    }
  }

  /**
   * A document in a Bibtex collection.
   */
  public static class Document implements SourceDocument {
    private String id;
    private String contents;
    private String type;
    private BibTeXEntry bibtexEntry;

    public Document(Map.Entry<Key, BibTeXEntry> entry) {
      id = entry.getKey().toString();
      Map<Key, Value> bibtexFields = entry.getValue().getFields();
      String doctitle = bibtexFields.getOrDefault(new Key("title"), new StringValue("", StringValue.Style.QUOTED)).toUserString();
      String docAbstract = bibtexFields.getOrDefault(new Key("abstract"), new StringValue("", StringValue.Style.QUOTED)).toUserString();
      contents = doctitle + ". " + docAbstract;
      type = entry.getValue().getType().toString();
      bibtexEntry = entry.getValue();
    }

    public Document(String rawContent) {
      contents = rawContent;
    }

    @Override
    public String id() {
      return id;
    }

    public String type() {
      return type;
    }

    @Override
    public String contents() {
      return contents;
    }

    @Override
    public String raw() {
      return contents;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    public BibTeXEntry bibtexEntry() {
      return bibtexEntry;
    }
  }
}