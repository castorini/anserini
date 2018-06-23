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

package io.anserini.document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The format of snippet file is:
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
 * Please see the comment in front of {@link io.anserini.collection.SnippetsCollection} for more details
 *
 */
public class SnippetsDocument implements SourceDocument {
  protected String id;
  protected String contents;
  private JsonArray raw;
  private int i;

  public SnippetsDocument(String path) throws FileNotFoundException {
    JsonParser parser = new JsonParser();
    raw = parser.parse(new BufferedReader(new FileReader(path))).getAsJsonArray();
    i = 0;
  }

  @Override
  public SnippetsDocument readNextRecord(BufferedReader bRdr) {
    if (i < raw.size()) {
      JsonObject o = raw.get(i).getAsJsonObject();
      id = o.get("url").getAsString();
      contents = o.get("snippets").getAsString();
      i++;
      return this;
    }  else {
      return null;
    }
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
}
