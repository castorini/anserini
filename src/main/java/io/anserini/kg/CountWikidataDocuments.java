/*
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

package io.anserini.kg;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;

import java.util.concurrent.TimeUnit;

/**
 * Class for counting documents from a Wikidata dump. Illustrates usage of Wikidata tools.
 */
public class CountWikidataDocuments {
  private static final Logger LOG = LogManager.getLogger(CountWikidataDocuments.class);

  static final class Args {
    @Option(name = "-input", metaVar = "[path]", required = true, usage = "path to dump file")
    private String input;
  }

  public static void main(String[] args) throws Exception {
    Args dumpArgs = new Args();
    CmdLineParser parser = new CmdLineParser(dumpArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ LookupFreebaseNodes.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    // This code is adapted from https://github.com/Wikidata/Wikidata-Toolkit-Examples/blob/master/src/examples/LocalDumpFileExample.java
    DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
    CountingEntityDocumentProcessor processor = new CountingEntityDocumentProcessor();
    dumpProcessingController.registerEntityDocumentProcessor(processor,"wikidatawiki", true);

    final long start = System.nanoTime();
    LOG.info("Starting to process dump...");

    MwLocalDumpFile dumpFile = new MwLocalDumpFile(dumpArgs.input);
    dumpProcessingController.processDump(dumpFile);

    LOG.info(processor.itemCount + " total items encountered");
    LOG.info(processor.lexemeCount + " total lexemes encountered");
    LOG.info(processor.propertyCount + " total properties encountered");
    long duration = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Dump processed in  " + DurationFormatUtils.formatDuration(duration, "HH:mm:ss"));
  }

  private static class CountingEntityDocumentProcessor implements EntityDocumentProcessor {
    public int itemCount = 0;
    public int lexemeCount = 0;
    public int propertyCount = 0;

    // Items are Entities that are typically represented by a Wikipage.
    // See https://www.mediawiki.org/wiki/Wikibase/DataModel#Items
    public void processItemDocument​(ItemDocument itemDocument) {
      itemCount++;
      if (itemCount % 1000000 == 0) {
        LOG.info(itemCount + " items encountered");
      }
    }

    // A Lexeme is a lexical element of a language, such as a word, a phrase, or a prefix.
    // See https://www.wikidata.org/wiki/Wikidata:Lexicographical_data/Documentation
    public void processLexemeDocument​(LexemeDocument lexemeDocument) {
      lexemeCount++;
      if (lexemeCount % 1000000 == 0) {
        LOG.info(lexemeCount + " lexemes encountered");
      }
    }

    // Properties are Entities that describe a relationship between Items (or other Entities) and Values of the property.
    // See https://www.mediawiki.org/wiki/Wikibase/DataModel#Properties
    public void processPropertyDocument​(PropertyDocument propertyDocument) {
      propertyCount++;
      if (propertyCount % 1000000 == 0) {
        LOG.info(propertyCount + " properties encountered");
      }
    }
  }
}