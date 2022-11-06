package io.anserini.collection;

import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JsonCollectionGeoRiverTest extends JsonCollectionTest {
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/json/collection_geo");
    collection = new JsonCollection(collectionPath);

    Path segment = Paths.get("src/test/resources/sample_docs/json/collection_geo/rivers.json");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 3);

    totalSegments = 1;
    totalDocs = 3;

    expected.put("90000001", Map.ofEntries(
            Map.entry("HYRIV_ID", "90000001"),
            Map.entry("NEXT_DOWN", "0"),
            Map.entry("MAIN_RIV", "90000001"),
            Map.entry("LENGTH_KM", "1.16"),
            Map.entry("DIST_DN_KM", "0.0"),
            Map.entry("DIST_UP_KM", "8.4"),
            Map.entry("CATCH_SKM", "15.06"),
            Map.entry("UPLAND_SKM", "15.0"),
            Map.entry("ENDORHEIC", "0"),
            Map.entry("DIS_AV_CMS", "0.089"),
            Map.entry("ORD_STRA", "1"),
            Map.entry("ORD_CLAS", "1"),
            Map.entry("ORD_FLOW", "8"),
            Map.entry("HYBAS_L12", "9120016560"),
            Map.entry("geometry", "LINESTRING (-32.235416666667334 83.57916666666631, -32.235416666667334 83.589583333333)"),
            Map.entry("id", "90000001")
    ));

    expected.put("90000002", Map.ofEntries(
            Map.entry("HYRIV_ID", "90000002"),
            Map.entry("NEXT_DOWN", "0"),
            Map.entry("MAIN_RIV", "90000002"),
            Map.entry("LENGTH_KM", "1.16"),
            Map.entry("DIST_DN_KM", "0.0"),
            Map.entry("DIST_UP_KM", "69.1"),
            Map.entry("CATCH_SKM", "10.08"),
            Map.entry("UPLAND_SKM", "10.1"),
            Map.entry("ENDORHEIC", "0"),
            Map.entry("DIS_AV_CMS", "0.0"),
            Map.entry("ORD_STRA", "1"),
            Map.entry("ORD_CLAS", "1"),
            Map.entry("ORD_FLOW", "10"),
            Map.entry("HYBAS_L12", "9120016500"),
            Map.entry("geometry", "LINESTRING (-36.03541666666723 83.54583333333295, -36.03541666666723 83.55624999999961)"),
            Map.entry("id", "90000002")
    ));

    expected.put("90000003", Map.ofEntries(
            Map.entry("HYRIV_ID", "90000003"),
            Map.entry("NEXT_DOWN", "0"),
            Map.entry("MAIN_RIV", "90000003"),
            Map.entry("LENGTH_KM", "3.02"),
            Map.entry("DIST_DN_KM", "0.0"),
            Map.entry("DIST_UP_KM", "35.3"),
            Map.entry("CATCH_SKM", "12.24"),
            Map.entry("UPLAND_SKM", "12.2"),
            Map.entry("ENDORHEIC", "0"),
            Map.entry("DIS_AV_CMS", "0.03"),
            Map.entry("ORD_STRA", "1"),
            Map.entry("ORD_CLAS", "1"),
            Map.entry("ORD_FLOW", "8"),
            Map.entry("HYBAS_L12", "9120016580"),
            Map.entry("geometry", "LINESTRING (-29.737500000000722 83.54583333333295, -29.731250000000642 83.55208333333294, -29.731250000000642 83.57291666666629)"),
            Map.entry("id", "90000003")
    ));
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    // Note that we need an id in addition to HYRIV_ID to distinguish between different docs
    assertTrue(doc.indexable());
    assertEquals(expected.get("HYRIV_ID"), ((JsonCollection.Document) doc).fields().get("HYRIV_ID"));
    assertEquals(expected.get("NEXT_DOWN"), ((JsonCollection.Document) doc).fields().get("NEXT_DOWN"));
    assertEquals(expected.get("MAIN_RIV"), ((JsonCollection.Document) doc).fields().get("MAIN_RIV"));
    assertEquals(expected.get("LENGTH_KM"), ((JsonCollection.Document) doc).fields().get("LENGTH_KM"));
    assertEquals(expected.get("DIST_DN_KM"), ((JsonCollection.Document) doc).fields().get("DIST_DN_KM"));
    assertEquals(expected.get("DIST_UP_KM"), ((JsonCollection.Document) doc).fields().get("DIST_UP_KM"));
    assertEquals(expected.get("CATCH_SKM"), ((JsonCollection.Document) doc).fields().get("CATCH_SKM"));
    assertEquals(expected.get("UPLAND_SKM"), ((JsonCollection.Document) doc).fields().get("UPLAND_SKM"));
    assertEquals(expected.get("ENDORHEIC"), ((JsonCollection.Document) doc).fields().get("ENDORHEIC"));
    assertEquals(expected.get("DIS_AV_CMS"), ((JsonCollection.Document) doc).fields().get("DIS_AV_CMS"));
    assertEquals(expected.get("ORD_STRA"), ((JsonCollection.Document) doc).fields().get("ORD_STRA"));
    assertEquals(expected.get("ORD_CLAS"), ((JsonCollection.Document) doc).fields().get("ORD_CLAS"));
    assertEquals(expected.get("ORD_FLOW"), ((JsonCollection.Document) doc).fields().get("ORD_FLOW"));
    assertEquals(expected.get("HYBAS_L12"), ((JsonCollection.Document) doc).fields().get("HYBAS_L12"));
    assertEquals(expected.get("geometry"), ((JsonCollection.Document) doc).fields().get("geometry"));
    assertEquals(expected.get("id"), doc.id());
  }

  @Override
  public void checkRawtoContent() {
    // no content field in this collection, skip the test
    assertEquals("", "");
  }
}
