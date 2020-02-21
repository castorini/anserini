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

package io.anserini.collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbibtex.BibTeXParser;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.Value;
import org.jbibtex.ObjectResolutionException;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

/**
 * A BibTex document collection.
 */
public class BibtexCollection extends DocumentCollection<BibtexCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(BibtexCollection.class);

  public BibtexCollection(Path path){
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".bib"));
  }

  @Override
  public FileSegment<BibtexCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file in a Bibtex collection, typically containing multiple documents.
   */
  public static class Segment extends FileSegment<BibtexCollection.Document> {
    private Iterator<Map.Entry<Key, BibTeXEntry>> iterator = null; // iterator for JSON document array
    private BibTeXDatabase database;

    protected Segment(Path path) throws IOException {
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
      LOG.warn(database.getStrings().toString());
      iterator = entryMap.entrySet().iterator();
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (iterator.hasNext()) {
        Map.Entry<Key, BibTeXEntry> entry = iterator.next();
        StringJoiner construct = new StringJoiner(", ");
        for (Map.Entry<Key, Value> e : entry.getValue().getFields().entrySet()) {
          construct.add(e.getKey() + "= " + e.getValue().toUserString());
        }
        String bibtexString = construct.toString();
        bufferedRecord = new BibtexCollection.Document(entry, bibtexString);
      } else {
        throw new NoSuchElementException("Reached end of BibtexDatabase Entries iterator");
      }
    }
  }

  /**
   * A document in a Bibtex collection.
   */
  public static class Document implements SourceDocument {
    private String id;
    private String contents;
    private BibTeXEntry bibtexEntry;

    public Document(Map.Entry<Key, BibTeXEntry> entry, String bibtexString) {
      id = entry.getKey().toString();
      contents = bibtexString;
      bibtexEntry = entry.getValue();
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String content() {
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