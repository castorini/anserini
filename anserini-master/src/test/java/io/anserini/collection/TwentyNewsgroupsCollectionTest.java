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
import java.util.Map;

public class TwentyNewsgroupsCollectionTest extends DocumentCollectionTest<TwentyNewsgroupsCollection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/20Newsgroups/collection1/");
    collection = new TwentyNewsgroupsCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/20Newsgroups/collection1/82757");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("82757",
        Map.of("id", "82757",
            "from","dsoconne@quads.uchicago.edu (Daniel S OConnell)",
            "subject", "Re: Religion and homosexuality",
            "keywords","being liberal",
            "organization","University of Chicago",
            "contents","From: dsoconne@quads.uchicago.edu (Daniel S OConnell)\nSubject: Re: Religion and homosexuality\nKeywords: being liberal\nReply-To: dsoconne@midway.uchicago.edu\nOrganization: University of Chicago\nDistribution: usa\nLines: 32\nGood Luck on your journey!"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    TwentyNewsgroupsCollection.Document Twentynewsgroups = (TwentyNewsgroupsCollection.Document) doc;
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("from"), Twentynewsgroups.from());
    assertEquals(expected.get("subject"), Twentynewsgroups.subject());
    assertEquals(expected.get("keywords"), Twentynewsgroups.keywords());
    assertEquals(expected.get("organization"), Twentynewsgroups.organization());
    assertEquals(expected.get("contents"), doc.contents());
    assertEquals(expected.get("contents"), doc.raw());
  }
}
