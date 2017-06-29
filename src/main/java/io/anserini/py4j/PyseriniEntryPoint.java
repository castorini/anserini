package io.anserini.py4j;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.simple.Sentence;
import io.anserini.index.IndexUtils;
import io.anserini.qa.passage.IdfPassageScorer;
import io.anserini.qa.passage.PassageScorer;
import io.anserini.qa.passage.ScoredPassage;
import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.AnalyzerUtils;
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
import org.apache.lucene.store.FSDirectory;
import py4j.GatewayServer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

/**
 * @author s43moham on 06/03/17.
 * @project anserini
 */
public class PyseriniEntryPoint {

  private String indexDir = null;
  private IndexReader reader = null;
  private IndexUtils indexUtils = null;
  private PassageScorer passageScorer = null;

  public PyseriniEntryPoint() {}

  public void initializeWithIndex(String indexDir) throws Exception {
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    this.indexDir = indexDir;
    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    this.indexUtils = new IndexUtils(indexDir);
  }

  /**
   * Prints TREC submission file to the standard output stream.
   *
   * @param topics     queries
   * @param similarity similarity
   * @throws IOException
   * @throws ParseException
   */

  public Map<String, Float> search(SortedMap<Integer, String> topics, Similarity similarity, int numHits, RerankerCascade cascade,
                             boolean useQueryParser, boolean keepstopwords) throws IOException, ParseException {

    Map<String, Float> scoredDocs = new LinkedHashMap<>();
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);

    EnglishAnalyzer ea = keepstopwords ? new EnglishAnalyzer(CharArraySet.EMPTY_SET) : new EnglishAnalyzer();
    QueryParser queryParser = new QueryParser(FIELD_BODY, ea);
    queryParser.setDefaultOperator(QueryParser.Operator.OR);

    for (Map.Entry<Integer, String> entry : topics.entrySet()) {

      int qID = entry.getKey();
      String queryString = entry.getValue();
      Query query = useQueryParser ? queryParser.parse(queryString) :
              AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, ea, queryString);

      TopDocs rs = searcher.search(query, numHits);
      ScoreDoc[] hits = rs.scoreDocs;
      List<String> queryTokens = AnalyzerUtils.tokenize(ea, queryString);
      RerankerContext context = new RerankerContext(searcher, query, String.valueOf(qID), queryString,
              queryTokens, FIELD_BODY, null);
      ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);
      for (int i = 0; i < docs.documents.length; i++) {
        String docid = docs.documents[i].getField(FIELD_ID).stringValue();
        float score = docs.scores[i];
        scoredDocs.put(docid, score);
      }
    }

    return scoredDocs;
  }

  public Map<String, Float> search(String query, int numHits) throws IOException, ParseException {

    // for now, using BM25 similarity  - not branching on args.bm25 or args.ql
    float k1 = 0.9f;
    float b = 0.4f;
    Similarity similarity = new BM25Similarity(k1, b);

    // for now, creating Topics map and appending query and setting id=1
    SortedMap<Integer, String> topics = new TreeMap<>();
    int id = 1;
    topics.put(id, query);

    // for now, using IdentityReranker  - not branching on args.rm3
    RerankerCascade cascade = new RerankerCascade();
    cascade.add(new IdentityReranker());
    Map<String, Float> scoredDocs = search(topics, similarity, numHits, cascade, false, false);
    return scoredDocs;
  }

  public String getRawDocument(String docid) throws Exception {
    return indexUtils.getRawDocument(docid);
  }

  public List<String> getAllSentences(String query, int numHits) throws Exception {
    Map<String, Float> docScore = search(query, numHits);
    Map<String, Float> sentencesMap = new LinkedHashMap<>();
    TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    List<String> allSentences = new ArrayList<String>();

    for (Map.Entry<String, Float> doc : docScore.entrySet()) {
      List<Sentence> sentences = indexUtils.getSentDocument(doc.getKey());
      for (Sentence thisSent : sentences) {
        List<CoreLabel> tokens = tokenizerFactory.getTokenizer(new StringReader(thisSent.text())).tokenize();
        String tokenizedAnswer = tokens.stream()
                .map(CoreLabel::toString)
                .collect(Collectors.joining(" "));

        allSentences.add(tokenizedAnswer);
      }
    }

    return allSentences;
  }

  public List<String> getRankedPassages(String query, int numHits, int k) throws Exception {
    Map<String, Float> docScore = search(query, numHits);
    Map<String, Float> sentencesMap = new LinkedHashMap<>();
    TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");

    for (Map.Entry<String, Float> doc : docScore.entrySet()) {
      List<Sentence> sentences = indexUtils.getSentDocument(doc.getKey());

      for (Sentence thisSent : sentences) {
        List<CoreLabel> tokens = tokenizerFactory.getTokenizer(new StringReader(thisSent.text())).tokenize();
        String answerTokens = tokens.stream()
                .map(CoreLabel::toString)
                .collect(Collectors.joining(" "));
        sentencesMap.put(answerTokens, doc.getValue());
      }
    }

    passageScorer = new IdfPassageScorer(indexDir, k);
    String queryTokens = tokenizerFactory.getTokenizer(new StringReader(query)).tokenize().stream()
            .map(CoreLabel::toString)
            .collect(Collectors.joining(" "));
    passageScorer.score(query, sentencesMap);

    List<String> topSentences = new ArrayList<>();
    List<ScoredPassage> topPassages = passageScorer.extractTopPassages();

    for (ScoredPassage s : topPassages) {
      topSentences.add(s.getSentence() + "\t" + s.getScore());
    }

    return topSentences;
  }

  public String getTermIdfJSON(){
    return passageScorer.getTermIdfJSON().toString();
  }

  public String getTermIdfJSONs(List<String> sentList) throws Exception {
    passageScorer = new IdfPassageScorer(indexDir, 10);
    return passageScorer.getTermIdfJSON(sentList).toString();
  }

  public static void main(String[] argv) throws Exception {
    System.out.println("starting Gateway Server...");
    GatewayServer gatewayServer = new GatewayServer(new PyseriniEntryPoint());
    gatewayServer.start();
    System.out.println("started!");
  }
}
