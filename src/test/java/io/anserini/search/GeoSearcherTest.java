package io.anserini.search;

import io.anserini.GeoIndexerTestBase;
import org.apache.lucene.document.ShapeField;
import org.apache.lucene.geo.Line;
import org.apache.lucene.geo.Point;
import org.apache.lucene.geo.Rectangle;
import org.junit.Test;

public class GeoSearcherTest extends GeoIndexerTestBase {
  @Test
  public void testGetLakeOntarioGeoJson() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits = searcher.searchGeo(1, ShapeField.QueryRelation.INTERSECTS, new Rectangle(43, 44, -78, -77));

    assertEquals(1, hits.length);
    assertEquals(0, hits[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetPolygonWithHole() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits1 = searcher.searchGeo(1, ShapeField.QueryRelation.INTERSECTS, new Rectangle(12.5, 17.5, 12.5, 17.5));
    SimpleSearcher.Result[] hits2 = searcher.searchGeo(1, ShapeField.QueryRelation.INTERSECTS, new Rectangle(2.5, 27.5, 2.5, 27.5));

    assertEquals(0, hits1.length);

    assertEquals(1, hits2.length);
    assertEquals(1, hits2[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetMultiPolygon() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits1 = searcher.searchGeo(5, ShapeField.QueryRelation.WITHIN, new Rectangle(-10, 25, 30, 80));
    SimpleSearcher.Result[] hits2 = searcher.searchGeo(5, ShapeField.QueryRelation.CONTAINS, new Rectangle(35, 45, 55, 65));
    SimpleSearcher.Result[] hits3 = searcher.searchGeo(5, ShapeField.QueryRelation.WITHIN, new Rectangle(-1, 80, 30, 71));
    SimpleSearcher.Result[] hits4 = searcher.searchGeo(5, ShapeField.QueryRelation.CONTAINS, new Point(10, 65));

    assertEquals(0, hits1.length);

    assertEquals(1, hits2.length);
    assertEquals(2, hits2[0].lucene_docid);

    assertEquals(1, hits3.length);
    assertEquals(2, hits3[0].lucene_docid);

    assertEquals(1, hits4.length);
    assertEquals(2, hits4[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetLine() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    Line queryLine = new Line(new double[]{30, 50}, new double[]{10, 10});
    SimpleSearcher.Result[] hits = searcher.searchGeo(5, ShapeField.QueryRelation.INTERSECTS, queryLine);
    assertEquals(1, hits.length);
    assertEquals(3, hits[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetMultiLine() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits1 = searcher.searchGeo(5, ShapeField.QueryRelation.CONTAINS, new Point(50, 75));
    SimpleSearcher.Result[] hits2 = searcher.searchGeo(5, ShapeField.QueryRelation.WITHIN, new Rectangle(0, 80, 74, 76));
    SimpleSearcher.Result[] hits3 = searcher.searchGeo(5, ShapeField.QueryRelation.WITHIN, new Rectangle(0, 80, 74, 81));

    assertEquals(0, hits1.length);

    assertEquals(0, hits2.length);

    assertEquals(1, hits3.length);
    assertEquals(4, hits3[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetGrandRiver() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    SimpleSearcher.Result[] hits = searcher.searchGeo(5, ShapeField.QueryRelation.WITHIN, new Rectangle(43.46, 43.56, -80.52, -80.45));

    assertEquals(1, hits.length);
    assertEquals(5, hits[0].lucene_docid);

    searcher.close();
  }
}
