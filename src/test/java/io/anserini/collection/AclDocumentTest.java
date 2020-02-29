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

package io.anserini.collection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

public class AclDocumentTest extends DocumentCollectionTest<AclCollection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/acl/");
    collection = new AclCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/acl/papers/segment1.yaml");
    Path segment2 = Paths.get("src/test/resources/sample_docs/acl/papers/segment2.yaml");

    segmentPaths.add(segment1);
    segmentPaths.add(segment2);

    segmentDocCounts.put(segment1, 1);
    segmentDocCounts.put(segment2, 1);

    totalSegments = 2;
    totalDocs = 2;

    HashMap<String, String> docC00 = new HashMap<>();
    docC00.put("id", "C00-1003");
    docC00.put("authors", "Ion Androutsopoulos Robert Dale");
    docC00.put("author_string", "Ion Androutsopoulos, Robert Dale");
    docC00.put("bibkey", "androutsopoulos-dale-2000-selectional");
    docC00.put("bibtype", "inproceedings");
    docC00.put("booktitle", "COLING 2000 Volume 1: The 18th International Conference on Computational Linguistics");
    docC00.put("paper_id", "3");
    docC00.put("parent_volume_id", "C00-1");
    docC00.put("pdf", "https://www.aclweb.org/anthology/C00-1003.pdf");
    docC00.put("thumbnail", "https://www.aclweb.org/anthology/thumb/C00-1003.jpg");
    docC00.put("title", "Selectional Restrictions in HPSG");
    docC00.put("url", "https://www.aclweb.org/anthology/C00-1003");
    docC00.put("contents", "Selectional Restrictions in HPSG ");
    docC00.put("sigs", "");
    docC00.put("venues", "COLING");
    expected.put("C00-1003", docC00);

    HashMap<String, String> docE17 = new HashMap<>();
    docE17.put("id", "E17-1003");
    docE17.put("address", "Valencia, Spain");
    docE17.put("authors", "Heike Adel Hinrich Schütze");
    docE17.put("author_string", "Heike Adel, Hinrich Schütze");
    docE17.put("bibkey", "adel-schutze-2017-exploring");
    docE17.put("bibtype", "inproceedings");
    docE17.put("booktitle", "Proceedings of the 15th Conference of the European Chapter of the Association " +
      "for Computational Linguistics: Volume 1, Long Papers");
    docE17.put("month", "April");
    docE17.put("year", "2017");
    docE17.put("page_first", "22");
    docE17.put("page_last", "34");
    docE17.put("paper_id", "3");
    docE17.put("parent_volume_id", "E17-1");
    docE17.put("pdf", "https://www.aclweb.org/anthology/E17-1003.pdf");
    docE17.put("publisher", "Association for Computational Linguistics");
    docE17.put("thumbnail", "https://www.aclweb.org/anthology/thumb/E17-1003.jpg");
    docE17.put("title", "Exploring Different Dimensions of Attention for Uncertainty Detection");
    docE17.put("url", "https://www.aclweb.org/anthology/E17-1003");
    docE17.put("contents", "Exploring Different Dimensions of Attention for Uncertainty Detection " +
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
    docE17.put("sigs", "");
    docE17.put("venues", "EACL");
    expected.put("E17-1003", docE17);
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    AclCollection.Document aclDoc = (AclCollection.Document) doc;
    assertTrue(aclDoc.indexable());

    for (Map.Entry<String, String> entry : expected.entrySet()) {
      String expectedKey = entry.getKey();
      String expectedValue = entry.getValue();
      if (expectedKey.equals("id")) {
        assertEquals(expectedValue, aclDoc.id());
      } else if (expectedKey.equals("contents")) {
        assertEquals(expectedValue, aclDoc.content());
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
