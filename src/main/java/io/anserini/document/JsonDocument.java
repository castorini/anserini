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
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * Here we actually read the whole json file at once.
 * If there are multiple JSON objects we use a global index i to track the progress.
 *
 */
public class JsonDocument implements SourceDocument {
  protected String id;
  protected String contents;
  private JsonElement raw;
  private int i;

  public JsonDocument(String path) throws FileNotFoundException {
    JsonParser parser = new JsonParser();
    try {
      raw = parser.parse(new BufferedReader(new FileReader(path))).getAsJsonArray();
    } catch (IllegalStateException e1) {
      try {
        raw = parser.parse(new BufferedReader(new FileReader(path))).getAsJsonObject();
      } catch (IllegalStateException e2) {
        raw = parser.parse(new BufferedReader(new FileReader(path))).getAsJsonNull();
      }
    }
    i = 0;
  }

  @Override
  public JsonDocument readNextRecord(BufferedReader bRdr) throws IOException {
    if (raw.isJsonArray() && i < raw.getAsJsonArray().size()) {
      JsonObject o = raw.getAsJsonArray().get(i).getAsJsonObject();
      id = o.get("id").getAsString();
      contents = o.get("contents").getAsString();
      i++;
      return this;
    } else if (raw.isJsonObject()) {
      id = raw.getAsJsonObject().get("id").getAsString();
      contents = raw.getAsJsonObject().get("contents").getAsString();
      return this;
    } else {
      // try to read one JSON Object per line
      String line;
      JsonParser parser = new JsonParser();
      while ((line = bRdr.readLine()) != null) {
        JsonObject o = parser.parse(line).getAsJsonObject();
        id = o.get("id").getAsString();
        contents = o.get("contents").getAsString();
        return this;
      }
    }
    return null;
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
