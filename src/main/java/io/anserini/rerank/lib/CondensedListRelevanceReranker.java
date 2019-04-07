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

package io.anserini.rerank;

import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.FeatureVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_DOCID;
import static io.anserini.search.SearchCollection.BREAK_SCORE_TIES_BY_TWEETID;

public class CondensedListRelevanceReranker extends Rm3Reranker {
  private static final Logger LOG = LogManager.getLogger(CondensedListRelevanceReranker.class);

  public CondensedListRelevanceReranker(Analyzer analyzer, String field, int fbTerms, int fbDocs, float originalQueryWeight, boolean outputQuery) {
    super(analyzer,field, fbTerms,fbDocs, originalQueryWeight, outputQuery);
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context)  {

    assert(docs.documents.length == docs.scores.length);

    IndexSearcher searcher = context.getIndexSearcher();
    FeatureVector rm3 = estimateRM3Model(docs, context);

    Query finalQuery = buildFeedbackQuery(context,rm3);

    QueryRescorer rm3Rescoer = new RM3QueryRescorer(finalQuery);
    TopDocs rs;
    try {
      rs = rm3Rescoer.rescore(searcher,docs.topDocs,context.getSearchArgs().hits);
    } catch (IOException e) {
      e.printStackTrace();
      return docs;
    }

    return ScoredDocuments.fromTopDocs(rs, searcher);

  }


  @Override
  public String tag() {
    return "CLRm3(fbDocs="+getFbDocs()+",fbTerms="+getFbTerms()+",originalQueryWeight:"+getOriginalQueryWeight()+")";
  }



}

class RM3QueryRescorer extends QueryRescorer{
  public RM3QueryRescorer(Query query) {
    super(query);
  }

  @Override
  protected float combine(float firstPassScore, boolean secondPassMatches, float secondPassScore) {
    return secondPassScore;
  }
}

