package io.anserini.py4j;

import edu.stanford.nlp.simple.Sentence;
import io.anserini.index.IndexUtils;
import io.anserini.qa.passage.PassageScorer;
import io.anserini.qa.passage.ScoredPassage;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.AnalyzerUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.queryparser.classic.ParseException;
import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.SortedMap;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import io.anserini.qa.passage.IdfPassageScorer;

import py4j.GatewayServer;

/**
 * @author s43moham on 06/03/17.
 * @project anserini
 */
public class PyseriniEntryPoint {

  private String indexDir = null;
  private IndexReader reader = null;
  private IndexUtils indexUtils = null;

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

  public List<String> search(SortedMap<Integer, String> topics, Similarity similarity, int numHits, RerankerCascade cascade,
                             boolean useQueryParser, boolean keepstopwords) throws IOException, ParseException {

    List<String> docids = new ArrayList<String>();
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
        docids.add(docid);
      }
    }

    return docids;
  }

  public List<String> search(String query, int numHits) throws IOException, ParseException {

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

    List<String> docids = search(topics, similarity, numHits, cascade, false, false);
    return docids;
  }

  public String getRawDocument(String docid) throws Exception {
    return indexUtils.getRawDocument(docid);
  }

  public List<String> getRankedPassages(String query, int numHits, int k) throws Exception {
    List<String> docids = search(query, numHits);
    List<String> sentencesList = new ArrayList<>();
    for (String docid : docids) {
      List<Sentence> sentences = indexUtils.getSentDocument(docid);
      for (Sentence sent : sentences) {
        sentencesList.add(sent.text());
      }
    }

    PassageScorer passageScorer = new IdfPassageScorer(indexDir, k);
    passageScorer.score(sentencesList, "");
    List<String> topSentences = new ArrayList<>();
    List<ScoredPassage> topPassages = passageScorer.extractTopPassages();
    for (ScoredPassage s: topPassages) {
      topSentences.add(s.getSentence());
    }
    return topSentences;
  }

  public static void main(String[] argv) throws Exception {
    System.out.println("starting Gateway Server...");
    GatewayServer gatewayServer = new GatewayServer(new PyseriniEntryPoint());
    gatewayServer.start();
    System.out.println("started!");
  }
}
