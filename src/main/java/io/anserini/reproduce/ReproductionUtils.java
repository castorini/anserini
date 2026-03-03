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

package io.anserini.reproduce;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReproductionUtils {
  private static final Logger LOG = LogManager.getLogger(ReproductionUtils.class);

  private ReproductionUtils() {}

  public static InputStream loadResourceStream(String resourceName, Class<?> fallbackClass) throws IOException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null) {
      classLoader = fallbackClass.getClassLoader();
    }

    Enumeration<URL> resources = classLoader.getResources(resourceName);
    if (!resources.hasMoreElements()) {
      throw new IllegalArgumentException("Missing regression resource: " + resourceName);
    }

    URL firstMatch = resources.nextElement();
    if (resources.hasMoreElements()) {
      LOG.warn("Multiple regression resources found for {}; using {}", resourceName, firstMatch);
    }
    return new BufferedInputStream(firstMatch.openStream());
  }
}
