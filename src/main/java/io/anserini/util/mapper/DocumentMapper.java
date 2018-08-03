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

package io.anserini.util.mapper;

import io.anserini.collection.SourceDocument;
import io.anserini.util.MapCollections;

import java.util.MissingResourceException;

public abstract class DocumentMapper {

  protected MapCollections.Args args;

  public DocumentMapper(MapCollections.Args args) {
    this.args = args;
  }

  public boolean isCountDocumentMapper() {
    boolean isCountDocumentMapper;
    try {
      isCountDocumentMapper = this.getClass().equals(Class.forName("io.anserini.util.mapper.CountDocumentMapper"));
    } catch (ClassNotFoundException e) {
      throw new MissingResourceException("ClassNotFoundException", "CountDocumentMapper", "ClassNotFoundException");
    }
    return isCountDocumentMapper;
  }

  public abstract boolean process(SourceDocument d);

  public abstract void printResult(long durationMillis);
}
