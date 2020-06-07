package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class IsoCollectionTest extends DocumentCollectionTest<IsoCollection.Document> {
  @Before
  public void setUp() throws Exception {
    collectionPath = Paths.get("src/test/resources/sample_docs/iso19115");
    collection = new IsoCollection(collectionPath);
    Path segment = Paths.get("src/test/resources/sample_docs/iso19115/output.json");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 1);

    totalSegments = 1;
    totalDocs = 1;

    expected.put("12958", Map.of("id", "12958", "title", "Phosphorus Uptake of Crops in Ontario Counties from 1961-2016", "abstract", "Annual crop uptake of phosphorus in kilograms for each census year on a census division scale, taken every five years beginning in 1961. Data were used in the Net Anthropogenic Phosphorus Inputs (NAPI) model. SGC or ID is the unique Standard Geographic Code that represents the county, Name is the name of the census division, and the region is the agricultural region the county is in: South (1), West (2), Central (3), West (4), North (5). The raw data were downloaded from the publicly available census available at: http://odesi2.scholarsportal.info/webview/ in the Agriculture tab. All data were standardized to the census division distribution in 2011, i.e. any divisions or subdivisions that were amalgamated into other divisions were accounted for."));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    assertTrue(doc.indexable());
    assertEquals(expected.get("id"), doc.id());
    assertEquals(expected.get("title"), ((IsoCollection.Document) doc).getTitle());
    assertEquals(expected.get("abstract"), ((IsoCollection.Document) doc).getAbstract());
  }
}