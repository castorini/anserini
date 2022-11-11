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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.NoSuchElementException;

/**
 * An instance of the <a href="http://people.csail.mit.edu/jrennie/20Newsgroups">20 Newsgroup</a>.
 * The collection contains 18846 documents, 20 different news sets,each corresponding to a different topic.
 * The dataset version we use is the 20news-by-date one which is sorted by date.
 * stored in plain text format. The collection is 14.5MB compressed, 35.9MB uncompressed.
 */
public class TwentyNewsgroupsCollection extends DocumentCollection<TwentyNewsgroupsCollection.Document> {

  public TwentyNewsgroupsCollection(Path path) {
    this.path = path;
  }

  public TwentyNewsgroupsCollection() {
  }

  @Override
  public FileSegment<TwentyNewsgroupsCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<TwentyNewsgroupsCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment(bufferedReader);
  }

  /**
   * A file containing one document from the <a href="http://people.csail.mit.edu/jrennie/20Newsgroups">20 Newsgroup</a>.
   * The file Name which is the id of that document.
   */
  public static class Segment extends FileSegment<Document> {
    private String fileName;
    private String id;

    public Segment(Path path) throws IOException {
      super(path);
      this.fileName = path.toString();
      String[] str_path = fileName.split("/");
      this.id = str_path[str_path.length - 1];
      this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileName), "utf-8"));
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
    }

    @Override
    public void readNext() throws IOException {
      String record;
      String from = "";
      String subject = "";
      String keywords = "";
      String organization = "";
      String contents = "";

      while ((record = bufferedReader.readLine())!=null){
        String parts[] = record.split(" ", 2);

        if (record.startsWith("From: ")) {
          from = parts[1];
        } else if (record.startsWith("Subject: ")) {
          subject = parts[1];
        } else if (record.startsWith("Keywords: ")) {
          keywords = parts[1];
        } else if (record.startsWith("Organization: ")) {
          organization = parts[1];
        }

        if (contents!="") {
          contents = contents+"\n"+record;
        } else {
          contents = record;
        }
      }
      
      if (contents == "") {
        throw new NoSuchElementException();
      }
      this.bufferedRecord = new TwentyNewsgroupsCollection.Document(this.id,from,subject,keywords,organization,contents);
    }

  }
  /**
   * A document from the <a href="http://people.csail.mit.edu/jrennie/20Newsgroups">20 Newsgroup</a>.
   */
  public static class Document implements SourceDocument {
    protected String id;
    protected String from;
    protected String subject;
    protected String keywords;
    protected String organization;
    protected String contents;

    public Document(String id,String from,String subject,String keywords,String organization, String contents) {
      this.id = id;
      this.from = from;
      this.subject = subject;
      this.keywords = keywords;
      this.organization = organization;
      this.contents = contents;
    }

    @Override
    public String id() {
      return id;
    }

    public String from() {
      return from;
    }

    public String subject() {
      return subject;
    }

    public String keywords() {
      return keywords;
    }

    public String organization() {
      return organization;
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
  }
}
