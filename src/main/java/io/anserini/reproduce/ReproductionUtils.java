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
import java.time.Duration;
import java.time.Instant;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReproductionUtils {
  private static final Logger LOG = LogManager.getLogger(ReproductionUtils.class);

  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault());

  private ReproductionUtils() {}

  public static final class Constants {
    // ANSI escape code for red text
    private static final String RED = "\u001B[91m";

    // ANSI escape code for blue text
    private static final String BLUE = "\u001B[94m";

    // ANSI escape code for green text
    private static final String GREEN = "\u001b[32m";

    // ANSI escape code to reset to the default text color
    private static final String RESET = "\u001B[0m";

    public static final String OK = GREEN + "   [OK] " + RESET;
    public static final String OKISH = BLUE + "  [OK*] " + RESET;
    public static final String FAIL = RED + " [FAIL] " + RESET;

    public static final String DEFAULT_RUNS_DIRECTORY = "runs";
    public static final String DEFAULT_LOGS_DIRECTORY = "logs";

    public static final String JAVA_PREFIX = "java -cp";
    public static final String JVM_ARGS = "-Xms512M -Xmx192G -Dslf4j.internal.verbosity=WARN --add-modules jdk.incubator.vector";

    private Constants() {}
  }

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

  public static String formatStartTime(Instant startTime) {
    return TIME_FORMATTER.format(startTime);
  }

  public static String formatEndTime(Instant endTime) {
    return TIME_FORMATTER.format(endTime);
  }

  public static String formatDuration(long durationMillis) {
    return formatDuration(Duration.ofMillis(durationMillis));
  }

  public static String formatDuration(Duration duration) {
    long seconds = Math.abs(duration.getSeconds());
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long secs = seconds % 60;
    return String.format("%s%02d:%02d:%02d", duration.isNegative() ? "-" : "", hours, minutes, secs);
  }

  public static String escapeJson(String value) {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\b", "\\b")
        .replace("\f", "\\f")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }
}
