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

package io.anserini.search;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;


public class SearchVectorArgs {
  // required arguments
  @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index")
  public String index;

  @Option(name = "-topics", metaVar = "[file]", handler = StringArrayOptionHandler.class, required = true, usage = "topics file")
  public String[] topics;

  @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
  public String output;

  @Option(name = "-topicreader", required = true, usage = "TopicReader to use.")
  public String topicReader;

  // optional arguments
  @Option(name = "-querygenerator", usage = "QueryGenerator to use.")
  public String queryGenerator = "BagOfWordsQueryGenerator";

  @Option(name = "-threads", metaVar = "[int]", usage = "Number of threads to use for running different parameter configurations.")
  public int threads = 1;

  @Option(name = "-parallelism", metaVar = "[int]", usage = "Number of threads to use for each individual parameter configuration.")
  public int parallelism = 8;

  @Option(name = "-removeQuery", usage = "Remove docids that have the query id when writing final run output.")
  public Boolean removeQuery = false;

  // Note that this option is set to false by default because duplicate documents usually indicate some underlying
  // indexing issues, and we don't want to just eat errors silently.
  @Option(name = "-removedups", usage = "Remove duplicate docids when writing final run output.")
  public Boolean removedups = false;

  @Option(name = "-skipexists", usage = "When enabled, will skip if the run file exists")
  public Boolean skipexists = false;

  @Option(name = "-hits", metaVar = "[number]", required = false, usage = "max number of hits to return")
  public int hits = 1000;

  @Option(name = "-efSearch", metaVar = "[number]", required = false, usage = "efSearch parameter for HNSW search")
  public int efSearch = 100;

  @Option(name = "-inmem", usage = "Boolean switch to read index in memory")
  public Boolean inmem = false;

  @Option(name = "-topicfield", usage = "Which field of the query should be used, default \"title\"." +
      " For TREC ad hoc topics, description or narrative can be used.")
  public String topicfield = "title";

  @Option(name = "-runtag", metaVar = "[tag]", usage = "runtag")
  public String runtag = null;

  @Option(name = "-format", metaVar = "[output format]", usage = "Output format, default \"trec\", alternative \"msmarco\".")
  public String format = "trec";

  // ---------------------------------------------
  // Simple built-in support for passage retrieval
  // ---------------------------------------------

  // A simple approach to passage retrieval is to pre-segment documents in the corpus into passages and index those
  // passages. At retrieval time, we retain only the max scoring passage from each document; this is often called MaxP,
  // from Dai and Callan (SIGIR 2019) in the context of BERT, although the general approach dates back to Callan
  // (SIGIR 1994), Hearst and Plaunt (SIGIR 1993), and lots of other papers from the 1990s and even earlier.
  //
  // One common convention is to label the passages of a docid as "docid.00000", "docid.00001", "docid.00002", ...
  // We use this convention in CORD-19. Alternatively, in document expansion for the MS MARCO document corpus, we use
  // '#' as the delimiter.
  //
  // The options below control various aspects of this behavior.

  @Option(name = "-selectMaxPassage", usage = "Select and retain only the max scoring segment from each document.")
  public Boolean selectMaxPassage = false;

  @Option(name = "-selectMaxPassage.delimiter", metaVar = "[regexp]",
      usage = "The delimiter (as a regular regression) for splitting the segment id from the doc id.")
  public String selectMaxPassage_delimiter = "\\.";

  @Option(name = "-selectMaxPassage.hits", metaVar = "[int]",
      usage = "Maximum number of hits to return per topic after segment id removal. " +
          "Note that this is different from '-hits', which specifies the number of hits including the segment id. ")
  public int selectMaxPassage_hits = Integer.MAX_VALUE;
}
