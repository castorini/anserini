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
import java.io.IOException;

/**
 * A CAR dataset paragraph. The para_id serves as the id.
 */
public class CAR18Document implements SourceDocument {
  private final String paraID;
  private final String paragraph;

  public CAR18Document(String paraID, String paragraph) {
    this.paraID = paraID;
    this.paragraph = paragraph;

  }

  @Override
  public CAR18Document readNextRecord(BufferedReader bRdr) throws IOException {
    return null;
  }

  @Override
  public String id() {
    return paraID;
  }

  @Override
  public String content() {
    return paragraph;
  }

  @Override
  public boolean indexable() {
    return true;
  }
}


