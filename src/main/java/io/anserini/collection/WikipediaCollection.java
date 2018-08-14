/**
 * Anserini: An information retrieval toolkit built on Lucene
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
import org.wikiclean.WikiClean;
import org.wikiclean.WikiClean.WikiLanguage;
import org.wikiclean.WikiCleanBuilder;
import org.wikiclean.WikipediaBz2DumpInputStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * A Wikipedia collection.
 * Note that Wikipedia dumps come as a single <code>bz2</code> file. Since a collection is assumed
 * to be in a directory, place the <code>bz2</code> file in a directory prior to indexing.
 */
public class WikipediaCollection extends DocumentCollection
    implements SegmentProvider<WikipediaCollection.Document> {

  private static final Logger LOG = LogManager.getLogger(WikipediaCollection.class);

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".bz2"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
        allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

  public class FileSegment extends BaseFileSegment<Document> {
    private final WikipediaBz2DumpInputStream stream;
    private final WikiClean cleaner;

    protected FileSegment(Path path) throws IOException {
      this.path = path;
      stream = new WikipediaBz2DumpInputStream(path.toString());
      cleaner = new WikiCleanBuilder()
          .withLanguage(WikiLanguage.EN).withTitle(false)
          .withFooter(false).build();
    }

    @Override
    public void readNext() throws IOException {
      String page;
      String s;

      // Advance to the next valid page.
      while ((page = stream.readNext()) != null) {
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

  /**
   * A Wikipedia document. The article title serves as the id.
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
    public String content() {
      return contents;
    }

    @Override
    public boolean indexable() {
      return true;
    }
  }
}
