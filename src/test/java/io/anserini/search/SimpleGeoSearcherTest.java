package io.anserini.search;

import io.anserini.GeoIndexerTestBase;
import org.apache.lucene.document.LatLonDocValuesField;
import org.apache.lucene.document.LatLonShape;
import org.apache.lucene.document.ShapeField;
import org.apache.lucene.geo.Circle;
import org.apache.lucene.geo.Line;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.junit.Test;

public class SimpleGeoSearcherTest extends GeoIndexerTestBase {
  @Test
  public void testGetLakeOntarioGeoJson() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    Query q = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS, 43, 44, -78, -77);

    SimpleSearcher.Result[] hits = searcher.searchGeo(q, 1);

    assertEquals(1, hits.length);
    assertEquals(0, hits[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetPolygonWithHole() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    Query q1 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS, 12.5, 17.5, 12.5, 17.5);
    Query q2 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS, 2.5, 27.5, 2.5, 27.5);

    SimpleSearcher.Result[] hits1 = searcher.searchGeo(q1, 1);
    SimpleSearcher.Result[] hits2 = searcher.searchGeo(q2, 1);

    assertEquals(0, hits1.length);

    assertEquals(1, hits2.length);
    assertEquals(1, hits2[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetMultiPolygon() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    Query q1 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, -10, 25, 30, 80);
    Query q2 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.CONTAINS, 35, 45, 55, 65);
    Query q3 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, -1, 80, 30, 71);
    Query q4 = LatLonShape.newPointQuery("geometry", ShapeField.QueryRelation.CONTAINS, new double[]{10, 65});

    SimpleSearcher.Result[] hits1 = searcher.searchGeo(q1, 5);
    SimpleSearcher.Result[] hits2 = searcher.searchGeo(q2, 5);
    SimpleSearcher.Result[] hits3 = searcher.searchGeo(q3, 5);
    SimpleSearcher.Result[] hits4 = searcher.searchGeo(q4, 5);

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
    Query q = LatLonShape.newLineQuery("geometry", ShapeField.QueryRelation.INTERSECTS, queryLine);

    SimpleSearcher.Result[] hits = searcher.searchGeo(q, 5);

    assertEquals(1, hits.length);
    assertEquals(3, hits[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetMultiLine() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    Query q1 = LatLonShape.newPointQuery("geometry", ShapeField.QueryRelation.CONTAINS, new double[]{50, 75});
    Query q2 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, 0, 80, 74, 76);
    Query q3 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, 0, 80, 74, 81);

    SimpleSearcher.Result[] hits1 = searcher.searchGeo(q1, 5);
    SimpleSearcher.Result[] hits2 = searcher.searchGeo(q2, 5);
    SimpleSearcher.Result[] hits3 = searcher.searchGeo(q3, 5);

    assertEquals(0, hits1.length);

    assertEquals(0, hits2.length);

    assertEquals(1, hits3.length);
    assertEquals(4, hits3[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetGrandRiver() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    Query q = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, 43.46, 43.56, -80.52, -80.45);

    SimpleSearcher.Result[] hits = searcher.searchGeo(q, 5);

    assertEquals(1, hits.length);
    assertEquals(5, hits[0].lucene_docid);

    searcher.close();
  }

  @Test
  public void testGetLineSorted() throws Exception {
    SimpleGeoSearcher searcher = new SimpleGeoSearcher(super.tempDir1.toString());

    Sort sort = new Sort(LatLonDocValuesField.newDistanceSort("point", -35, 0));
    Query q = LatLonShape.newDistanceQuery("geometry", ShapeField.QueryRelation.WITHIN, new Circle(-35, 0, 900000000));

    SimpleSearcher.Result[] hits = searcher.searchGeo(q, 2, sort);

    // Make sure that we get the second line in line_sorted.wkt first (since it has closer endpoint)
    assertEquals(2, hits.length);
    assertEquals(7, hits[0].lucene_docid);
    assertEquals(6, hits[1].lucene_docid);
  }
}
