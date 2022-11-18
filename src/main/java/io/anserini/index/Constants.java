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

package io.anserini.index;

public final class Constants {

  // This is the name of the field in the Lucene document where the docid is stored.
  public static final String ID = "id";

  // This is the name of the field in the Lucene document that should be searched by default.
  public static final String CONTENTS = "contents";

  // This is the name of the field in the Lucene document where the raw document is stored.
  public static final String RAW = "raw";

  // This is the name of the field in the Lucene document where the entity document is stored.
  public static final String ENTITY = "entity";

  // This is the name of the field in the Lucene document where the vector document is stored.
  public static final String VECTOR = "vector";
}
