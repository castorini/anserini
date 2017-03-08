package io.anserini.qa;

import edu.stanford.nlp.simple.Sentence;
import io.anserini.index.IndexUtils;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.qa.passage.Context;
import io.anserini.qa.passage.PassageScorer;
import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.search.SearchWebCollection;
import io.anserini.util.AnalyzerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

public class RetrieveSentences {

  public static class QAargs {
    // required arguments
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index")
    public String index;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
    public String output;

    // optional arguments
    @Option(name = "-topics", metaVar = "[file]", usage = "topics file")
    public String topics = "";

    @Option(name = "-query", metaVar = "[string]", usage = "a single query")
    public String query = "";

    @Option(name = "-hits", metaVar = "[number]", required = false, usage = "max number of hits to return")
    public int hits = 100;

    //Todo: add more passage scorer
    @Option(name = "-scorer", metaVar = "[Idf]", usage = "passage scores")
    public String scorer;
  }

  private static final Logger LOG = LogManager.getLogger(SearchWebCollection.class);
  private final IndexReader reader;
  private final Class passageClass;
  private final PassageScorer scorer;

  public RetrieveSentences(QAargs qAargs) throws Exception {
    Path indexPath = Paths.get(qAargs.index);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(qAargs.index + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    this.passageClass = Class.forName("io.anserini.qa.passage." + qAargs.scorer + "PassageScorer");
    scorer = (PassageScorer) this.passageClass.newInstance();
  }

  public void search(SortedMap<Integer, String> topics, String submissionFile, int numHits)
          throws IOException, ParseException {
    IndexSearcher searcher = new IndexSearcher(reader);

    //using BM25 scoring model
    Similarity similarity = new BM25Similarity(0.9f, 0.4f);
    searcher.setSimilarity(similarity);

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(submissionFile), StandardCharsets.US_ASCII));

    EnglishAnalyzer ea = new EnglishAnalyzer();
    QueryParser queryParser = new QueryParser(FIELD_BODY, ea);
    queryParser.setDefaultOperator(QueryParser.Operator.OR);

    for (Map.Entry<Integer, String> entry : topics.entrySet()) {
      int qID = entry.getKey();
      String queryString = entry.getValue();
      Query query = AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, ea, queryString);

      TopDocs rs = searcher.search(query, numHits);
      ScoreDoc[] hits = rs.scoreDocs;
      ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

      for (int i = 0; i < docs.documents.length; i++) {
        out.println(String.format("%d %s %d %f", qID,
                docs.documents[i].getField(FIELD_ID).stringValue(), (i + 1), docs.scores[i]));
      }
    }
    out.flush();
    out.close();
  }

  public void getRankedPassages(QAargs qaArgs) throws Exception {
    IndexUtils util = new IndexUtils(qaArgs.index);
    List<String> sentencesList = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(qaArgs.output))) {
      String line;

      while ((line = br.readLine()) != null) {
        String docid = line.trim().split(" ")[1];
        List<Sentence> sentences = util.getSentDocument(docid);
        for (Sentence sent : sentences) {
          sentencesList.add(sent.text());
        }
      }
    }
    Context c = new Context();
    scorer.score(sentencesList, qaArgs.index, qaArgs.output, c);
    Map<String, Double> sentences = c.getState();

    Map.Entry<String, Double> maxEntry = null;
    for (Map.Entry<String, Double> entry : sentences.entrySet()) {
      if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
        maxEntry = entry;
      }
    }
    System.out.println("Answer: " + maxEntry.getKey());
  }

  public SortedMap<Integer, String> readTopicsFile(String topicsFile) throws IOException {
    SortedMap<Integer, String> map = new TreeMap<>();
    String pattern = "<QApairs id=\'(.*)\'>";
    Pattern r = Pattern.compile(pattern);

    try (BufferedReader br = new BufferedReader(new FileReader(topicsFile))) {
      String line;
      String prevLine = "";

      while ((line = br.readLine()) != null) {
        Matcher m = r.matcher(line);
        String id = "";
        if (m.find()) {
          id = m.group(1);
        }

        if (prevLine != null && prevLine.startsWith("<question>")) {
          map.put(Integers.parseInt(id), line);
        }
        prevLine = line;
      }
    }
    return map;
  }

  public void retrieveDocuments(QAargs qaArgs) throws Exception {
    Directory dir = FSDirectory.open(Paths.get(qaArgs.index));

    RerankerCascade cascade = new RerankerCascade();
    boolean useQueryParser = false;
    cascade.add(new IdentityReranker());
    FeatureExtractors extractors = null;

    SortedMap<Integer, String> topics = new TreeMap<>();
    if (!qaArgs.topics.isEmpty()) {
      topics = readTopicsFile(qaArgs.topics);
    } else {
      topics.put(1, qaArgs.query);
    }

    search(topics, qaArgs.output, qaArgs.hits);
  }

  public static void main(String[] args) throws Exception {
    QAargs qaArgs = new QAargs();
    CmdLineParser parser = new CmdLineParser(qaArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: RetrieveSentences" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    if (qaArgs.topics.isEmpty() && qaArgs.query.isEmpty()){
      System.err.println("Pass either a query or a topic. Both can't be empty.");
      return;
    }

    RetrieveSentences rs = new RetrieveSentences(qaArgs);
    rs.retrieveDocuments(qaArgs);
    rs.getRankedPassages(qaArgs);
  }
}
