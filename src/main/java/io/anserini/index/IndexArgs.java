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

package io.anserini.index;

import org.kohsuke.args4j.Option;

public class IndexArgs {

  private static final int TIMEOUT = 600 * 1000;

  // required arguments

  @Option(name = "-input", metaVar = "[path]", required = true,
      usage = "Location of input collection.")
  public String input;

  @Option(name = "-threads", metaVar = "[num]", required = true,
      usage = "Number of indexing threads.")
  public int threads;

  @Option(name = "-collection", metaVar = "[class]", required = true,
      usage = "Collection class in package 'io.anserini.collection'.")
  public String collectionClass;

  @Option(name = "-generator", metaVar = "[class]", required = true,
      usage = "Document generator class in package 'io.anserini.index.generator'.")
  public String generatorClass;

  // optional general arguments

  @Option(name = "-verbose", forbids = {"-quiet"},
      usage = "Enables verbose logging for each indexing thread; can be noisy if collection has many small file segments.")
  public boolean verbose = false;

  @Option(name = "-quiet", forbids = {"-verbose"},
      usage = "Turns off all logging.")
  public boolean quiet = false;

  // optional arguments

  @Option(name = "-index", metaVar = "[path]", forbids = {"-solr", "-es"},
      usage = "Index path.")
  public String index;

  @Option(name = "-storePositions",
      usage = "Boolean switch to index store term positions; needed for phrase queries.")
  public boolean storePositions = false;

  @Option(name = "-storeDocvectors",
      usage = "Boolean switch to store document vectors; needed for (pseudo) relevance feedback.")
  public boolean storeDocvectors = false;

  @Option(name = "-storeTransformedDocs",
      usage = "Boolean switch to store transformed document text.")
  public boolean storeTransformedDocs = false;

  @Option(name = "-storeRawDocs",
      usage = "Boolean switch to store raw document text.")
  public boolean storeRawDocs = false;

  @Option(name = "-optimize",
      usage = "Boolean switch to optimize index (i.e., force merge) into a single segment; costly for large collections.")
  public boolean optimize = false;

  @Option(name = "-keepStopwords",
      usage = "Boolean switch to keep stopwords.")
  public boolean keepStopwords = false;

  @Option(name = "-stemmer", metaVar = "[stemmer]",
      usage = "Stemmer: one of the following {porter, krovetz, none}; defaults to 'porter'.")
  public String stemmer = "porter";

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

  @Option(name = "-bm25.accurate",
      usage = "Boolean switch to use AccurateBM25Similarity (computes accurate document lengths).")
  public boolean bm25Accurate = false;

  @Option(name = "-language", metaVar = "[language]",
      usage = "Analyzer language (ISO 3166 two-letter code).")
  public String language= "en";

  // Tweet options

  @Option(name = "-tweet.keepRetweets",
      usage = "Boolean switch to index retweets.")
  public boolean tweetKeepRetweets = false;

  @Option(name = "-tweet.keepUrls",
      usage = "Boolean switch to keep URLs.")
  public boolean tweetKeepUrls = false;

  @Option(name = "-tweet.stemming",
      usage = "Boolean switch to apply Porter stemming while indexing tweets.")
  public boolean tweetStemming = false;

  @Option(name = "-tweet.maxId", metaVar = "[id]",
      usage = "Max tweet id to index (long); all tweets with larger tweet ids will be skipped.")
  public long tweetMaxId = Long.MAX_VALUE;

  @Option(name = "-tweet.deletedIdsFile", metaVar = "[file]",
      usage = "File that contains deleted tweet ids (longs), one per line; these tweets will be skipped during indexing.")
  public String tweetDeletedIdsFile = "";

  // Solr options

  @Option(name = "-solr", forbids = {"-index", "-es"},
      usage = "Indexes into Solr.")
  public boolean solr = false;

  @Option(name = "-solr.batch", metaVar = "[n]",
      usage = "Solr indexing batch size.")
  public int solrBatch = 1000;

  @Option(name = "-solr.commitWithin", metaVar = "[s]",
      usage = "Solr commitWithin setting (in seconds).")
  public int solrCommitWithin = 60;

  @Option(name = "-solr.index", metaVar = "[name]",
      usage = "Solr index name.")
  public String solrIndex = null;

  @Option(name = "-solr.zkUrl", metaVar = "[urls]",
      usage = "Solr ZooKeeper URLs (comma separated list).")
  public String zkUrl = null;

  @Option(name = "-solr.zkChroot", metaVar = "[path]",
      usage = "Solr ZooKeeper chroot")
  public String zkChroot = "/";

  @Option(name = "-solr.poolSize", metaVar = "[n]",
      usage = "Solr client pool size.")
  public int solrPoolSize = 16;

  // Elasticsearch options

  @Option(name = "-es", forbids = {"-index", "-solr"},
      usage = "Indexes into Elasticsearch.")
  public boolean es = false;

  @Option(name = "-es.index", metaVar = "[name]",
      usage = "Elasticsearch index name.")
  public String esIndex = null;

  @Option(name = "-es.batch", metaVar = "[n]",
      usage = "Elasticsearch batch index requests size.")
  public int esBatch = 1000;

  @Option(name = "-es.hostname", metaVar = "[host]",
      usage = "Elasticsearch host.")
  public String esHostname = "localhost";

  @Option(name = "-es.port", metaVar = "[port]",
      usage = "Elasticsearch port number.")
  public int esPort = 9200;

  @Option(name = "-es.user", metaVar = "[username]",
      usage = "Elasticsearch user name.")
  public String esUser = "elastic";

  @Option(name = "-es.password", metaVar = "[password]",
      usage = "Elasticsearch password.")
  public String esPassword = "changeme";

  @Option(name = "-es.poolSize", metaVar = "[num]",
      usage = "Elasticsearch client pool size.")
  public int esPoolSize = 10;

  @Option(name = "-es.connectTimeout", metaVar = "[ms]",
      usage = "Elasticsearch (low level) REST client connect timeout (in ms).")
  public int esConnectTimeout = TIMEOUT;

  @Option(name = "-es.socketTimeout", metaVar = "[ms]",
      usage = "Elasticsearch (low level) REST client socket timeout (in ms).")
  public int esSocketTimeout = TIMEOUT;

  // Sharding options

  @Option(name = "-shard.count", metaVar = "[n]",
      usage = "Number of shards to partition the document collection into.")
  public int shardCount = -1;

  @Option(name = "-shard.current", metaVar = "[n]",
      usage = "The current shard number to generate (indexed from 0).")
  public int shardCurrent = -1;
}
