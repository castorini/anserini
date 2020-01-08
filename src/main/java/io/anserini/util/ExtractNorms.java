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

package io.anserini.util;

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
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

/**
 * Utility for extracting the norm of every document in the index. With Lucene's BM25 implementation, the norm
 * is the document length under Lucene's lossy compression scheme that encodes an integer into a byte.
 */
public class ExtractNorms {

  public static class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index")
    String index;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
    String output;
  }

  public static void main(String[] args) throws Exception {
    Args myArgs = new Args();
    CmdLineParser parser = new CmdLineParser(myArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    Directory dir = FSDirectory.open(Paths.get(myArgs.index));
    IndexReader reader = DirectoryReader.open(dir);

    PrintStream out = new PrintStream(new FileOutputStream(new File(myArgs.output)));
    out.println("docid\tnorm");
    for (LeafReaderContext context : reader.leaves()) {
      LeafReader leafReader = context.reader();
      NumericDocValues docValues = leafReader.getNormValues("contents");
      if (docValues == null) {
        throw new NotStoredException("Norms do not appear to have been indexed!");
      }
      while (docValues.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        out.println(String.format("%d\t%d", docValues.docID() + context.docBase,
            SmallFloat.byte4ToInt((byte) docValues.longValue())));
      }
    }
    out.flush();
    out.close();
    reader.close();
    dir.close();
  }
}
