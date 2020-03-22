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

import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class CovidCollectionDocument implements SourceDocument {
  private static final Logger LOG = LogManager.getLogger(CovidCollectionDocument.class);
  protected String id;
  protected String content;
  protected String raw;
  protected CSVRecord record;

  protected final String getFullTextJson(CSVRecord record, String basePath) {
    if (!record.get("has_full_text").contains("True")) {
      return "";
    }

    String[] hashes = record.get("sha").split(";");
    String fullTextPath = "/" + record.get("full_text_file") + "/" + hashes[hashes.length - 1].strip() + ".json";
    try {
      String fullTextJson = new String(Files.readAllBytes(
        Paths.get(basePath + fullTextPath)));
      return fullTextJson;
    } catch (IOException e) {
      LOG.error("Error parsing file at " + fullTextPath);
    }

    return "";
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

  public String raw() {
    return raw;
  }

  public CSVRecord record() {
    return record;
  }
}

