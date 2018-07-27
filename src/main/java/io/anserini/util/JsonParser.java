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

package io.anserini.util;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class JsonParser {
  public static boolean isFieldAvailable(Object field) {
    if (field == null) {
      return false;
    }
    boolean isPresent;
    if (field.getClass() == OptionalLong.class) {
      isPresent = ((OptionalLong)field).isPresent();
    } else if (field.getClass() == OptionalDouble.class) {
      isPresent = ((OptionalDouble)field).isPresent();
    } else if (field.getClass() == OptionalInt.class) {
      isPresent = ((OptionalInt)field).isPresent();
    } else {
      isPresent = ((Optional)field).isPresent();
    }
    return isPresent;
  }
}
