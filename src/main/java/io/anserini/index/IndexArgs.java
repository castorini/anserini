package io.anserini.index;

import org.kohsuke.args4j.Option;

public class IndexArgs {

  private static final int TIMEOUT = 600 * 1000;

  // required arguments

  @Option(name = "-input", metaVar = "[Directory]", required = true, usage = "collection directory")
  public String input;

  @Option(name = "-threads", metaVar = "[Number]", required = true, usage = "Number of Threads")
  public int threads;

  @Option(name = "-collection", required = true, usage = "collection class in io.anserini.collection")
  public String collectionClass;

  @Option(name = "-generator", required = true, usage = "document generator in io.anserini.index.generator")
  public String generatorClass;

  // optional arguments

  @Option(name = "-index", metaVar = "[Path]", forbids = {"-solr", "-es"}, usage = "index path")
  public String index;

  @Option(name = "-storePositions", usage = "boolean switch to index storePositions")
  public boolean storePositions = false;

  @Option(name = "-storeDocvectors", usage = "boolean switch to store document vectors")
  public boolean storeDocvectors = false;

  @Option(name = "-storeTransformedDocs", usage = "boolean switch to store transformed document text")
  public boolean storeTransformedDocs = false;

  @Option(name = "-storeRawDocs", usage = "boolean switch to store raw document text")
  public boolean storeRawDocs = false;

  @Option(name = "-optimize", usage = "boolean switch to optimize index (force merge)")
  public boolean optimize = false;

  @Option(name = "-keepStopwords", usage = "boolean switch to keep stopwords")
  public boolean keepStopwords = false;

  @Option(name = "-stemmer", usage = "Stemmer: one of the following porter,krovetz,none. Default porter")
  public String stemmer = "porter";

  @Option(name = "-uniqueDocid", usage = "remove duplicated documents with the same doc id when indexing. " +
      "please note that this option may slow the indexing a lot and if you are sure there is no " +
      "duplicated document ids in the corpus you shouldn't use this option.")
  public boolean uniqueDocid = false;

  @Option(name = "-memorybuffer", usage = "memory buffer size")
  public int memorybufferSize = 2048;

  @Option(name = "-whitelist", usage = "file containing docids, one per line; only specified docids will be indexed.")
  public String whitelist = null;

  @Option(name = "-bm25.accurate", usage = "Switch to use the accurate BM25 similarity)")
  public boolean bm25Accurate = false;

  @Option(name = "-tweet.keepRetweets", usage = "boolean switch to keep retweets while indexing")
  public boolean tweetKeepRetweets = false;

  @Option(name = "-tweet.keepUrls", usage = "boolean switch to keep URLs while indexing tweets")
  public boolean tweetKeepUrls = false;

  @Option(name = "-tweet.stemming", usage = "boolean switch to apply Porter stemming while indexing tweets")
  public boolean tweetStemming = false;

  @Option(name = "-tweet.maxId", usage = "the max tweet Id for indexing. Tweet Ids that are larger " +
      " (when being parsed to Long type) than this value will NOT be indexed")
  public long tweetMaxId = Long.MAX_VALUE;

  @Option(name = "-tweet.deletedIdsFile", metaVar = "[Path]",
      usage = "a file that contains deleted tweetIds, one per line. these tweeets won't be indexed")
  public String tweetDeletedIdsFile = "";

  @Option(name = "-solr", forbids = {"-index", "-es"}, usage = "boolean switch to determine if we should index into Solr")
  public boolean solr = false;

  @Option(name = "-solr.batch", usage = "the batch size for submitting documents to Solr")
  public int solrBatch = 1000;

  @Option(name = "-solr.commitWithin", usage = "the number of seconds to commitWithin")
  public int solrCommitWithin = 60;

  @Option(name = "-solr.index", usage = "the name of the index in Solr")
  public String solrIndex = null;

  @Option(name = "-solr.zkUrl", usage = "the URL of Solr's ZooKeeper (comma separated list of using ensemble)")
  public String zkUrl = null;

  @Option(name = "-solr.zkChroot", usage = "the ZooKeeper chroot")
  public String zkChroot = "/";

  @Option(name = "-solr.poolSize", metaVar = "[NUMBER]", usage = "the number of clients to keep in the pool")
  public int solrPoolSize = 16;

  @Option(name = "-es", forbids = {"-index", "-solr"}, usage = "boolean switch to determine if we should index through Elasticsearch")
  public boolean es = false;

  @Option(name = "-es.batch", usage = "the number of index requests in a bulk request sent to Elasticsearch")
  public int esBatch = 1000;

  @Option(name = "-es.index", usage = "the name of the index in Elasticsearch")
  public String esIndex = null;

  @Option(name = "-es.hostname", usage = "the name of Elasticsearch HTTP host")
  public String esHostname = "localhost";

  @Option(name = "-es.port", usage = "the port for Elasticsearch HTTP host")
  public int esPort = 9200;

  /**
   * The user and password are defaulted to those pre-configured for docker-elk
   */
  @Option(name = "-es.user", usage = "the user of the ELK stack")
  public String esUser = "elastic";

  @Option(name = "-es.password", usage = "the password for the ELK stack")
  public String esPassword = "changeme";

  @Option(name = "-es.poolSize", metaVar = "[NUMBER]", usage = "the number of Elasticsearch clients to keep in the pool")
  public int esPoolSize = 10;

  @Option(name = "-es.connectTimeout", metaVar = "[NUMBER]", usage = "the Elasticsearch (low level) REST client connect timeout (in ms)")
  public int esConnectTimeout = TIMEOUT;

  @Option(name = "-es.socketTimeout", metaVar = "[NUMBER]", usage = "the Elasticsearch (low level) REST client socket timeout (in ms)")
  public int esSocketTimeout = TIMEOUT;

  @Option(name = "-shard.count", usage = "the number of shards for the index")
  public int shardCount = -1;

  @Option(name = "-shard.current", usage = "the current shard number to produce (indexed from 0)")
  public int shardCurrent = -1;

  @Option(name = "-dryRun", usage = "performs all analysis steps except Lucene / Solr indexing")
  public boolean dryRun = false;
}
