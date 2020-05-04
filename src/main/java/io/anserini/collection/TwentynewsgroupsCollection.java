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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.NoSuchElementException;

/**
 * An instance of the <a href="http://people.csail.mit.edu/jrennie/20Newsgroups">20 Newsgroup by date</a>.
 * The collection contains 18846 documents, 20 different news sets,each corresponding to a different topic. 
 * stored in plain text format. The collection is 14MB compressed, 35.9MB uncompressed.
 */
public class TwentynewsgroupsCollection extends DocumentCollection<TwentynewsgroupsCollection.Document> {

  public TwentynewsgroupsCollection(Path path){
    this.path = path;
  }

  @Override
  public FileSegment<TwentynewsgroupsCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file containing one document from the <a href="http://people.csail.mit.edu/jrennie/20Newsgroups">20 Newsgroup by date</a>.
   * The file Name which is the Id of that document.
   */
  public static class Segment extends FileSegment<Document> {
    private String fileName;
    private String ID;

    public Segment(Path path) throws IOException {
      super(path);
      this.fileName = path.toString();
      String[] str_path = fileName.split("/");
      this.ID = str_path[str_path.length-1];
      this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileName), "utf-8"));
    }

    @Override
    public void readNext() throws IOException {
      String record;
      String From = "";
      String Subject = "";
      String Keywords = "";
      String Organization = "";
      String s = "";
      while ((record = bufferedReader.readLine())!=null){
            String parts[] = record.split(" ", 2);
            if (record.startsWith("From: ")){     
                From = parts[1];
            }else if (record.startsWith("Subject: ")){
                Subject = parts[1];
            }else if (record.startsWith("Keywords: ")){
                Keywords = parts[1];
            }else if (record.startsWith("Organization: ")){
                Organization = parts[1];
            }
            if (s!=""){
                s = s+"\n"+record;
            }else{
                s = record;
            }
      }
      if (s == "") {
            throw new NoSuchElementException();
      }
      this.bufferedRecord = new TwentynewsgroupsCollection.Document(this.ID,From,Subject,Keywords,Organization,s);
    }

  }
  /**
   * A document from the <a href="http://people.csail.mit.edu/jrennie/20Newsgroups">20 Newsgroup by date</a>.
   */
  public static class Document implements SourceDocument {
    protected String Id;
    protected String From;
    protected String Subject;
    protected String Keywords;
    protected String Organization;
    protected String Content;

    public Document(String id,String From,String Subject,String Keywords,String Organization, String contents) {
      this.Id = id;
      this.From = From;
      this.Subject = Subject;
      this.Keywords = Keywords;
      this.Organization = Organization;
      this.Content = contents;
    }

    @Override
    public String id() {
      return Id;
    }

    public String From() {
      return From;
    }

    public String Subject() {
      return Subject;
    }

    public String Keywords() {
      return Keywords;
    }

    public String Organization() {
      return Organization;
    }

    @Override
    public String contents() {
      return Content;
    }

    @Override
    public String raw() {
      return Content;
    }

    @Override
    public boolean indexable() {
      return true;
    }
  }
}
