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
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

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

  /**
   * Prints to stderr usage information for a command and list any required options.
   *
   * @param parser the args4j parser used by the calling application.
   * @param applicationClass the class that owns the options.
   * @param prioritizedOptions option names or aliases to print first; may be {@code null}.
   */
  public static void printUsage(CmdLineParser parser, Class<?> applicationClass, String[] prioritizedOptions) {
    System.err.printf("%nOptions for %s:%n%n", applicationClass.getSimpleName());

    List<String> required = new ArrayList<>();
    Map<String, String> optionAliasToCanonical = new LinkedHashMap<>();
    Map<String, String> optionsToHelp = new LinkedHashMap<>();

    Class<?> optionsClass = resolveOptionsClass(applicationClass);
    Object optionsInstance = instantiateOptionsClass(optionsClass);
    for (Field field : optionsClass.getDeclaredFields()) {
      Option option = field.getAnnotation(Option.class);
      if (option == null) {
        continue;
      }

      String names = buildOptionNames(option);
      String canonical = option.name();
      String usage = appendDefaultValue(option.usage(), field, optionsInstance);
      optionsToHelp.put(canonical, String.format("  %-24s %s", names, usage));
      optionAliasToCanonical.put(canonical, canonical);
      for (String alias : option.aliases()) {
        optionAliasToCanonical.put(alias, canonical);
      }

      if (option.required()) {
        required.add(canonical);
      }
    }

    Set<String> printed = new LinkedHashSet<>();
    if (prioritizedOptions != null) {
      for (String optionName : prioritizedOptions) {
        String canonical = optionAliasToCanonical.get(optionName);
        if (canonical == null || printed.contains(canonical)) {
          continue;
        }
        System.err.println(optionsToHelp.get(canonical));
        printed.add(canonical);
      }
    }

    for (Map.Entry<String, String> entry : optionsToHelp.entrySet()) {
      if (printed.contains(entry.getKey())) {
        continue;
      }
      System.err.println(entry.getValue());
      printed.add(entry.getKey());
    }

    if (!required.isEmpty()) {
      System.err.printf("%nRequired options: %s%n", required);
    }
  }

  private static Class<?> resolveOptionsClass(Class<?> applicationClass) {
    if (hasOptionDefinitions(applicationClass)) {
      return applicationClass;
    }

    for (Class<?> nested : applicationClass.getDeclaredClasses()) {
      if (hasOptionDefinitions(nested)) {
        return nested;
      }
    }
    return applicationClass;
  }

  private static boolean hasOptionDefinitions(Class<?> candidateClass) {
    for (Field field : candidateClass.getDeclaredFields()) {
      if (field.getAnnotation(Option.class) != null) {
        return true;
      }
    }
    return false;
  }

  private static String buildOptionNames(Option option) {
    String names = option.metaVar().isEmpty() ? option.name() : option.name() + " " + option.metaVar();
    if (option.aliases().length == 0) {
      return names;
    }

    if (option.metaVar().isEmpty()) {
      return String.format("%s, %s", option.name(), String.join(", ", Arrays.asList(option.aliases())));
    }

    StringBuilder aliasesWithMetaVar = new StringBuilder();
    for (int i = 0; i < option.aliases().length; i++) {
      if (i > 0) {
        aliasesWithMetaVar.append(", ");
      }
      aliasesWithMetaVar.append(option.aliases()[i]).append(" ").append(option.metaVar());
    }

    return String.format("%s, %s", names, aliasesWithMetaVar);
  }

  private static String appendDefaultValue(String usage, Field field, Object optionsInstance) {
    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
      return usage;
    }

    Object defaultValue = getDefaultValue(field, optionsInstance);
    if (defaultValue == null) {
      return usage;
    }

    return String.format("%s (default: %s)", usage, stringifyDefaultValue(defaultValue));
  }

  private static Object instantiateOptionsClass(Class<?> optionsClass) {
    try {
      return optionsClass.getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException | SecurityException e) {
      return null;
    }
  }

  private static Object getDefaultValue(Field field, Object optionsInstance) {
    if (optionsInstance == null) {
      return null;
    }

    try {
      if (!field.canAccess(optionsInstance)) {
        field.setAccessible(true);
      }
      return field.get(optionsInstance);
    } catch (ReflectiveOperationException | SecurityException e) {
      return null;
    }
  }

  private static String stringifyDefaultValue(Object defaultValue) {
    if (defaultValue instanceof String) {
      return "\"" + defaultValue + "\"";
    }
    if (defaultValue.getClass().isArray()) {
      int length = Array.getLength(defaultValue);
      String[] values = new String[length];
      for (int i = 0; i < length; i++) {
        values[i] = String.valueOf(Array.get(defaultValue, i));
      }
      return Arrays.toString(values);
    }
    return String.valueOf(defaultValue);
  }
}
