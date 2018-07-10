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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * A document from the Washington Post collection.
 */
public class WashingtonPostDocument implements SourceDocument {
  private static final Logger LOG = LogManager.getLogger(WashingtonPostDocument.class);
  private static final String PATTERN = "<\\/?\\w+>";
  private final List<String> CONTENT_TYPE_TAG = Arrays.asList("sanitized_html", "tweet");

  // Required fields
  protected String id;
  protected long published_date;
  protected String content;

  @Override
  public SourceDocument readNextRecord(BufferedReader bufferedReader) throws IOException {
    String nextRecord = bufferedReader.readLine();
    if (nextRecord == null) {
      return null;
    }
    return parseRecord(nextRecord);
  }

  private SourceDocument parseRecord(String record) {

    StringBuilder builder = new StringBuilder();
    ObjectMapper mapper = new ObjectMapper();
    WashingtonPost recordObj = null;
    try {
      recordObj = mapper
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unrecognized properties
        .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
        .readValue(record, WashingtonPost.class);
    } catch (IOException e) {
      // For current dataset, we can make sure all record has unique id and
      //  published date. So we just simply log a warning and return null
      //  here in case future data may bring up this issue
      LOG.warn("No unique ID or published date for this record, ignored...");
      return null;
    }

    id = recordObj.id();
    published_date = recordObj.published_date();

    if (recordObj.contents != null && recordObj.contents.isPresent()) {
      for (WashingtonPost.Content contentObj : recordObj.contents.get()) {
        if (contentObj.type() != null && contentObj.type().isPresent() &&
            contentObj.content() != null && contentObj.content().isPresent()) {
          if (CONTENT_TYPE_TAG.contains(contentObj.type().get())) {
            builder.append(removeTags(contentObj.content().get().trim())).append("\n");
          }
        } else {
          LOG.warn("No type or content tag defined in Article " + id + ", ignored this file.");
        }
      }
    }

    content = builder.toString();
    return this;
  }

  private String removeTags(String content) {
    return content.replaceAll(PATTERN, " ");
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String content() {
    return content;
  }

  @Override
  public boolean indexable() {
    return true;
  }

  public long published_date() {
    return published_date;
  }
}
