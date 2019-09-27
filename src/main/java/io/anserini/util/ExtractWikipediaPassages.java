/**
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;
import io.anserini.collection.WikipediaCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.wikiclean.WikiClean;
import org.wikiclean.WikiClean.WikiLanguage;
import org.wikiclean.WikipediaArticlesDump;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtractWikipediaPassages {
  private static final Logger LOG = LogManager.getLogger(WikipediaCollection.class);

  private static final class Args {
    @Option(name = "-input", metaVar = "[path]", required = true, usage = "input path")
    File input;

    @Option(name = "-output", metaVar = "[path]", required = true, usage = "output path")
    String output;

    @Option(name = "-lang", metaVar = "[lang]", required = true, usage = "language")
    String lang;
  }

  public static void main(String[] argv) throws Exception {
    final Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.exit(-1);
    }

    LOG.info("Input: " + args.input);
    LOG.info("Output: " + args.output);
    LOG.info("Lang: " + args.lang);

    // We just support en and zh currently.
    WikiLanguage wikiLang;
    if (args.lang.equals("zh")) {
      wikiLang = WikiLanguage.ZH;
    } else {
      wikiLang = WikiLanguage.EN;
    }

    final WikiClean cleaner = new WikiClean.Builder().withLanguage(wikiLang)
        .withTitle(false).withFooter(false).build();

    PrintWriter writer = new PrintWriter(args.output, "UTF-8");
    WikipediaArticlesDump wikipedia = new WikipediaArticlesDump(args.input);
    AtomicInteger articleCnt = new AtomicInteger();

    wikipedia.stream()
        .filter(page -> !page.contains("<ns>") || page.contains("<ns>0</ns>"))
        .forEach(page -> {
          String s = cleaner.clean(page);
          if (s.startsWith("#REDIRECT")) return;

          String title = cleaner.getTitle(page).replaceAll("\\n+", " ");
          if ( wikiLang.equals(WikiLanguage.ZH)) {
            int cnt = 0;
            // Yes, what a janky sentence splitter.
            // TODO: implement a better one.
            for (String sentence : s.split("。")) {
              sentence = sentence.trim() + "。"; // Trim and add the punctuation back in since we split on it.
              sentence = sentence.replaceAll("\\n+", " ");
              writer.println(String.format("%s.%04d\t%s %s", title, cnt, title, sentence));

              cnt++;
            }
          } else {
            int cnt = 0;
            Reader reader = new StringReader(s);
            DocumentPreprocessor dp = new DocumentPreprocessor(reader);
            for (List<HasWord> sentence : dp) {
              writer.print(String.format("%s.%04d\t%s\n", title, cnt, SentenceUtils.listToString(sentence)));

              cnt++;
            }
          }

          if (articleCnt.incrementAndGet() % 10000 == 0 ) {
            LOG.info(articleCnt.get() + " articles processed.");
          }
        });

    writer.close();
  }

}
