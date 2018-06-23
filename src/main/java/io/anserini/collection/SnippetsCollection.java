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

import io.anserini.document.SnippetsDocument;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class serves as the collection of snippets crawled from commercial search engines --
 * Google, Yahoo and Bing. For format of the snippets directory is:
 * ── snippets
 *     ├── bing_201.json
 *     ├── bing_202.json
 *     ...
 *     ├── google_201.json
 *     ├── google_202.json
 *     ...
 *     ├── yahoo_201.json
 *     ├── yahoo_202.json
 *
 * where each file corresponds to one query topic (one qid).
 * Inside each file is JSON-formatted:
 *
 * [
 *   {
 *     "url": "https://mediaexperience.com/raspberry-pi-xbmc-with-raspbmc/",
 *     "id": "201_bing_1",
 *     "snippets": "In this complete tutorial, I will show you steps to set up Raspberry Pi 3 B+ powered Kodi and improvement tips that you cannot find in any other how-to."
 *   },
 *   {
 *     "url": "https://reolink.com/connect-raspberry-pi-to-ip-cameras/",
 *     "id": "201_bing_2",
 *     "snippets": "How can you connect Raspberry Pi to an IP camera? What do you need to make a Raspberry Pi IP camera viewer? Read it to build a Raspberry Pi security camera."
 *   },
 *   ...
 * ]
 *
 */
public class SnippetsCollection extends Collection<SnippetsDocument> {

  public class FileSegment extends Collection.FileSegment {
    protected FileSegment(Path path) throws IOException {
      dType = new SnippetsDocument(path.toString());
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".json"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
      allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }
}
