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

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.SmallFloat;
import org.kohsuke.args4j.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;

public class ExtractDocumentLengths {

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

    int numDocs = reader.numDocs();
    out.println("luceneID\tcount\tuniquecount\tlossycount");
    for (int i = 0; i < numDocs; i++) {
      Terms terms = reader.getTermVector(i, "contents");
      if(terms == null) {
        out.println(i + "\t" + 0 + "\t" + 0 + "\t" + 0);
        continue;
      }
      long total = terms.getSumTotalTermFreq();
      long length = SmallFloat.longToInt4(total);
      out.println(i + "\t" + total + "\t" + terms.size() + "\t" + length) ;
    }
    out.close();
  }
}
