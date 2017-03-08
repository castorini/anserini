package io.anserini.qa;

import edu.stanford.nlp.simple.Sentence;
import io.anserini.index.IndexUtils;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.search.SearchWebCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

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
  }

  private static final Logger LOG = LogManager.getLogger(SearchWebCollection.class);

  public Map<String, Double> computeIDF(QAargs qAargs) throws Exception {
    IndexUtils util = new IndexUtils(qAargs.index);
    FSDirectory directory = FSDirectory.open(new File(qAargs.index).toPath());
    DirectoryReader reader = DirectoryReader.open(directory);

    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);

    Map<String, Double> sentenceIDF = new HashMap();
    ClassicSimilarity similarity = new ClassicSimilarity();

    //for each document in the ranked list, dump sentences
    try (BufferedReader br = new BufferedReader(new FileReader(qAargs.output))) {
      String line;

      while ((line = br.readLine()) != null) {
        String docid = line.trim().split(" ")[2];
        List<Sentence> sentences = util.getSentDocument(docid);
        for (Sentence sent: sentences) {
          double idf = 0.0;
          String[] terms = sent.text().split(" ");
          for (String term: terms) {
            try {
              TermQuery q = (TermQuery) qp.parse(term);
              Term t = q.getTerm();
              idf += similarity.idf(reader.docFreq(t), reader.numDocs());
            } catch (Exception e){
              continue;
            }
          }
          sentenceIDF.put(sent.text(), new Double(idf));
        }
      }
    }
    return sentenceIDF;
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
    Path indexPath = Paths.get(qaArgs.index);
    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(qaArgs.index + " does not exist or is not a directory.");
    }

    IndexReader reader = DirectoryReader.open(FSDirectory.open(indexPath));

    Directory dir;
    dir = FSDirectory.open(Paths.get(qaArgs.index));

    //using BM25 scoring model
    Similarity similarity = null;
    similarity = new BM25Similarity(0.9f, 0.4f);

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

    SearchWebCollection searcher = new SearchWebCollection(qaArgs.index);
    searcher.search(topics, qaArgs.output, similarity, qaArgs.hits, cascade, false, false);
    searcher.close();
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

    RetrieveSentences rs = new RetrieveSentences();
    rs.retrieveDocuments(qaArgs);
    rs.computeIDF(qaArgs);

    Map<String, Double> sentences = rs.computeIDF(qaArgs);

    Map.Entry<String, Double> maxEntry = null;
    for (Map.Entry<String, Double> entry : sentences.entrySet())
    {
      if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
      {
        maxEntry = entry;
      }
    }
    System.out.println("Answer: " + maxEntry.getKey());
  }
}
