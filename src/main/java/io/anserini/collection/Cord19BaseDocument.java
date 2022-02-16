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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public abstract class Cord19BaseDocument implements SourceDocument {
  private static final Logger LOG = LogManager.getLogger(Cord19BaseDocument.class);
  protected String id;
  protected String content;
  protected String raw;
  protected CSVRecord record;

  protected final String getFullTextJson(String basePath) {
    String fullTextPath = null;
    if (!record.get("pmc_json_files").isEmpty()) {
      fullTextPath = "/" + record.get("pmc_json_files").split(";")[0];
    } else if (!record.get("pdf_json_files").isEmpty()) {
      fullTextPath = "/" + record.get("pdf_json_files").split(";")[0];
    } else {
      return null;
    }

    String fullTextJson = null;
    try {
      fullTextJson = new String(Files.readAllBytes(
        Paths.get(basePath + fullTextPath)));
    } catch (IOException e) {
      LOG.error("Error parsing file at " + fullTextPath);
    }
    return fullTextJson;
  }

  protected final String getRecordJson() {
    String recordString = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      recordString = objectMapper.writeValueAsString(record.toMap());
    } catch (JsonProcessingException e) {
      LOG.error("Error writing record to JSON " + record.toString());
    }
    return recordString;
  }

  protected final String buildRawJson(String fullTextJson) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode rawJsonNode = mapper.createObjectNode();

    if (fullTextJson != null) {
      try {
        rawJsonNode = (ObjectNode) mapper.readTree(fullTextJson);
      } catch (Exception e) {
        fullTextJson = null;
      }
    }

    rawJsonNode.put("cord_uid", record.get("cord_uid"));
    rawJsonNode.put("has_full_text", fullTextJson != null);

    ObjectNode csvMetadaNode = mapper.createObjectNode();
    for (Map.Entry<String, String> entry : record.toMap().entrySet()) {
      csvMetadaNode.put(entry.getKey(), entry.getValue());
    }

    rawJsonNode.set("csv_metadata", csvMetadaNode);
    return rawJsonNode.toString();
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String contents() {
    return content;
  }

  @Override
  public String raw() {
    return raw;
  }

  @Override
  public boolean indexable() {
    return true;
  }

  public CSVRecord record() {
    return record;
  }
}

