package luceneingester;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.benchmark.quality.Judge;
import org.apache.lucene.benchmark.quality.QualityBenchmark;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.QualityStats;
import org.apache.lucene.benchmark.quality.trec.QueryDriver;
import org.apache.lucene.benchmark.quality.trec.TrecJudge;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.benchmark.quality.utils.SubmissionReport;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

public class TrecDriver extends QueryDriver {
  public static void main(String[] args) throws Exception {

    if (args.length < 4 || args.length > 5) {
      System.err.println("Usage: QueryDriver <topicsFile> <qrelsFile> <submissionFile> <indexDir> [querySpec]");
      System.err.println("topicsFile: input file containing queries");
      System.err.println("qrelsFile: input file containing relevance judgements");
      System.err.println("submissionFile: output submission file for trec_eval");
      System.err.println("indexDir: index directory");
      System.err.println("querySpec: string composed of fields to use in query consisting of T=title,D=description,N=narrative:");
      System.err.println("\texample: TD (query on Title + Description). The default is T (title only)");
      System.exit(1);
    }
    
    Path topicsFile = Paths.get(args[0]);
    Path qrelsFile = Paths.get(args[1]);
    Path submissionFile = Paths.get(args[2]);
    SubmissionReport submitLog = new SubmissionReport(new PrintWriter(Files.newBufferedWriter(submissionFile, StandardCharsets.UTF_8)), "lucene");
    FSDirectory dir = FSDirectory.open(Paths.get(args[3]));
    String fieldSpec = args.length == 5 ? args[4] : "T"; // default to Title-only if not specified.
    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new BM25Similarity(0.9f, 0.4f));

    int maxResults = 1000;
    String docNameField = "docname";

    PrintWriter logger = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset()), true);

    // use trec utilities to read trec topics into quality queries
    TrecTopicsReader qReader = new TrecTopicsReader();
    QualityQuery qqs[] = qReader.readQueries(Files.newBufferedReader(topicsFile, StandardCharsets.UTF_8));

    // prepare judge, with trec utilities that read from a QRels file
    Judge judge = new TrecJudge(Files.newBufferedReader(qrelsFile, StandardCharsets.UTF_8));

    // validate topics & judgments match each other
    judge.validateData(qqs, logger);

    Set<String> fieldSet = new HashSet<>();
    if (fieldSpec.indexOf('T') >= 0) fieldSet.add("title");
    if (fieldSpec.indexOf('D') >= 0) fieldSet.add("description");
    if (fieldSpec.indexOf('N') >= 0) fieldSet.add("narrative");
    
    // set the parsing of quality queries into Lucene queries.
    QualityQueryParser qqParser = new EnglishQQParser(fieldSet.toArray(new String[0]), "body");

    // run the benchmark
    QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);
    qrun.setMaxResults(maxResults);
    QualityStats stats[] = qrun.execute(judge, submitLog, logger);

    // print an avarage sum of the results
    QualityStats avg = QualityStats.average(stats);
    avg.log("SUMMARY", 2, logger, "  ");
    reader.close();
    dir.close();
  }
}

class EnglishQQParser implements QualityQueryParser {

  private String qqNames[];
  private String indexField;
  ThreadLocal<QueryParser> queryParser = new ThreadLocal<>();

  /**
   * Constructor of a simple qq parser.
   * @param qqNames name-value pairs of quality query to use for creating the query
   * @param indexField corresponding index field  
   */
  public EnglishQQParser(String qqNames[], String indexField) {
    this.qqNames = qqNames;
    this.indexField = indexField;
  }

  /**
   * Constructor of a simple qq parser.
   * @param qqName name-value pair of quality query to use for creating the query
   * @param indexField corresponding index field  
   */
  public EnglishQQParser(String qqName, String indexField) {
    this(new String[] { qqName }, indexField);
  }

  /* (non-Javadoc)
   * @see org.apache.lucene.benchmark.quality.QualityQueryParser#parse(org.apache.lucene.benchmark.quality.QualityQuery)
   */
  @Override
  public Query parse(QualityQuery qq) throws ParseException {
    QueryParser qp = queryParser.get();
    if (qp==null) {
      qp = new QueryParser(indexField, new EnglishAnalyzer());
      queryParser.set(qp);
    }
    BooleanQuery bq = new BooleanQuery();
    for (int i = 0; i < qqNames.length; i++)
      bq.add(qp.parse(QueryParserBase.escape(qq.getValue(qqNames[i]))), BooleanClause.Occur.SHOULD);
    
    return bq;
  }

}

