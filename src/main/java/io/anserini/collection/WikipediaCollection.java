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

import org.wikiclean.WikiClean;
import org.wikiclean.WikiClean.WikiLanguage;
import org.wikiclean.WikipediaArticlesDump;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * A Wikipedia collection.
 * Note that Wikipedia dumps come as a single <code>bz2</code> file. Since a collection is assumed
 * to be in a directory, place the <code>bz2</code> file in a directory prior to indexing.
 */
public class WikipediaCollection extends DocumentCollection<WikipediaCollection.Document> {

  public WikipediaCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".bz2"));
  }

  public WikipediaCollection() {
  }

  @Override
  public FileSegment<WikipediaCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<WikipediaCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment(bufferedReader);
  }

  /**
   * A collection of Wikipedia articles (note that Wikipedia dumps are distributed as a single file).
   */
  public static class Segment extends FileSegment<WikipediaCollection.Document> {
    private final Iterator<String> iter;
    private final WikiClean cleaner;
    private final String rawString;

    public Segment(Path path) throws IOException {
      super(path);
      iter = new WikipediaArticlesDump(new File(path.toString())).iterator();
      cleaner = new WikiClean.Builder()
          .withLanguage(WikiLanguage.EN).withTitle(false)
          .withFooter(false).build();
      rawString = null;
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
      iter = null;
      cleaner = new WikiClean.Builder()
          .withLanguage(WikiLanguage.EN).withTitle(false)
          .withFooter(false).build();
      rawString = bufferedReader.lines().collect(Collectors.joining("\n"));
    }

    @Override
    public void readNext() {
      String page;
      String s;
      String titleSeparator = ".\n";
      if (rawString != null) {
        String title = rawString.split(titleSeparator)[0];
        bufferedRecord = new Document(title, rawString);
      } else {
        // Advance to the next valid page.
        while (iter.hasNext()) {
          page = iter.next();

          // See https://en.wikipedia.org/wiki/Wikipedia:Namespace
          if (page.contains("<ns>") && !page.contains("<ns>0</ns>")) {
            continue;
          }

          s = cleaner.clean(page).replaceAll("\\n+", " ");
          // Skip redirects
          if (s.startsWith("#REDIRECT")) {
            continue;
          }

          // If we've gotten here, it means that we've advanced to the next "valid" article.
          String title = cleaner.getTitle(page).replaceAll("\\n+", " ");
          bufferedRecord = new Document(title, title + ".\n" + s);
          break;
        }
      }
    }
  }

  /**
   * A Wikipedia article. The article title serves as the id.
   */
  public static class Document implements SourceDocument {
    private final String title;
    private final String contents;

    public Document(String title, String contents) {
      this.title = title;
      this.contents = contents;
    }

    @Override
    public String id() {
      return title;
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
