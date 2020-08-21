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

import io.anserini.index.IndexArgs;
import io.anserini.index.IndexReaderUtils;
import io.anserini.index.NotStoredException;
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

/**
 * Utility for extracting the document length and the number of unique terms from every document in the index using the
 * indexed term vector. Outputs both the exact values and the value under Lucene's lossy compression scheme that encodes
 * an integer into a byte. With Lucene's BM25 implementation, the lossy document length should be exactly the same as
 * the stored norm of a document. Note that the term vector <i>must</i> be indexed in order for this utility to work;
 * otherwise, there is no other way to recover the <i>exact</i> document length (with Lucene's default BM25
 * implementation).
 */
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
      System.err.println(String.format("Example: %s %s",
          ExtractDocumentLengths.class.getSimpleName(), parser.printExample(OptionHandlerFilter.REQUIRED)));
      return;
    }

    Directory dir = FSDirectory.open(Paths.get(myArgs.index));
    IndexReader reader = DirectoryReader.open(dir);

    PrintStream out = new PrintStream(new FileOutputStream(new File(myArgs.output)));

    int numDocs = reader.numDocs();
    long lossyTotalTerms = 0;
    long exactTotalTerms = 0;

    out.println("internal_docid\texternal_docid\tdoc_length\tunique_term_count\tlossy_doc_length\tlossy_unique_term_count");
    for (int i = 0; i < numDocs; i++) {
      Terms terms = reader.getTermVector(i, IndexArgs.CONTENTS);
      if (terms == null) {
        // It could be the case that TermVectors weren't stored when constructing the index, or we're just missing a
        // TermVector for a zero-length document. Warn, but don't throw exception.
        String external_did = IndexReaderUtils.convertLuceneDocidToDocid(reader, i);
        System.err.println(String.format("Warning: TermVector not available for docid %s.", external_did));
        out.println(String.format("%d\t%s\t0\t0\t0\t0", i, external_did));
        continue;
      }

      long exactDoclength = terms.getSumTotalTermFreq();
      long exactTermCount = terms.size();
      // Uses Lucene's method of encoding an integer into a byte, and the decoding it again.
      // This matches the norm encoding in BM25Similarity:
      // See https://github.com/apache/lucene-solr/blob/master/lucene/core/src/java/org/apache/lucene/search/similarities/BM25Similarity.java
      int lossyDoclength = SmallFloat.byte4ToInt(SmallFloat.intToByte4((int) exactDoclength));
      int lossyTermCount = SmallFloat.byte4ToInt(SmallFloat.intToByte4((int) exactTermCount));
      out.println(String.format("%d\t%s\t%d\t%d\t%d\t%d", i, IndexReaderUtils.convertLuceneDocidToDocid(reader, i),
              exactDoclength, exactTermCount, lossyDoclength, lossyTermCount));
      lossyTotalTerms += lossyDoclength;
      exactTotalTerms += exactDoclength;
    }

    System.out.println("Total number of terms in collection (sum of doclengths):");
    System.out.println("Lossy: " + lossyTotalTerms);
    System.out.println("Exact: " + exactTotalTerms);

    out.flush();
    out.close();
    reader.close();
    dir.close();
  }
}
