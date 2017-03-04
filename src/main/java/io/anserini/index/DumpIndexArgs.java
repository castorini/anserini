/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.index;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.util.List;

/**
 * Common arguments (dataDir, indexPath, numThreads, etc) necessary for indexing.
 */
public class DumpIndexArgs {

  // required arguments

  @Option(name = "-index", metaVar = "[Path]", required = true, usage = "Lucene index path")
  String index;

  @Option(name = "-s", forbids = {"-t","-di","-dn","-dt","-dv"}, handler=BooleanOptionHandler.class, usage = "Print statistics for the Repository")
  boolean stats;

  @Option(name = "-t", forbids = {"-s","-di","-dn","-dt","-dv"}, usage = "Print term info: stemmed, total counts, doc counts")
  String term;

  @Option(name = "-di", forbids = {"-s","-t","-dn","-dt","-dv"}, handler = StringArrayOptionHandler.class, usage = "Print the internal document IDs of documents")
  List<String> di;

  @Option(name = "-dn", forbids = {"-s","-t","-di","-dt","-dv"}, handler = IntOptionHandler.class, usage = "Print the text representation of a document ID")
  int dn;

  @Option(name = "-dt", forbids = {"-s","-t","-di","-dn","-dv"}, handler = IntOptionHandler.class, usage = "Print the text of a document")
  int dt;

  @Option(name = "-dv", forbids = {"-s","-t","-di","-dt","-dn"}, handler = IntOptionHandler.class, usage = "Print the document vector of a document")
  int dv;

  @Option(name = "-dumpRawDoc", usage = "dumps raw document (if stored in the index)")
  int rawDoc;

  @Option(name = "-dumpTransformedDoc", usage = "dumps transformed document (if stored in the index)")
  int transformedDoc;

  @Option(name = "-cDocid2iDocid", usage = "converts a collection docid to a Lucene internal docid")
  String cDocid2iDocid;

  @Option(name = "-iDocid2cDocid", usage = "converts to a Lucene internal docid to a collection docid ")
  int iDocid2cDocid;
}