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

package io.anserini.cli;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public final class CliUtils {
  private CliUtils() {}

  public static void printUsage(CmdLineParser parser, Class<?> applicationClass, String[] prioritizedOptions) {
    System.err.printf("%nOptions for %s:%n%n", applicationClass.getSimpleName());

    List<String> required = new ArrayList<>();
    Map<String, String> optionAliasToCanonical = new LinkedHashMap<>();
    Map<String, String> optionNames = new LinkedHashMap<>();
    Map<String, String> optionUsage = new LinkedHashMap<>();

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
      optionNames.put(canonical, names);
      optionUsage.put(canonical, usage);
      optionAliasToCanonical.put(canonical, canonical);
      for (String alias : option.aliases()) {
        optionAliasToCanonical.put(alias, canonical);
      }

      if (option.required()) {
        required.add(canonical);
      }
    }

    int nameColumnWidth = 0;
    for (String names : optionNames.values()) {
      nameColumnWidth = Math.max(nameColumnWidth, names.length());
    }
    nameColumnWidth = Math.max(nameColumnWidth, 24);

    Set<String> printed = new LinkedHashSet<>();
    if (prioritizedOptions != null) {
      for (String optionName : prioritizedOptions) {
        String canonical = optionAliasToCanonical.get(optionName);
        if (canonical == null || printed.contains(canonical)) {
          continue;
        }
        System.err.printf("  %-" + nameColumnWidth + "s  %s%n", optionNames.get(canonical), optionUsage.get(canonical));
        printed.add(canonical);
      }
    }

    for (Map.Entry<String, String> entry : optionNames.entrySet()) {
      if (printed.contains(entry.getKey())) {
        continue;
      }
      String canonical = entry.getKey();
      System.err.printf("  %-" + nameColumnWidth + "s  %s%n", optionNames.get(canonical), optionUsage.get(canonical));
      printed.add(canonical);
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
    boolean omitBooleanMetaVar = isBooleanMetaVar(option.metaVar());
    String names = (option.metaVar().isEmpty() || omitBooleanMetaVar) ? option.name() : option.name() + " " + option.metaVar();
    if (option.aliases().length == 0) {
      return names;
    }

    if (option.metaVar().isEmpty() || omitBooleanMetaVar) {
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

  private static boolean isBooleanMetaVar(String metaVar) {
    if (metaVar == null || metaVar.isEmpty()) {
      return false;
    }
    String normalized = metaVar.replaceAll("[^A-Za-z]", "").toLowerCase();
    return "boolean".equals(normalized);
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
