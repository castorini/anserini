package io.anserini.kg;

/*
 * #%L
 * Wikidata Toolkit Examples
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.nio.file.Path;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;
import org.wikidata.wdtk.datamodel.interfaces.*;

/**
 * This class illustrates how to process local dumpfiles. It uses
 * {@link EntityTimerProcessor} to process a dump.
 *
 * @author Markus Damm
 *
 */
public class LocalDumpFileExample {

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

    DumpProcessingController dumpProcessingController = new DumpProcessingController(
        "wikidatawiki");
    // Note that the project name "wikidatawiki" is only for online access;
    // not relevant here.

    EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
    entityTimerProcessor.setReportInterval(10);
    dumpProcessingController.registerEntityDocumentProcessor(
        entityTimerProcessor, null, true);

    // Select local file (meta-data will be guessed):
    System.out.println();
    System.out
        .println("Processing a local dump file giving only its location");
    System.out
        .println("(meta-data like the date is guessed from the file name):");
    MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(dumpArgs.input);
    dumpProcessingController.registerEntityDocumentProcessor(new MyEntityDocumentProcessor(), "wikidatawiki", true);
    dumpProcessingController.processDump(mwDumpFile);

//    // Select local file and set meta-data:
//    System.out.println();
//    System.out
//        .println("Processing a local dump file with all meta-data set:");
//    mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON,
//        "20150815", "wikidatawiki");
//    dumpProcessingController.registerEntityDocumentProcessor(new MyEntityDocumentProcessor(), "wikidatawiki", true);
//    dumpProcessingController.processDump(mwDumpFile);

    entityTimerProcessor.close();
  }

  public static class MyEntityDocumentProcessor implements EntityDocumentProcessor {
    private int itemCount = 0;

    public void processItemDocument​(ItemDocument itemDocument) {
      //System.out.println(itemDocument.getEntityId() + "--" + itemDocument.findLabel("en"));
      itemCount++;
      if (itemCount % 100000 == 0) {
        System.out.println(itemCount + " items scanned");
      }
    }

    public void processLexemeDocument​(LexemeDocument lexemeDocument) {
      //System.out.println(lexemeDocument);
    }

    public void processPropertyDocument​(PropertyDocument propertyDocument) {
      //System.out.println(propertyDocument);
    }

  }

}