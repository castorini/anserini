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

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class TwentynewsgroupsCollectionTest extends DocumentCollectionTest<TwentynewsgroupsCollection.Document> {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/twentynews/collection1/");
    collection = new TwentynewsgroupsCollection(collectionPath);

    Path segment1 = Paths.get("src/test/resources/sample_docs/twentynews/collection1/82757");

    segmentPaths.add(segment1);
    segmentDocCounts.put(segment1, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("82757",
        Map.of("Id", "82757",
            "From","dsoconne@quads.uchicago.edu (Daniel S OConnell)",
            "Subject", "Re: Religion and homosexuality",
            "Keywords","being liberal",
            "Organization","University of Chicago",
            "Content","From: dsoconne@quads.uchicago.edu (Daniel S OConnell)\nSubject: Re: Religion and homosexuality\nKeywords: being liberal\nReply-To: dsoconne@midway.uchicago.edu\nOrganization: University of Chicago\nDistribution: usa\nLines: 32\nGood Luck on your journey!"));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    TwentynewsgroupsCollection.Document Twentynewsgroups = (TwentynewsgroupsCollection.Document) doc;
    assertTrue(doc.indexable());
    assertEquals(expected.get("Id"), doc.id());
    assertEquals(expected.get("From"), Twentynewsgroups.From());
    assertEquals(expected.get("Subject"), Twentynewsgroups.Subject());
    assertEquals(expected.get("Keywords"), Twentynewsgroups.Keywords());
    assertEquals(expected.get("Organization"), Twentynewsgroups.Organization());
    assertEquals(expected.get("Content"), doc.contents());
    assertEquals(expected.get("Content"), doc.raw());
  }
}
