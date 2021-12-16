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

package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AclAnthologyTest extends DocumentCollectionTest<AclAnthology.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/acl/");
    collection = new AclAnthology(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/acl/papers/segment1.yaml");
    Path segment2 = Paths.get("src/test/resources/sample_docs/acl/papers/segment2.yaml");

    segmentPaths.add(segment1);
    segmentPaths.add(segment2);

    segmentDocCounts.put(segment1, 2);
    segmentDocCounts.put(segment2, 1);

    totalSegments = 2;
    totalDocs = 3;

    HashMap<String, String> doc1 = new HashMap<>();
    doc1.put("id", "C00-1003");
    doc1.put("authors", "Ion Androutsopoulos Robert Dale");
    doc1.put("author_string", "Ion Androutsopoulos, Robert Dale");
    doc1.put("bibkey", "androutsopoulos-dale-2000-selectional");
    doc1.put("bibtype", "inproceedings");
    doc1.put("booktitle", "COLING 2000 Volume 1: The 18th International Conference on Computational Linguistics");
    doc1.put("paper_id", "3");
    doc1.put("parent_volume_id", "C00-1");
    doc1.put("pdf", "https://www.aclweb.org/anthology/C00-1003.pdf");
    doc1.put("thumbnail", "https://www.aclweb.org/anthology/thumb/C00-1003.jpg");
    doc1.put("title", "Selectional Restrictions in HPSG");
    doc1.put("url", "https://www.aclweb.org/anthology/C00-1003");
    doc1.put("contents", "Selectional Restrictions in HPSG ");
    doc1.put("sigs", "");
    doc1.put("venues", "COLING");
    expected.put("C00-1003", doc1);

    HashMap<String, String> doc2 = new HashMap<>();
    doc2.put("id", "C00-1007");
    doc2.put("authors", "Srinivas Bangalore Owen Rambow");
    doc2.put("title", "Exploiting a Probabilistic Hierarchical Model for Generation");
    doc2.put("contents", "Exploiting a Probabilistic Hierarchical Model for Generation ");
    doc2.put("sigs", "");
    doc2.put("venues", "COLING");
    expected.put("C00-1007", doc2);

    HashMap<String, String> doc3 = new HashMap<>();
    doc3.put("id", "E17-1003");
    doc3.put("address", "Valencia, Spain");
    doc3.put("authors", "Heike Adel Hinrich Schütze");
    doc3.put("author_string", "Heike Adel, Hinrich Schütze");
    doc3.put("bibkey", "adel-schutze-2017-exploring");
    doc3.put("bibtype", "inproceedings");
    doc3.put("booktitle", "Proceedings of the 15th Conference of the European Chapter of the Association " +
      "for Computational Linguistics: Volume 1, Long Papers");
    doc3.put("month", "April");
    doc3.put("year", "2017");
    doc3.put("page_first", "22");
    doc3.put("page_last", "34");
    doc3.put("paper_id", "3");
    doc3.put("parent_volume_id", "E17-1");
    doc3.put("pdf", "https://www.aclweb.org/anthology/E17-1003.pdf");
    doc3.put("publisher", "Association for Computational Linguistics");
    doc3.put("thumbnail", "https://www.aclweb.org/anthology/thumb/E17-1003.jpg");
    doc3.put("title", "Exploring Different Dimensions of Attention for Uncertainty Detection");
    doc3.put("url", "https://www.aclweb.org/anthology/E17-1003");
    doc3.put("contents", "Exploring Different Dimensions of Attention for Uncertainty Detection " +
      "Neural networks with attention have proven effective for many natural " +
      "language processing tasks. In this paper, we develop attention mechanisms for " +
      "uncertainty detection. In particular, we generalize standardly used attention " +
      "mechanisms by introducing external attention and sequence-preserving attention. " +
      "These novel architectures differ from standard approaches in that they use external " +
      "resources to compute attention weights and preserve sequence information. We compare " +
      "them to other configurations along different dimensions of attention. Our novel " +
      "architectures set the new state of the art on a Wikipedia benchmark dataset and " +
      "perform similar to the state-of-the-art model on a biomedical benchmark which " +
      "uses a large set of linguistic features.");
    doc3.put("sigs", "");
    doc3.put("venues", "EACL");
    expected.put("E17-1003", doc3);
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    AclAnthology.Document aclDoc = (AclAnthology.Document) doc;
    assertTrue(aclDoc.indexable());

    for (Map.Entry<String, String> entry : expected.entrySet()) {
      String expectedKey = entry.getKey();
      String expectedValue = entry.getValue();
      if (expectedKey.equals("id")) {
        assertEquals(expectedValue, aclDoc.id());
      } else if (expectedKey.equals("contents")) {
        assertEquals(expectedValue, aclDoc.contents());
        assertEquals(expectedValue, doc.raw());
      } else if (expectedKey.equals("authors")) {
        assertEquals(expectedValue, String.join(" ", aclDoc.authors()));
      } else if (expectedKey.equals("sigs")) {
        assertEquals(expectedValue, String.join(" ", aclDoc.sigs()));
      } else if (expectedKey.equals("venues")) {
        assertEquals(expectedValue, String.join(" ", aclDoc.venues()));
      } else {
        assertEquals(expectedValue, aclDoc.paper().get(expectedKey).asText());
      }
    }
  }
}
