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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.document.Document;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.index.Constants;
import io.anserini.index.IndexReaderUtils;
import io.anserini.search.SimpleSearcher;
import io.anserini.util.LoggingBootstrap;

public final class GetDocument {
  public static class Args {
    @Option(name = "--index", metaVar = "[path|name]", required = true, usage = "Path to Lucene index or prebuilt index name")
    public String index;

    @Option(name = "--docid", metaVar = "[docid]", usage = "Collection document id")
    public String docid;

    @Option(name = "--interactive", usage = "Read docids from stdin until user quits.")
    public Boolean interactive = false;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
      "--index", "--docid", "--interactive", "--help"};

  public static void main(String[] args) {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format("Error: %s", e.getMessage()));
      CliUtils.printUsage(parser, GetDocument.class, argsOrdering);
      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, GetDocument.class, argsOrdering);
      return;
    }

    if (parsedArgs.interactive && parsedArgs.docid != null) {
      System.err.println("Error: --interactive and --docid are mutually exclusive");
      return;
    }

    if (!parsedArgs.interactive && parsedArgs.docid == null) {
      System.err.println("Error: --docid is required when not running in interactive mode");
      return;
    }

    Configurator.setRootLevel(Level.OFF);
    run(parsedArgs);
  }

  private static void run(Args args) {
    if (args.interactive) {
      runInteractive(args);
    } else {
      runSingleDocument(args);
    }
  }

  private static void runInteractive(Args args) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
         SimpleSearcher searcher = new SimpleSearcher(IndexReaderUtils.getIndex(args.index).toString())) {
      String docid;
      while (true) {
        docid = reader.readLine();
        if (docid == null) {
          return;
        }

        docid = docid.trim();

        if (docid.isBlank()) {
          continue;
        }

        try {
          System.out.println(getRawDocument(searcher, docid));
        } catch (IllegalArgumentException e) {
          System.err.printf("Error: %s%n", e.getMessage());
        }
      }
    } catch (IllegalArgumentException | IOException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }

  private static void runSingleDocument(Args parsed) {
    try (SimpleSearcher searcher = new SimpleSearcher(IndexReaderUtils.getIndex(parsed.index).toString())) {
      System.out.println(getRawDocument(searcher, parsed.docid));
    } catch (IllegalArgumentException | IOException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }

  private static String getRawDocument(SimpleSearcher searcher, String docid) {
    Document document = searcher.doc(docid);
    if (document == null) {
      throw new IllegalArgumentException("Document not found: " + docid);
    }

    String raw = document.get(Constants.RAW);
    if (raw == null) {
      throw new IllegalArgumentException("Document does not have stored raw field: " + docid);
    }

    return raw;
  }
}
