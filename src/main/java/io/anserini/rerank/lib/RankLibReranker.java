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

import ciir.umass.edu.learning.DataPoint;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.RankerFactory;
import io.anserini.ltr.BaseFeatureExtractor;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.Result;
import io.anserini.rerank.ScoredDocuments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.util.*;

/**
 * This reranker class will load in a RankLib model and then score and rerank the documents
 * using that
 */
public class RankLibReranker<T> implements Reranker<T> {
  private static final Logger LOG = LogManager.getLogger(RankLibReranker.class);

  private static final RankerFactory FACTORY = new RankerFactory();
  private final Ranker ranker;
  private final FeatureExtractors extractors;
  private final String termsField;

  private DataPoint convertToDataPoint(Document doc, RerankerContext<T> context) {
    Terms terms = null;
    try {
      terms = MultiFields.getTerms(context.getIndexSearcher().getIndexReader(), this.termsField);
    } catch (IOException e) {
      LOG.error("Unable to retrieve term vectors");
    }

    float[] features = this.extractors.extractAll(doc, terms, context);
    String rankLibEntryString = BaseFeatureExtractor.constructOutputString("0", 0, "0", features);
    DataPoint dp = new DataPoint(rankLibEntryString);
    return dp;
  }

  public RankLibReranker(String modelFile, String termsField, FeatureExtractors extractors) {
    this.ranker = FACTORY.loadRanker(modelFile);
    this.extractors = extractors;
    this.termsField = termsField;
  }

  public RankLibReranker (String modelFile, String termsField, String extractorDefinition) throws Exception {
    this.ranker = FACTORY.loadRanker(modelFile);
    this.extractors = FeatureExtractors.loadExtractor(extractorDefinition);
    this.termsField = termsField;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext<T> context) {
    // Used to hold our rescored docs
    ScoredDocuments rerankedDocs = new ScoredDocuments();
    int numResults = docs.documents.length;
    rerankedDocs.documents = new Document[numResults];
    rerankedDocs.ids = new int[numResults];
    rerankedDocs.scores = new float[numResults];

    SortedSet<Result> results = new TreeSet<>();

    // To use the rank lib scoring models, we need to construct DataPoint objects for scoring
    // So we need to construct each feature vector in string representation then
    // parse it...
    for (int i = 0; i < numResults; i++) {
      DataPoint dp = convertToDataPoint(docs.documents[i], context);
      float score = (float) this.ranker.eval(dp);
      results.add(new Result(docs.documents[i], i, score, docs.ids[i]));
    }

    int index = 0;
    for (Result result : results) {
      rerankedDocs.documents[index] = result.document;
      rerankedDocs.ids[index] = result.id;
      rerankedDocs.scores[index] = result.score;
      index++;
    }

    return rerankedDocs;
  }
}
