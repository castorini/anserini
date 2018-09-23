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

package io.anserini.ltr.feature;

import io.anserini.ltr.feature.base.*;
import io.anserini.ltr.feature.twitter.*;
import io.anserini.rerank.RerankerContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A feature extractor.
 */
public interface FeatureExtractor<T> {
  //********************************************************
  // TODO normalize names
  final Map<String, Class<?>> EXTRACTOR_MAP = new HashMap<String, Class<?>>() {{
    put("AvgICTF", AvgICTFFeatureExtractor.class);
    put("SimplifiedClarityScore", SimplifiedClarityFeatureExtractor.class);
    put("PMIFeature", PMIFeatureExtractor.class);
    put("AvgSCQ", SCQFeatureExtractor.class);
    put("SumMatchingTf", SumMatchingTf.class);
    put("UnigramsFeatureExtractor", UnigramFeatureExtractor.class);
    put("AvgIDF", AvgIDFFeatureExtractor.class);
    put("BM25Feature", BM25FeatureExtractor.class);
    put("DocSize", DocSizeFeatureExtractor.class);
    put("MatchingTermCount", MatchingTermCount.class);
    put("QueryLength", QueryLength.class);
    put("SumTermFrequency", TermFrequencyFeatureExtractor.class);
    put("TFIDF", TFIDFFeatureExtractor.class);
    put("UniqueQueryTerms", UniqueTermCount.class);
    put("UnorderedSequentialPairs", UnorderedSequentialPairsFeatureExtractor.class);
    put("OrderedSequentialPairs", OrderedSequentialPairsFeatureExtractor.class);
    put("TwitterHashtagCount", HashtagCount.class);
    put("IsTweetReply", IsTweetReply.class);
    put("TwitterLinkCount", LinkCount.class);
    put("TwitterFollowerCount", TwitterFollowerCount.class);
    put("TwitterFriendCount", TwitterFriendCount.class);
  }};

  float extract(Document doc, Terms terms, RerankerContext<T> context);

  String getName();

}
