/**
 * Anserini: A toolkit for reproducible information retrieval research built on Lucene
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

import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.util.AnalyzerUtils;
import io.anserini.analysis.TweetAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SimpleSearcher implements Closeable {
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(LuceneDocumentGenerator.FIELD_ID, SortField.Type.STRING_VAL));
  public static final Sort BREAK_SCORE_TIES_BY_TWEETID =
      new Sort(SortField.FIELD_SCORE,
          new SortField(TweetGenerator.StatusField.ID_LONG.name, SortField.Type.LONG, true));
  private static final Logger LOG = LogManager.getLogger(SimpleSearcher.class);
  private final IndexReader reader;
  private Similarity similarity;
  private Analyzer analyzer;
  private RerankerCascade cascade;
  private boolean searchtweets;
  private boolean isRerank;

  protected class Result {
    public String docid;
    public int ldocid;
    public float score;
    public String content;

    public Result(String docid, int ldocid, float score, String content) {
      this.docid = docid;
      this.ldocid = ldocid;
      this.score = score;
      this.content = content;
    }
  }

  public SimpleSearcher(String indexDir) throws IOException {
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    this.similarity = new LMDirichletSimilarity(1000.0f);
    this.analyzer = new EnglishAnalyzer();
    this.searchtweets = false;
    this.isRerank = false;
    setDefaultReranker();
  }

  public void setSearchTweets(boolean flag) {
     this.searchtweets = flag;
     this.analyzer = flag? new TweetAnalyzer(true) : new EnglishAnalyzer();
  }

  public void setRM3Reranker() {
    setRM3Reranker(10, 10, 0.5f, false);
  }

  public void setRM3Reranker(int fbTerms, int fbDocs, float originalQueryWeight) {
    setRM3Reranker(fbTerms, fbDocs, originalQueryWeight, false);
  }

  public void setDefaultReranker() {
    isRerank = false;
    cascade = new RerankerCascade();
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  public void setRM3Reranker(int fbTerms, int fbDocs, float originalQueryWeight, boolean rm3_outputQuery) {
    isRerank = true;
    cascade = new RerankerCascade();
    cascade.add(new Rm3Reranker(this.analyzer, LuceneDocumentGenerator.FIELD_BODY, fbTerms, fbDocs, originalQueryWeight, rm3_outputQuery));
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  public void setLMDirichletSimilarity(float mu) {
    this.similarity = new LMDirichletSimilarity(mu);
  }

  public void setLMJelinekMercerSimilarity(float lambda) {
    this.similarity = new LMJelinekMercerSimilarity(lambda);
  }

  public void setBM25Similarity(float k1, float b) {
    this.similarity = new BM25Similarity(k1, b);
  }

  public void setDFRSimilarity(float c) {
    this.similarity = new DFRSimilarity(new BasicModelP(), new AfterEffectL(), new NormalizationH2(c));
  }

  public void setIBSimilarity(float c) {
    this.similarity = new IBSimilarity(new DistributionSPL(), new LambdaDF(), new NormalizationH2(c));
  }

  public void setF2ExpSimilarity(float s) {
    this.similarity = new AxiomaticF2EXP(s);
  }

  public void setF2LogSimilarity(float s) {
    this.similarity = new AxiomaticF2LOG(s);
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public Result[] search(String q) throws IOException {
    return search(q, 10);
  }

  public Result[] search(String q, int k) throws IOException {
    return search(q, k, -1);
  }

  public Result[] search(String q, int k, long t) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);
    Query query = new BagOfWordsQueryGenerator().buildQuery(LuceneDocumentGenerator.FIELD_BODY, analyzer, q);
    List<String> queryTokens = AnalyzerUtils.tokenize(analyzer, q);
    
    SearchArgs searchArgs = new SearchArgs();
    searchArgs.arbitraryScoreTieBreak = false;
    searchArgs.hits = k;
    searchArgs.searchtweets = searchtweets;

    TopDocs rs = new TopDocs(0, new ScoreDoc[]{}, Float.NaN);
    RerankerContext context;
    if (searchtweets) {
      if (t > 0) {
        // Do not consider the tweets with tweet ids that are beyond the queryTweetTime
        // <querytweettime> tag contains the timestamp of the query in terms of the
        // chronologically nearest tweet id within the corpus
        Query filter = LongPoint.newRangeQuery(TweetGenerator.StatusField.ID_LONG.name, 0L, t);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(filter, BooleanClause.Occur.FILTER);
        builder.add(query, BooleanClause.Occur.MUST);
        Query compositeQuery = builder.build();
        rs = searcher.search(compositeQuery, isRerank ? searchArgs.rerankcutoff : k, BREAK_SCORE_TIES_BY_TWEETID, true, true);
        context = new RerankerContext<>(searcher, null, compositeQuery, null, q, queryTokens, filter, searchArgs);
      } else {
        rs = searcher.search(query, isRerank ? searchArgs.rerankcutoff : k, BREAK_SCORE_TIES_BY_TWEETID, true, true);
        context = new RerankerContext<>(searcher, null, query, null, q, queryTokens, null, searchArgs);
      }
    } else {
      rs = searcher.search(query, isRerank ? searchArgs.rerankcutoff : k, BREAK_SCORE_TIES_BY_DOCID, true, true);
        context = new RerankerContext<>(searcher, null, query, null, q, queryTokens, null, searchArgs);
    }

    ScoredDocuments hits = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

    Result[] results = new Result[hits.ids.length];
    for (int i = 0; i < hits.ids.length; i++) {
      Document doc = hits.documents[i];
      String docid = doc.getField(LuceneDocumentGenerator.FIELD_ID).stringValue();
      IndexableField field = doc.getField(LuceneDocumentGenerator.FIELD_RAW);
      String content = field == null ? null : field.stringValue();
      results[i] = new Result(docid, hits.ids[i], hits.scores[i], content);
    }

    return results;
  }

  public String doc(int ldocid) {
    Document doc;
    try {
      doc = reader.document(ldocid);
    } catch (IOException e) {
      return null;
    }

    IndexableField field = doc.getField(LuceneDocumentGenerator.FIELD_RAW);
    return field == null ? null : field.stringValue();
  }
}
