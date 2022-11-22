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

import io.anserini.index.Constants;
import io.anserini.index.NotStoredException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.SmallFloat;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.nio.file.Paths;

public class ExtractAverageDocumentLength {

  public static class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index")
    String index;

    @Option(name = "-field", metaVar = "[name]", usage = "field")
    String field = Constants.CONTENTS;
  }

  public static void main(String[] args) throws Exception {
    ExtractAverageDocumentLength.Args myArgs = new ExtractAverageDocumentLength.Args();
    CmdLineParser parser = new CmdLineParser(myArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println(String.format("Example: %s %s",
          ExtractAverageDocumentLength.class.getSimpleName(), parser.printExample(OptionHandlerFilter.REQUIRED)));
      return;
    }

    Directory dir = FSDirectory.open(Paths.get(myArgs.index));
    IndexReader reader = DirectoryReader.open(dir);
    if (reader.leaves().size() != 1) {
      System.err.println("There should be only one leaf, index the collection using the -optimize flag.");
      return;
    }

    LeafReader leafReader = reader.leaves().get(0).reader();
    System.out.println("# Exact avg doclength");
    System.out.println("SumTotalTermFreq: " + leafReader.getSumTotalTermFreq(myArgs.field));
    System.out.println("DocCount:         " + leafReader.getDocCount(myArgs.field));
    System.out.println("avg doclength:    " +
        ((float) leafReader.getSumTotalTermFreq(myArgs.field) / (float) leafReader.getDocCount(myArgs.field)));

    long sumDoclengths = 0;
    for (LeafReaderContext context : reader.leaves()) {
      leafReader = context.reader();
      NumericDocValues docValues = leafReader.getNormValues(myArgs.field);
      if (docValues == null) {
        throw new NotStoredException("Norms do not appear to have been indexed!");
      }
      while (docValues.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        sumDoclengths += SmallFloat.byte4ToInt((byte) docValues.longValue());
      }
    }

    System.out.println("\n# Lossy avg doclength, based on sum of norms (lossy doclength) of each doc");
    System.out.println("SumTotalTermFreq: " + sumDoclengths);
    System.out.println("DocCount:         " + leafReader.getDocCount(myArgs.field));
    System.out.println("avg doclength:    " + ((float) sumDoclengths / (float) leafReader.getDocCount(myArgs.field)));

    reader.close();
    dir.close();
  }
}
