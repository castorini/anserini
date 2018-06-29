/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.rerank.lib;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.search.SearchArgs;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;
import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_TWEETID;

/*
 * Axiomatic reranking or Axiomatic semantic relevance feedback model.
 *
 * NOTE: This model supports finding expansion terms using another index. But please make sure
 * that both indexes have the same stemming rules and were built using the same Generator
 * (see {@link io.anserini.index.generator.LuceneDocumentGenerator}) or the model won't work properly.
 * For example, we may stem tweets differently from newswire corpus (TweetsAnalyzer vs. EnglishAnalyzer).
 * Then it is better NOT to using a newswire index for expansion terms and feed them to the original
 * tweets index.
 *
 */
public class AxiomReranker<T> implements Reranker<T> {
  private static final Logger LOG = LogManager.getLogger(AxiomReranker.class);

  private final String field; // from which field we look for the expansion terms, e.g. "body"
  private final boolean deterministic;  // whether the expansion terms are deterministically picked
  private final long seed;
  private final String externalIndexPath;  // Axiomatic reranking can opt to use
                                           // external sources for searching the expansion
                                           // terms. Typically, we build another index
                                           // separately and include its information here.
  private final ScoreDoc[] docidsCache;

  private final int R; // factor that used in extracting random documents, we will extract R*M randomly select documents
  private final int M; // number of top documents in initial results
  private final int L = 1000; // top similar terms
  private final int K = 20; // number of expansion terms
  private final float beta; // scaling parameter

  public AxiomReranker(String field, SearchArgs args) throws IOException {
    this.field = field;
    this.deterministic = args.axiom_deterministic;
    this.seed = args.axiom_seed;
    this.R = args.axiom_r;
    this.M = args.axiom_m;
    this.beta = args.axiom_beta;
    this.externalIndexPath = args.axiom_external_index.trim();

    if (this.deterministic && this.R > 0) {
      this.docidsCache = makeDocidsCache(args);
    } else {
      this.docidsCache = null;
    }
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext<T> context) {
    Preconditions.checkState(docs.documents.length == docs.scores.length);

    try {
      // First to search against external index if it is not null
      docs = processExternalContext(docs, context);
      // Select R*M docs from the original ranking list as the reranking pool
      Set<Integer> usedDocs = selectDocs(docs, context);
      // Extract an inverted list from the reranking pool
      Map<String, Set<Integer>> termInvertedList = extractTerms(usedDocs, context, null);
      // Calculate all the terms in the reranking pool and pick top K of them
      Map<String, Double> expandedTermScores = calTermScore(termInvertedList, context);
      StringBuilder builder = new StringBuilder();
      for (Map.Entry<String, Double> termScore : expandedTermScores.entrySet()) {
        String term = termScore.getKey();
        double score = termScore.getValue();
        builder.append(term + "^" + score + " ");
      }
      String queryText = builder.toString().trim();

      if (queryText.isEmpty()) {
        LOG.info("[Empty Expanded Query]: " + context.getQueryTokens());
        queryText = context.getQueryText();
      }

      StandardQueryParser p = new StandardQueryParser();
      Query nq = null;
      nq = p.parse(queryText, this.field);
      LOG.info("Original Query: " + context.getQueryTokens());
      LOG.info("Running new query: " + nq);

      return searchTopDocs(nq, context);
    } catch (Exception e) {
      e.printStackTrace();
      return docs;
    }
  }

  /**
   * Please note that the query in the context is always the keywordQuery w/o filter!
   */
  private ScoredDocuments searchTopDocs(Query query, RerankerContext<T> context) throws IOException {
    IndexSearcher searcher = context.getIndexSearcher();
    Query finalQuery = null;
    if (query == null) { // we are dealing with the external index and we DONOT apply filter to it.
      finalQuery = context.getQuery();
    } else {
      if (context.getFilter() != null) {
        // If there's a filter condition, we need to add in the constraint.
        // Otherwise, just use the original query.
        BooleanQuery.Builder bqBuilder = new BooleanQuery.Builder();
        bqBuilder.add(context.getFilter(), BooleanClause.Occur.FILTER);
        bqBuilder.add(query, BooleanClause.Occur.MUST);
        finalQuery = bqBuilder.build();
      } else {
        finalQuery = query;
      }
    }

    TopDocs rs;
    // Figure out how to break the scoring ties.
    if (context.getSearchArgs().arbitraryScoreTieBreak) {
      rs = searcher.search(finalQuery, context.getSearchArgs().hits);
    } else if (context.getSearchArgs().searchtweets) {
      rs = searcher.search(finalQuery, context.getSearchArgs().hits, BREAK_SCORE_TIES_BY_TWEETID, true, true);
    } else {
      rs = searcher.search(finalQuery, context.getSearchArgs().hits, BREAK_SCORE_TIES_BY_DOCID, true, true);
    }

    return ScoredDocuments.fromTopDocs(rs, searcher);
  }

  /**
   * If the result is deterministic we can cache all the docids. All queries can share this
   * cache.
   */
  private ScoreDoc[] makeDocidsCache(SearchArgs args) throws IOException {
    String index = args.axiom_external_index.trim().isEmpty() ? args.index : args.axiom_external_index.trim();
    Path indexPath = Paths.get(index);
    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(this.externalIndexPath + " does not exist or is not a directory.");
    }
    IndexReader reader = DirectoryReader.open(FSDirectory.open(indexPath));
    IndexSearcher searcher = new IndexSearcher(reader);
    return searcher.search(new MatchAllDocsQuery(), reader.maxDoc(),
      args.searchtweets ? BREAK_SCORE_TIES_BY_TWEETID : BREAK_SCORE_TIES_BY_DOCID,
      true, true).scoreDocs;
  }

  /**
   * If the external reranking context is not null we will first search against the external
   * index and return the top ranked documents.
   *
   * @param docs The initial ranking results against target index. We will return them if external
   *             index is null.
   *
   * @return Top ranked ScoredDocuments from searching external index
   */
  private ScoredDocuments processExternalContext(ScoredDocuments docs, RerankerContext<T> context) throws IOException {
    if (!this.externalIndexPath.isEmpty()) {
      Path indexPath = Paths.get(this.externalIndexPath);
      if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
        throw new IllegalArgumentException(this.externalIndexPath + " does not exist or is not a directory.");
      }
      IndexReader reader = DirectoryReader.open(FSDirectory.open(indexPath));
      IndexSearcher searcher = new IndexSearcher(reader);
      searcher.setSimilarity(context.getIndexSearcher().getSimilarity(true));

      SearchArgs args = new SearchArgs();
      args.hits = this.M;
      args.arbitraryScoreTieBreak = context.getSearchArgs().arbitraryScoreTieBreak;
      args.searchtweets = context.getSearchArgs().searchtweets;

      RerankerContext<T> externalContext = new RerankerContext<>(searcher, context.getQueryId(), context.getQuery(),
        context.getQueryText(), context.getQueryTokens(), context.getFilter(), args);

      return searchTopDocs(null, externalContext);
    } else {
      return docs;
    }
  }

  /**
   * Select R*M docs from the ranking results and the index as the reranking pool.
   * The process is:
   * 1. Keep the top M documents in the original ranking list
   * 2. Randomly pick (R-1)*M documents from the rest of the index so in total we have R*M documents
   *
   * @param docs The initial ranking results
   * @param context An instance of RerankerContext
   * @return a Set of R*M document Ids
   */
  @VisibleForTesting
  private Set<Integer> selectDocs(ScoredDocuments docs, RerankerContext<T> context)
    throws IOException {
    Set<Integer> docidSet = new HashSet<>(Arrays.asList(ArrayUtils.toObject(
      Arrays.copyOfRange(docs.ids, 0, Math.min(this.M, docs.ids.length)))));
    long targetSize = this.R * this.M;

    if (docidSet.size() < targetSize) {
      IndexReader reader;
      if (!this.externalIndexPath.isEmpty()) {
        Path indexPath = Paths.get(this.externalIndexPath);
        if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
          throw new IllegalArgumentException(this.externalIndexPath + " does not exist or is not a directory.");
        }
        reader = DirectoryReader.open(FSDirectory.open(indexPath));
      } else {
        IndexSearcher searcher = context.getIndexSearcher();
        reader = searcher.getIndexReader();
      }
      int availableDocsCnt = reader.getDocCount(this.field);
      if (this.deterministic) { // internal docid cannot be relied due to multi-threads indexing,
                                // we have to rely on external docid here
        Random random = new Random(this.seed);
        while (docidSet.size() < targetSize) {
          docidSet.add(this.docidsCache[random.nextInt(this.docidsCache.length)].doc);
        }
      } else {
        Random random = new Random();
        while (docidSet.size() < targetSize) {
          docidSet.add(random.nextInt(availableDocsCnt));
        }
      }
    }

    return docidSet;
  }

  /**
   * Extract ALL the terms from the documents pool.
   *
   * @param docIds The reranking pool, see {@link #selectDocs} for explanations
   * @param context An instance of RerankerContext
   * @param filterPattern A Regex pattern that terms are collected only they matches the pattern, could be null
   * @return A Map of <term -> Set<docId>> kind of a small inverted list where the Set of docIds is where the term occurs
   */
  @VisibleForTesting
  private Map<String, Set<Integer>> extractTerms(Set<Integer> docIds, RerankerContext<T> context,
                                                 Pattern filterPattern) throws Exception, IOException {
    IndexReader reader;
    if (!this.externalIndexPath.isEmpty()) {
      Path indexPath = Paths.get(this.externalIndexPath);
      if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
        throw new IllegalArgumentException(this.externalIndexPath + " does not exist or is not a directory.");
      }
      reader = DirectoryReader.open(FSDirectory.open(indexPath));
    } else {
      IndexSearcher searcher = context.getIndexSearcher();
      reader = searcher.getIndexReader();
    }
    Map<String, Set<Integer>> termDocidSets = new HashMap<>();
    for (int docid : docIds) {
      Terms terms = reader.getTermVector(docid, LuceneDocumentGenerator.FIELD_BODY);
      if (terms == null) {
        LOG.info("Document vector not stored for docid: " + docid);
        continue;
      }
      TermsEnum te = terms.iterator();
      if (te == null) {
        LOG.info("Document vector not stored for docid: " + docid);
        continue;
      }
      while ((te.next()) != null) {
        String term = te.term().utf8ToString();
        // We do some noisy filtering here ... pure empirical heuristic
        if (term.length() < 2) continue;
        if (!term.matches("[a-z]+")) continue;
        if (filterPattern == null || filterPattern.matcher(term).matches()) {
          if (!termDocidSets.containsKey(term)) {
            termDocidSets.put(term, new HashSet<>());
          }
          termDocidSets.get(term).add(docid);
        }
      }
    }
    return termDocidSets;
  }

  /**
   * Calculate the scores (weights) of each term that occured in the reranking pool.
   * The Process:
   * 1. For each query term, calculate its score for each term in the reranking pool. the score
   * is calcuated as P(both occurs)*log{P(both occurs)/P(t1 occurs)/P(t2 occurs)}
   * + P(both not occurs)*log{P(both not occurs)/P(t1 not occurs)/P(t2 not occurs)}
   * + P(t1 occurs t2 not occurs)*log{P(t1 occurs t2 not occurs)/P(t1 occurs)/P(t2 not occurs)}
   * + P(t1 not occurs t2 occurs)*log{P(t1 not occurs t2 occurs)/P(t1 not occurs)/P(t2 occurs)}
   * 2. For each query term the scores of every other term in the reranking pool are stored in a
   * PriorityQueue, only the top L are kept.
   * 3. Add the scores of the same term together and pick the top K ones.
   *
   * @param termInvertedList A Map of <term -> Set<docId>> where the Set of docIds is where the term occurs
   * @param context An instance of RerankerContext
   * @return Map<String, Double> Top terms and their weight scores in a HashMap
   */
  @VisibleForTesting
  private Map<String, Double> calTermScore(
    Map<String, Set<Integer>> termInvertedList, RerankerContext<T> context) {
    class ScoreComparator implements Comparator<Pair<String, Double>> {
      public int compare(Pair<String, Double> a, Pair<String, Double> b) {
        return Double.compare(b.getRight(), a.getRight());
      }
    }

    //calculate the Mutual Information between term with each query term
    List<String> queryTerms = context.getQueryTokens();
    Map<String, Integer> queryTermsCounts = new HashMap<>();
    for (String qt : queryTerms) {
      queryTermsCounts.put(qt, queryTermsCounts.getOrDefault(qt, 0) + 1);
    }

    Set<Integer> allDocIds = new HashSet<>();
    for (Set<Integer> s : termInvertedList.values()) {
      allDocIds.addAll(s);
    }
    int docIdsCount = allDocIds.size();

    // Each priority queue corresponds to a query term: The p-queue itself stores all terms
    // in the reranking pool and their reranking scores to the query term.
    List<PriorityQueue<Pair<String, Double>>> allTermScoresPQ = new ArrayList<>();
    for (Map.Entry<String, Integer> q : queryTermsCounts.entrySet()) {
      String queryTerm = q.getKey();
      int qtf = q.getValue();
      if (termInvertedList.containsKey(queryTerm)) {
        PriorityQueue<Pair<String, Double>> termScorePQ = new PriorityQueue<>(new ScoreComparator());
        double selfMI = computeMutualInformation(termInvertedList.get(queryTerm), termInvertedList.get(queryTerm), docIdsCount);
        for (Map.Entry<String, Set<Integer>> termEntry : termInvertedList.entrySet()) {
          double score;
          if (termEntry.getKey().equals(queryTerm)) { // The mutual information to itself will always be 1
            score = 1.0 * qtf;
          } else {
            double crossMI = computeMutualInformation(termInvertedList.get(queryTerm), termEntry.getValue(), docIdsCount);
            score = 1.0 * beta * qtf * crossMI / selfMI;
          }
          termScorePQ.add(Pair.of(termEntry.getKey(), score));
        }
        allTermScoresPQ.add(termScorePQ);
      }
    }

    Map<String, Double> aggTermScores = new HashMap<>();
    for (PriorityQueue<Pair<String, Double>> termScores : allTermScoresPQ) {
      for (int i = 0; i < Math.min(termScores.size(), this.L); i++) {
        Pair<String, Double> termScore = termScores.poll();
        String term = termScore.getLeft();
        Double score = termScore.getRight();
        if (score - 0.0 > 1e-8) {
          aggTermScores.put(term, aggTermScores.getOrDefault(term, 0.0) + score);
        }
      }
    }
    PriorityQueue<Pair<String, Double>> termScoresPQ = new PriorityQueue<>(new ScoreComparator());
    for (Map.Entry<String, Double> termScore : aggTermScores.entrySet()) {
      termScoresPQ.add(Pair.of(termScore.getKey(), termScore.getValue() / queryTerms.size()));
    }
    Map<String, Double> resultTermScores = new HashMap<>();
    for (int i = 0; i < Math.min(termScoresPQ.size(), this.K); i++) {
      Pair<String, Double> termScore = termScoresPQ.poll();
      String term = termScore.getKey();
      double score = termScore.getValue();
      resultTermScores.put(term, score);
    }

    return resultTermScores;
  }

  @VisibleForTesting
  private double computeMutualInformation(Set<Integer> docidsX, Set<Integer> docidsY, int totalDocCount) {
    int x1 = docidsX.size(), y1 = docidsY.size(); //document that x occurres
    int x0 = totalDocCount - x1, y0 = totalDocCount - y1; //document num that x doesn't occurres

    if (x1 == 0 || x0 == 0 || y1 == 0 || y0 == 0) {
      return 0;
    }

    float pX0 = 1.0f * x0 / totalDocCount;
    float pX1 = 1.0f * x1 / totalDocCount;
    float pY0 = 1.0f * y0 / totalDocCount;
    float pY1 = 1.0f * y1 / totalDocCount;

    //get the intersection of docIds
    Set<Integer> docidsXClone = new HashSet<>(docidsX); // directly operate on docidsX will change it permanently
    docidsXClone.retainAll(docidsY);
    int numXY11 = docidsXClone.size();
    int numXY10 = numXY10 = x1 - numXY11;    //doc num that x occurs but y doesn't
    int numXY01 = y1 - numXY11;    // doc num that y occurs but x doesn't
    int numXY00 = totalDocCount - numXY11 - numXY10 - numXY01; //doc num that neither x nor y occurs

    float pXY11 = 1.0f * numXY11 / totalDocCount;
    float pXY10 = 1.0f * numXY10 / totalDocCount;
    float pXY01 = 1.0f * numXY01 / totalDocCount;
    float pXY00 = 1.0f * numXY00 / totalDocCount;

    double m00 = 0, m01 = 0, m10 = 0, m11 = 0;
    if (pXY00 != 0) m00 = pXY00 * Math.log(pXY00 / (pX0 * pY0));
    if (pXY01 != 0) m01 = pXY01 * Math.log(pXY01 / (pX0 * pY1));
    if (pXY10 != 0) m10 = pXY10 * Math.log(pXY10 / (pX1 * pY0));
    if (pXY11 != 0) m11 = pXY11 * Math.log(pXY11 / (pX1 * pY1));
    return m00 + m10 + m01 + m11;
  }
}
