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

/**
 * This is the base class that holds common arguments for configuring searchers. Note that, explicitly, there are no
 * arguments that are specific to the retrieval implementation (e.g., for HNSW searchers), and that there are no
 * arguments that define queries and outputs (which are to be defined by subclasses that may call the searcher in
 * different ways).
 */
public class BaseSearchArgs {
  @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index")
  public String index;

  @Option(name = "-threads", metaVar = "[int]", usage = "Number of threads for running queries in parallel.")
  public int threads = 4;

  // In some test collections, a document is used as a query, usually denoted by setting the qid as the docid. In this
  // case, we want to remove the docid from the ranked list.
  @Option(name = "-removeQuery", usage = "Remove docids that have the query id when writing final run output.")
  public Boolean removeQuery = false;

  // Note that this option is set to false by default because duplicate documents usually indicate some underlying
  // corpus or indexing issues, and we don't want to just eat errors silently.
  @Option(name = "-removeDuplicates", usage = "Remove duplicate docids when writing final run output.")
  public Boolean removeDuplicates = false;

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
  public String selectMaxPassageDelimiter = "\\.";

  @Option(name = "-selectMaxPassage.hits", metaVar = "[int]",
      usage = "Maximum number of hits to return per topic after segment id removal. " +
          "Note that this is different from '-hits', which specifies the number of hits including the segment id.")
  public int selectMaxPassageHits = Integer.MAX_VALUE;
}