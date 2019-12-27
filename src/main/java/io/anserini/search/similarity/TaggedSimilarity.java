/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.search.similarity;

import org.apache.lucene.search.similarities.Similarity;

/**
 * TaggedSimilarity wraps Lucene's Similarity with an optional String tag.
 * The tag will be used as part of the output file name if multiple search parameters are given.
 * See @see #SearchCollection
 */
public class TaggedSimilarity {
  public Similarity similarity;
  public String tag; // params tag. use similarity.toString() if one needs more info (e.g. model name + params)
  
  public TaggedSimilarity(Similarity similarity, String tag) {
    this.similarity = similarity;
    this.tag = tag;
  }
}
