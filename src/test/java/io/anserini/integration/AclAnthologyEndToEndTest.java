/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.integration;

import io.anserini.collection.AclAnthology;
import io.anserini.index.IndexCollection;
import io.anserini.index.generator.AclAnthologyGenerator;

import java.util.Map;

public class AclAnthologyEndToEndTest extends EndToEndTest {
  @Override
  protected IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/acl";
    indexArgs.collectionClass = AclAnthology.class.getSimpleName();
    indexArgs.generatorClass = AclAnthologyGenerator.class.getSimpleName();

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 3;
    docFieldCount = -1; // Documents may have variable number of fields, so don't bother checking.

    referenceDocs.put("E17-1003", Map.of(
        "contents",
        "Exploring Different Dimensions of Attention for Uncertainty Detection Neural networks with attention " +
            "have proven effective for many natural language processing tasks. In this paper, we develop attention " +
            "mechanisms for uncertainty detection. In particular, we generalize standardly used attention mechanisms " +
            "by introducing external attention and sequence-preserving attention. These novel architectures differ " +
            "from standard approaches in that they use external resources to compute attention weights and preserve " +
            "sequence information. We compare them to other configurations along different dimensions of attention. " +
            "Our novel architectures set the new state of the art on a Wikipedia benchmark dataset and perform " +
            "similar to the state-of-the-art model on a biomedical benchmark which uses a large set of linguistic " +
            "features.",
        "raw",
        "Exploring Different Dimensions of Attention for Uncertainty Detection Neural networks with attention " +
            "have proven effective for many natural language processing tasks. In this paper, we develop attention " +
            "mechanisms for uncertainty detection. In particular, we generalize standardly used attention mechanisms " +
            "by introducing external attention and sequence-preserving attention. These novel architectures differ " +
            "from standard approaches in that they use external resources to compute attention weights and preserve " +
            "sequence information. We compare them to other configurations along different dimensions of attention. " +
            "Our novel architectures set the new state of the art on a Wikipedia benchmark dataset and perform " +
            "similar to the state-of-the-art model on a biomedical benchmark which uses a large set of linguistic " +
            "features."));
    referenceDocs.put("C00-1003", Map.of(
        "contents",
        "Selectional Restrictions in HPSG ",
        "raw",
        "Selectional Restrictions in HPSG "));
    referenceDocs.put("C00-1007", Map.of(
        "contents",
        "Exploiting a Probabilistic Hierarchical Model for Generation ",
        "raw",
        "Exploiting a Probabilistic Hierarchical Model for Generation "));

    fieldNormStatusTotalFields = 13;
    termIndexStatusTermCount = 241;
    termIndexStatusTotFreq = 288;
    storedFieldStatusTotalDocCounts = 3;
    termIndexStatusTotPos = 339;
    storedFieldStatusTotFields = 67;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/acl_topics.tsv";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 C00-1007 1 0.294000 Anserini",
        "1 Q0 E17-1003 2 0.186100 Anserini",
        "2 Q0 C00-1003 1 0.622700 Anserini"});
  }
}
