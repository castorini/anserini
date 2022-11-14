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

package io.anserini.index;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;


public class IndexDenseVectorsArgs {

  // This is the name of the field in the Lucene document where the docid is stored.
  public static final String ID = "id";

  // This is the name of the field in the Lucene document that should be searched by default.
  public static final String CONTENTS = "contents";

  // This is the name of the field in the Lucene document where the raw document is stored.
  public static final String RAW = "raw";

  // This is the name of the field in the Lucene document where the vector document is stored.
  public static final String VECTOR = "vector";

  private static final int TIMEOUT = 600 * 1000;


  // required arguments
  @Option(name = "-M", metaVar = "[num]", required = true,
      usage = "HNSW parameters M")
  public int M = 16;

  @Option(name = "-efC", metaVar = "[num]", required = true,
      usage = "HNSW parameters ef Construction")
  public int efC = 100;

  @Option(name = "-input", metaVar = "[path]", required = true,
      usage = "Location of input collection.")
  public String input;

  @Option(name = "-threads", metaVar = "[num]", required = true,
      usage = "Number of indexing threads.")
  public int threads;

  @Option(name = "-collection", metaVar = "[class]", required = true,
      usage = "Collection class in package 'io.anserini.collection'.")
  public String collectionClass;

  @Option(name = "-generator", metaVar = "[class]",
      usage = "Document generator class in package 'io.anserini.index.generator'.")
  public String generatorClass = "DefaultLuceneDocumentGenerator";

  // optional general arguments

  @Option(name = "-verbose", forbids = {"-quiet"},
      usage = "Enables verbose logging for each indexing thread; can be noisy if collection has many small file segments.")
  public boolean verbose = false;

  @Option(name = "-quiet", forbids = {"-verbose"},
      usage = "Turns off all logging.")
  public boolean quiet = false;

  // optional arguments

  @Option(name = "-index", metaVar = "[path]", usage = "Index path.")
  public String index;

  @Option(name = "-fields", handler = StringArrayOptionHandler.class,
      usage = "List of fields to index (space separated), in addition to the default 'contents' field.")
  public String[] fields = new String[]{};

  @Option(name = "-storePositions",
      usage = "Boolean switch to index store term positions; needed for phrase queries.")
  public boolean storePositions = false;

  @Option(name = "-storeDocvectors",
      usage = "Boolean switch to store document vectors; needed for (pseudo) relevance feedback.")
  public boolean storeDocvectors = false;

  @Option(name = "-storeContents",
      usage = "Boolean switch to store document contents.")
  public boolean storeContents = false;

  @Option(name = "-storeRaw",
      usage = "Boolean switch to store raw source documents.")
  public boolean storeRaw = false;

  @Option(name = "-optimize",
      usage = "Boolean switch to optimize index (i.e., force merge) into a single segment; costly for large collections.")
  public boolean optimize = false;

  @Option(name = "-uniqueDocid",
      usage = "Removes duplicate documents with the same docid during indexing. This significantly slows indexing throughput " +
              "but may be needed for tweet collections since the streaming API might deliver a tweet multiple times.")
  public boolean uniqueDocid = false;

  @Option(name = "-memorybuffer", metaVar = "[mb]",
      usage = "Memory buffer size (in MB).")
  public int memorybufferSize = 2048;

  @Option(name = "-whitelist", metaVar = "[file]",
      usage = "File containing list of docids, one per line; only these docids will be indexed.")
  public String whitelist = null;


  // Sharding options

  @Option(name = "-shard.count", metaVar = "[n]",
      usage = "Number of shards to partition the document collection into.")
  public int shardCount = -1;

  @Option(name = "-shard.current", metaVar = "[n]",
      usage = "The current shard number to generate (indexed from 0).")
  public int shardCurrent = -1;

}
