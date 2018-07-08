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

import io.anserini.document.WikipediaParagraph;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wikiclean.WikiClean;
import org.wikiclean.WikiClean.WikiLanguage;
import org.wikiclean.WikiCleanBuilder;
import org.wikiclean.WikipediaBz2DumpInputStream;

/**
 * Class representing an instance of a Wikipedia paragraphs collection. Note that Wikipedia dumps often come
 * as a single bz2 file. Since a collection is assumed to be in a directory, place the bz2 file in
 * a directory prior to indexing.
 */
public class WikipediaParagraphsCollection extends Collection<WikipediaParagraph> {

  public class FileSegment extends Collection<WikipediaParagraph>.FileSegment {
    private final WikipediaBz2DumpInputStream stream;
    private final WikiClean cleaner;
    private final Pattern CONTAINS_PERIOD;
    private final Pattern SINGLE_WORD_WITH_PERIOD;
    private ArrayList<String> bufferedParagraphs = null;
    private int currentParagraphIdx = 0;
    private String title = "";

    protected FileSegment(Path path) throws IOException {
      this.path = path;

      stream = new WikipediaBz2DumpInputStream(path.toString());
      cleaner = new WikiCleanBuilder()
          .withLanguage(WikiLanguage.EN).withTitle(false)
          .withFooter(false).build();

      CONTAINS_PERIOD = Pattern.compile("\\.");
      SINGLE_WORD_WITH_PERIOD = Pattern.compile("\\w+\\.");
    }

    @Override
    public WikipediaParagraph next() {
      if (bufferedParagraphs != null && currentParagraphIdx < bufferedParagraphs.size()) {
        String currentParagraph = bufferedParagraphs.get(currentParagraphIdx);
        WikipediaParagraph result = new WikipediaParagraph(title, currentParagraphIdx, currentParagraph);
        currentParagraphIdx++;
        return result;
      }

      try {
        String page;
        String s;

        // Advance to the next valid page.
        while ((page = stream.readNext()) != null) {
          // See https://en.wikipedia.org/wiki/Wikipedia:Namespace
          if (page.contains("<ns>") && !page.contains("<ns>0</ns>")) {
            continue;
          }

          s = cleaner.clean(page);

          // Skip redirects
          if (s.startsWith("#REDIRECT")) {
            continue;
          }

          String[] parts = s.split("\\n+");
          bufferedParagraphs = new ArrayList<>();
          for (String p : parts) {
            // Heuristic is that parts without periods ('.') or has one word followed by a period is a section header and should not be indexed.
            if (CONTAINS_PERIOD.matcher(p).find() && !SINGLE_WORD_WITH_PERIOD.matcher(p).matches()) {
              bufferedParagraphs.add(p);
            }
          }
          currentParagraphIdx = 0;

          if (bufferedParagraphs.isEmpty()) {
            continue;
          }

          // If we've gotten here, it means that we've advanced to the next "valid" article.
          title = cleaner.getTitle(page).replaceAll("\\n+", " ");
          currentParagraphIdx++;
          return new WikipediaParagraph(title, 0, bufferedParagraphs.get(0));
        }

      } catch (IOException e) {
        e.printStackTrace();
      }

      // If we've fall through here, we've either encountered an exception or we've reached the end
      // of the underlying stream.
      atEOF = true;
      return null;
    }
  }

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

}
