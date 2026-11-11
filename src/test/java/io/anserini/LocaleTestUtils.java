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

package io.anserini;

import java.util.List;
import java.util.Locale;

/** Locales that exercise decimal commas and two distinct non-ASCII digit systems. */
public final class LocaleTestUtils {
  public static final List<Locale> LOCALES = List.of(
      Locale.forLanguageTag("mzn-Arab-IR"), Locale.GERMANY,
      Locale.forLanguageTag("hi-IN-u-nu-deva"));

  private LocaleTestUtils() {}
}
