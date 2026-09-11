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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ReproductionUtils {
  private static final Logger LOG = LogManager.getLogger(ReproductionUtils.class);

  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ROOT).withZone(ZoneId.systemDefault());

  private ReproductionUtils() {}

  public enum Status {
    OK, OKISH, FAIL
  }

  /** Absolute allowance for floating-point noise, independent of metric scale or display precision. */
  public static final double NUMERICAL_TOLERANCE = 1e-9;

  /** Exclusive OKish threshold when no tolerance is configured. */
  public static final double DEFAULT_OKISH_TOLERANCE = 0.0002;

  /**
   * Classifies scores after any metric-specific rounding performed by the caller. 
   * With {@code delta = abs(observed - expected)}, apply these checks in order:
   * <ul>
   *   <li><b>OK:</b> {@code delta <= max(tolerance, NUMERICAL_TOLERANCE)}, using zero for {@code null} tolerance in this calculation.</li>
   *   <li><b>Otherwise, OKish:</b> any of:
   *     <ul>
   *       <li>{@code observed > expected}.</li>
   *       <li>Tolerance is configured: {@code delta <= 1.5 * tolerance}.</li>
   *       <li>Tolerance is absent ({@code null}): {@code delta < DEFAULT_OKISH_TOLERANCE}.</li>
   *     </ul>
   *   </li>
   *   <li><b>Otherwise, FAIL.</b></li>
   * </ul>
   *
   * @param expected expected reference score, after any metric-specific rounding
   * @param observed observed score, after any metric-specific rounding
   * @param tolerance configured absolute tolerance, or {@code null} when absent; explicit zero disables the {@code delta < DEFAULT_OKISH_TOLERANCE} fallback
   * @return {@link Status#OK}, {@link Status#OKISH}, or {@link Status#FAIL} according to the decision tree above
   */
  public static Status compareScores(double expected, double observed, @Nullable Double tolerance) {
    double delta = Math.abs(observed - expected);
    if (delta <= Math.max(tolerance == null ? 0.0 : tolerance, NUMERICAL_TOLERANCE)) {
      return Status.OK;
    }
    if (observed > expected || (tolerance == null ? delta < DEFAULT_OKISH_TOLERANCE : delta <= 1.5 * tolerance)) {
      return Status.OKISH;
    }
    return Status.FAIL;
  }

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
    public static final String OKISH = BLUE + "[OKish] " + RESET;
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
    return String.format(Locale.ROOT, "%s%02d:%02d:%02d", duration.isNegative() ? "-" : "", hours, minutes, secs);
  }

  public static String constructRunfilePath(String indexName, String modelName, String topics) {
    return Paths.get(Constants.DEFAULT_RUNS_DIRECTORY,
        String.format(Locale.ROOT, "run.%s.%s.%s.txt", indexName, modelName, topics)).toString();
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

  public static List<String> listYamlConfigs(Class<?> clazz, String configDirectory) throws IOException, URISyntaxException {
    Path codePath = Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
    List<String> configs = new ArrayList<>();

    if (Files.isRegularFile(codePath) && codePath.toString().endsWith(".jar")) {
      String prefix = configDirectory + "/";
      try (JarFile jarFile = new JarFile(codePath.toFile())) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          String name = entry.getName();
          if (entry.isDirectory() || !name.startsWith(prefix) || !name.endsWith(".yaml")) {
            continue;
          }
          String configName = name.substring(prefix.length(), name.length() - ".yaml".length());
          if (!configName.contains("/")) {
            configs.add(configName);
          }
        }
      }
    } else {
      Path configDir = codePath.resolve(configDirectory);
      if (Files.exists(configDir)) {
        try (java.util.stream.Stream<Path> paths = Files.list(configDir)) {
          paths.filter(Files::isRegularFile)
              .map(path -> path.getFileName().toString())
              .filter(name -> name.endsWith(".yaml"))
              .map(name -> name.substring(0, name.length() - ".yaml".length()))
              .forEach(configs::add);
        }
      }
    }

    Collections.sort(configs);
    return configs;
  }
}
