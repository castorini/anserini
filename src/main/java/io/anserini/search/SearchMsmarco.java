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

package io.anserini.search;

import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.util.List;

/*
 * Java rewrite of retrieve.py
 */
public class SearchMsmarco {
  public static void main(String[] args) throws Exception {
    RetrieveArgs retrieveArgs = new RetrieveArgs();
    CmdLineParser parser = new CmdLineParser(retrieveArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: Eval " + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    SimpleSearcher searcher = new SimpleSearcher(retrieveArgs.index);
    searcher.setBM25Similarity(retrieveArgs.k1, retrieveArgs.b);
    System.out.println("Initializing BM25, setting k1=" + retrieveArgs.k1 + " and b=" + retrieveArgs.b + "");

    if (retrieveArgs.rm3) {
      searcher.setRM3Reranker(retrieveArgs.fbTerms, retrieveArgs.fbDocs, retrieveArgs.originalQueryWeight);
      System.out.println("Initializing RM3, setting fbTerms=" + retrieveArgs.fbTerms + ", fbDocs=" + retrieveArgs.fbDocs
              + " and originalQueryWeight=" + retrieveArgs.originalQueryWeight);
    }

    File fout = new File(retrieveArgs.output);
    FileUtils.writeStringToFile(fout, "", "utf-8"); // clear the file

    long startTime = System.nanoTime();
    List<String> lines = FileUtils.readLines(new File(retrieveArgs.qid_queries), "utf-8");

    for (int lineNumber = 0; lineNumber < lines.size(); ++lineNumber) {
      String line = lines.get(lineNumber);
      String[] split = line.trim().split("\t");
      String qid = split[0];
      String query = split[1];

      SimpleSearcher.Result[] hits = searcher.search(query, retrieveArgs.hits);

      if (lineNumber % 10 == 0) {
        double timePerQuery = (double) (System.nanoTime() - startTime) / (lineNumber + 1) / 10e9;
        System.out.format("Retrieving query " + lineNumber + " (%.3f s/query)\n", timePerQuery);
      }

      for (int rank = 0; rank < hits.length; ++rank) {
        String docno = hits[rank].docid;
        FileUtils.writeStringToFile(fout, qid + "\t" + docno + "\t" + (rank + 1) + "\n", "utf-8", true);
      }
    }

    System.out.println("Done!");
  }
}
