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

package io.anserini.search;

import io.anserini.index.generator.TweetGenerator;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.topicreader.TopicReader;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

/*
* Entry point of the Retrieval.
 */
public final class SearchElastic implements Closeable {

  private static final Logger LOG = LogManager.getLogger(SearchCollection.class);
  private static final int TIMEOUT = 600 * 1000;
  private final Args args;
  private RestHighLevelClient client;

  public static final class Args {

    // required arguments

    @Option(name = "-topics", metaVar = "[file]", handler = StringArrayOptionHandler.class, required = true, usage = "topics file")
    public String[] topics;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
    public String output;

    @Option(name = "-topicreader", required = true, usage = "define how to read the topic(query) file: one of [Trec|Webxml]")
    public String topicReader;

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

    // optional arguments
    @Option(name = "-topicfield", usage = "Which field of the query should be used, default \"title\"." +
            " For TREC ad hoc topics, description or narrative can be used.")
    public String topicfield = "title";

    @Option(name = "-searchtweets", usage = "Whether the search is against a tweet " +
            "index created by IndexCollection -collection TweetCollection")
    public Boolean searchtweets = false;

    @Option(name = "-hits", metaVar = "[number]", required = false, usage = "max number of hits to return")
    public int hits = 1000;

    @Option(name = "-runtag", metaVar = "[tag]", required = false, usage = "runtag")
    public String runtag = null;

  }

  private final class ESSearcherThread<K> extends Thread {

    final private SortedMap<K, Map<String, String>> topics;
    final private String outputPath;
    final private String runTag;

    private ESSearcherThread(SortedMap<K, Map<String, String>> topics, String outputPath, String runTag){

      this.topics = topics;
      this.runTag = runTag;
      this.outputPath = outputPath;
      setName(outputPath);
    }

    @Override
    public void run() {
      try {
        LOG.info("[Start] Retrieval with Elasticsearch collection: " + args.esIndex);
        final long start = System.nanoTime();
        PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(outputPath), StandardCharsets.US_ASCII));

        for (Map.Entry<K, Map<String, String>> entry : topics.entrySet()) {
          K qid = entry.getKey();
          String queryString = entry.getValue().get(args.topicfield);
          ScoredDocuments docs;
          if (args.searchtweets) {
            docs = searchTweets(queryString, Long.parseLong(entry.getValue().get("time")));
          } else {
            docs = search(queryString);
          }

          /**
           * the first column is the topic number.
           * the second column is currently unused and should always be "Q0".
           * the third column is the official document identifier of the retrieved document.
           * the fourth column is the rank the document is retrieved.
           * the fifth column shows the score (integer or floating point) that generated the ranking.
           * the sixth column is called the "run tag" and should be a unique identifier for your
           */
          for (int i = 0; i < docs.documents.length; i++) {
            out.println(String.format(Locale.US, "%s Q0 %s %d %f %s", qid,
                    docs.documents[i].getField(FIELD_ID).stringValue(), (i + 1), docs.scores[i], runTag));
          }
        }
        out.flush();
        out.close();

        final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        LOG.info("[Finished] Run " + topics.size() + " topics searched in "
                + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      }
    }
  }

  public SearchElastic(Args args) {
    this.args = args;
    LOG.info("Elasticsearch index: " + args.esIndex);
    LOG.info("Elasticsearch hostname: " + args.esHostname);
    LOG.info("Elasticsearch host port: " + args.esPort);

    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(args.esUser, args.esPassword));

    this.client = new RestHighLevelClient(
            RestClient.builder(new HttpHost(args.esHostname, args.esPort, "http"))
                    .setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider))
                    .setRequestConfigCallback(builder -> builder.setConnectTimeout(TIMEOUT).setSocketTimeout(TIMEOUT)));
  }

  @SuppressWarnings("unchecked")
  public<K> void runTopics() throws IOException {
    TopicReader<K> tr;
    SortedMap<K, Map<String, String>> topics = new TreeMap<>();
    for (String singleTopicsFile : args.topics) {
      Path topicsFilePath = Paths.get(singleTopicsFile);
      if (!Files.exists(topicsFilePath) || !Files.isRegularFile(topicsFilePath) || !Files.isReadable(topicsFilePath)) {
        throw new IllegalArgumentException("Topics file : " + topicsFilePath + " does not exist or is not a (readable) file.");
      }
      try {
        tr = (TopicReader<K>) Class.forName("io.anserini.search.topicreader." + args.topicReader + "TopicReader")
            .getConstructor(Path.class).newInstance(topicsFilePath);
        topics.putAll(tr.read());
      } catch (Exception e) {
        throw new IllegalArgumentException("Unable to load topic reader: " + args.topicReader);
      }
    }
  
    final String runTag = args.runtag == null ? "Elastirini" : args.runtag;
    ESSearcherThread<K> esThread = new ESSearcherThread<K>(topics, args.output, runTag);
    esThread.run();
  }

  public<K> ScoredDocuments search(String queryString){

    SearchHits results = null;

    String specials = "+-=&|><!(){}[]^\"~*?:\\/";

    for (int i = 0; i < specials.length(); i++){
      char c = specials.charAt(i);
      queryString = queryString.replace(String.valueOf(c), " ");
    }

    QueryStringQueryBuilder query = QueryBuilders
            .queryStringQuery(queryString)
            .defaultField("contents")
            .analyzer("english");

    SearchRequest searchRequest = new SearchRequest(args.esIndex);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(query);
    sourceBuilder.size(args.hits);
    sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
    sourceBuilder.sort(new FieldSortBuilder(FIELD_ID).order(SortOrder.ASC));
    searchRequest.source(sourceBuilder);

    try {
      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      results = searchResponse.getHits();
    } catch (Exception e) {
      LOG.error("Exception during ES query: ", e);
    }

    ScoreTiesAdjusterReranker reranker = new ScoreTiesAdjusterReranker();
    return reranker.rerank(ScoredDocuments.fromESDocs(results), null);
  }

  public<K> ScoredDocuments searchTweets(String queryString, long t){

    SearchHits results = null;

    String specials = "+-=&|><!(){}[]^\"~*?:\\/";

    for (int i = 0; i < specials.length(); i++){
      char c = specials.charAt(i);
      queryString = queryString.replace(String.valueOf(c), " ");
    }

    // Do not consider the tweets with tweet ids that are beyond the queryTweetTime
    // <querytweettime> tag contains the timestamp of the query in terms of the
    // chronologically nearest tweet id within the corpus
    RangeQueryBuilder queryTweetTime = QueryBuilders
            .rangeQuery(TweetGenerator.StatusField.ID_LONG.name)
            .from(0L)
            .to(t);

    QueryStringQueryBuilder queryTerms = QueryBuilders
            .queryStringQuery(queryString)
            .defaultField("contents")
            .analyzer("english");

    BoolQueryBuilder query = QueryBuilders.boolQuery()
            .filter(queryTweetTime)
            .should(queryTerms);

    SearchRequest searchRequest = new SearchRequest(args.esIndex);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(query);
    sourceBuilder.size(args.hits);
    sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
    sourceBuilder.sort(new FieldSortBuilder(TweetGenerator.StatusField.ID_LONG.name).order(SortOrder.DESC));
    searchRequest.source(sourceBuilder);

    try {
      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      results = searchResponse.getHits();
    } catch (Exception e) {
      LOG.error("Exception during ES query: ", e);
    }

    ScoreTiesAdjusterReranker reranker = new ScoreTiesAdjusterReranker();
    return reranker.rerank(ScoredDocuments.fromESDocs(results), null);
  }

  @Override
  public void close() throws IOException {
    client.close();
  }

  public static void main(String[] args) throws Exception {
    Args searchElasticArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchElasticArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SearchElastic" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    SearchElastic searcher = new SearchElastic(searchElasticArgs);
    searcher.runTopics();
    searcher.close();
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}
