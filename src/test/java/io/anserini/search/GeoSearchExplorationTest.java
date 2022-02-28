package io.anserini.search;

import io.anserini.GeoIndexerTestBase;
import org.apache.lucene.document.LatLonShape;
import org.apache.lucene.document.ShapeField;
import org.apache.lucene.geo.Line;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

/**
 * Initial exploration test on the Lucene Geospatial search API
 */
public class GeoSearchExplorationTest extends GeoIndexerTestBase {

  @Test
  public void testGetLakeOntarioGeoJson() throws Exception {
    Directory directory = FSDirectory.open(tempDir1);
    DirectoryReader reader = DirectoryReader.open(directory);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS, 43, 44, -78, -77);
    TopDocs hits = searcher.search(q, 1);
    assertEquals(1, hits.totalHits.value);
    assertEquals(0, hits.scoreDocs[0].doc);

    reader.close();
    directory.close();
  }

  @Test
  public void testGetPolygonWithHole() throws Exception {
    Directory directory = FSDirectory.open(tempDir1);
    DirectoryReader reader = DirectoryReader.open(directory);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q1 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS, 12.5, 17.5, 12.5, 17.5);
    TopDocs hits1 = searcher.search(q1, 1);
    assertEquals(0, hits1.totalHits.value);

    Query q2 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS, 2.5, 27.5, 2.5, 27.5);
    TopDocs hits2 = searcher.search(q2, 1);
    assertEquals(1, hits2.totalHits.value);
    assertEquals(1, hits2.scoreDocs[0].doc);

    reader.close();
    directory.close();
  }

  @Test
  public void testGetMultiPolygon() throws Exception {
    Directory directory = FSDirectory.open(tempDir1);
    DirectoryReader reader = DirectoryReader.open(directory);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q1 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, -10, 25, 30, 80);
    TopDocs hits1 = searcher.search(q1, 5);
    assertEquals(0, hits1.totalHits.value);

    Query q2 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.CONTAINS, 35, 45, 55, 65);
    TopDocs hits2 = searcher.search(q2, 5);
    assertEquals(1, hits2.totalHits.value);
    assertEquals(2, hits2.scoreDocs[0].doc);

    Query q3 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, -1, 80, 30, 71);
    TopDocs hits3 = searcher.search(q3, 5);
    assertEquals(1, hits3.totalHits.value);
    assertEquals(2, hits3.scoreDocs[0].doc);

    double[] queryPoint = new double[]{10, 65};
    Query q4 = LatLonShape.newPointQuery("geometry", ShapeField.QueryRelation.CONTAINS, queryPoint);
    TopDocs hits4 = searcher.search(q4, 5);
    assertEquals(1, hits4.totalHits.value);
    assertEquals(2, hits4.scoreDocs[0].doc);


    reader.close();
    directory.close();
  }

  @Test
  public void testGetLine() throws Exception {
    Directory directory = FSDirectory.open(tempDir1);
    DirectoryReader reader = DirectoryReader.open(directory);
    IndexSearcher searcher = new IndexSearcher(reader);

    Line queryLine = new Line(new double[]{30, 50}, new double[]{10, 10});
    Query q = LatLonShape.newLineQuery("geometry", ShapeField.QueryRelation.INTERSECTS, queryLine);
    TopDocs hits = searcher.search(q, 5);
    assertEquals(1, hits.totalHits.value);
    assertEquals(3, hits.scoreDocs[0].doc);

    reader.close();
    directory.close();
  }

  @Test
  public void testGetMultiLine() throws Exception {
    Directory directory = FSDirectory.open(tempDir1);
    DirectoryReader reader = DirectoryReader.open(directory);
    IndexSearcher searcher = new IndexSearcher(reader);

    double[] queryPoint = new double[]{50, 75};
    Query q1 = LatLonShape.newPointQuery("geometry", ShapeField.QueryRelation.CONTAINS, queryPoint);
    TopDocs hits1 = searcher.search(q1, 5);
    assertEquals(0, hits1.totalHits.value);

    Query q2 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, 0, 80, 74, 76);
    TopDocs hits2 = searcher.search(q2, 5);
    assertEquals(0, hits2.totalHits.value);

    Query q3 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, 0, 80, 74, 81);
    TopDocs hits3 = searcher.search(q3, 5);
    assertEquals(1, hits3.totalHits.value);
    assertEquals(4, hits3.scoreDocs[0].doc);

    reader.close();
    directory.close();
  }

  @Test
  public void testGetGrandRiver() throws Exception {
    Directory directory = FSDirectory.open(tempDir1);
    DirectoryReader reader = DirectoryReader.open(directory);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query q = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN, 43.46, 43.56, -80.52, -80.45);
    TopDocs hits = searcher.search(q, 5);
    assertEquals(1, hits.totalHits.value);
    assertEquals(5, hits.scoreDocs[0].doc);

    reader.close();
    directory.close();
  }
}
