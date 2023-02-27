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

package io.anserini.util;

import io.anserini.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.te.TeluguAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer;
import io.anserini.index.IndexCollection;
import io.anserini.search.topicreader.TopicReader;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Utility to dump out query terms that have analyzed with Anserini's default Lucene Analyzer, DefaultEnglishAnalyzer
 * with Porter stemming. Query terms are taken from the "title" of topics. Output is a TSV file, with (topic id,
 * analyzed query) tuples; the analyzed query comprises space-delimited tokens.
 */
public class DumpAnalyzedQueries {

  public static class Args {
    @Option(name = "-topicreader", metaVar = "[class]", usage = "topic reader")
    public String topicReader = null;

    @Option(name = "-topics", metaVar = "[file]", required = true, usage = "queries")
    public Path topicsFile;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "queries")
    public String output;

    @Option(name = "-language", usage = "Analyzer Language")
    public String language = "en";

  }

  @SuppressWarnings("unchecked")
  public static void main(String[] argv) throws IOException {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));
    Analyzer analyzer;

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    // Read the analyzer from the command line, default is en 
    if (args.language.equals("ar")) {
      analyzer = new ArabicAnalyzer();
      System.out.println("Language: ar");
    } else if (args.language.equals("bn")) {
      analyzer = new BengaliAnalyzer();
      System.out.println("Language: bn");
    } else if (args.language.equals("da")) {
      analyzer = new DanishAnalyzer();
      System.out.println("Language: da");
    } else if (args.language.equals("de")) {
      analyzer = new GermanAnalyzer();
      System.out.println("Language: de");
    } else if (args.language.equals("es")) {
      analyzer = new SpanishAnalyzer();
      System.out.println("Language: es");
    } else if (args.language.equals("fa")) {
      analyzer = new PersianAnalyzer();
      System.out.println("Language: fa");
    } else if (args.language.equals("fi")) {
      analyzer = new FinnishAnalyzer();
      System.out.println("Language: fi");
    } else if (args.language.equals("fr")) {
      analyzer = new FrenchAnalyzer();
      System.out.println("Language: fr");
    } else if (args.language.equals("hi")) {
      analyzer = new HindiAnalyzer();
      System.out.println("Language: hi");
    } else if (args.language.equals("hu")) {
      analyzer = new HungarianAnalyzer();
      System.out.println("Language: hu");
    } else if (args.language.equals("id")) {
      analyzer = new IndonesianAnalyzer();
      System.out.println("Language: id");
    } else if (args.language.equals("it")) {
      analyzer = new ItalianAnalyzer();
      System.out.println("Language: it");
    } else if (args.language.equals("ja")) {
      analyzer = new JapaneseAnalyzer();
      System.out.println("Language: ja");
    } else if (args.language.equals("ko")) {
      analyzer = new CJKAnalyzer();
      System.out.println("Language: ko");
    } else if (args.language.equals("nl")) {
      analyzer = new DutchAnalyzer();
      System.out.println("Language: nl");
    } else if (args.language.equals("no")) {
      analyzer = new NorwegianAnalyzer();
      System.out.println("Language: no");
    } else if (args.language.equals("pl")) {
      analyzer = new MorfologikAnalyzer();
      System.out.println("Language: pl");
    } else if (args.language.equals("pt")) {
      analyzer = new PortugueseAnalyzer();
      System.out.println("Language: pt");
    } else if (args.language.equals("ru")) {
      analyzer = new RussianAnalyzer();
      System.out.println("Language: ru");
    } else if (args.language.equals("sv")) {
      analyzer = new SwedishAnalyzer();
      System.out.println("Language: sv");
    } else if (args.language.equals("te")) {
      analyzer = new TeluguAnalyzer();
      System.out.println("Language: te");
    } else if (args.language.equals("th")) {
      analyzer = new ThaiAnalyzer();
      System.out.println("Language: th");
    } else if (args.language.equals("tr")) {
      analyzer = new TurkishAnalyzer();
      System.out.println("Language: tr");
    } else if (args.language.equals("uk")) {
      analyzer = new UkrainianMorfologikAnalyzer();
      System.out.println("Language: uk");
    } else if (args.language.equals("zh")) {
      analyzer = new CJKAnalyzer();
      System.out.println("Language: zh");
    } else if (args.language.equals("sw") || args.language.equals("te")) {
      analyzer = new WhitespaceAnalyzer();
      System.out.println("Whitespace Analyzer");
    } else {
      // Default to English with Porter stemmer
      System.out.println("English/Porter: Default Analyzer");
      analyzer = IndexCollection.DEFAULT_ANALYZER;
    }

    TopicReader<?> tr;
    try {
      // Can we infer the TopicReader?
      Class<? extends TopicReader> clazz = TopicReader.getTopicReaderClassByFile(args.topicsFile.toString());
      if (clazz != null) {
        System.out.println(String.format("Inferring %s has TopicReader class %s.", args.topicsFile, clazz));
      } else {
        // If not, get it from the command-line argument.
        System.out.println(String.format("Unable to infer TopicReader class for %s, using specified class %s.",
            args.topicsFile, args.topicReader));
        if (args.topicReader == null) {
          System.err.println("Must specify TopicReader with -topicreader!");
          System.exit(-1);
        }

        clazz = (Class<? extends TopicReader>) Class.forName(
            "io.anserini.search.topicreader." + args.topicReader + "TopicReader");
      }

      tr = (TopicReader<?>) clazz.getConstructor(Path.class).newInstance(args.topicsFile);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Unable to load TopicReader: " + args.topicReader);
    }

    SortedMap<?, Map<String, String>> topics = tr.read();

    FileOutputStream out = new FileOutputStream(args.output);
    for (Map.Entry<?, Map<String, String>> entry : topics.entrySet()) {
      List<String> tokens = AnalyzerUtils.analyze(analyzer, entry.getValue().get("title"));
      out.write((entry.getKey() + "\t" + StringUtils.join(tokens, " ") + "\n").getBytes());
    }
    out.close();

    System.out.println("Done!");
  }
}
