package io.anserini.search;

import io.anserini.GeoIndexerTestBase;
import org.apache.lucene.document.LatLonShape;
import org.apache.lucene.document.ShapeField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class GeoSearcherTest extends GeoIndexerTestBase {

    @Test
    public void testGetLakeOntarioGeoJson() throws Exception {
        Directory directory = FSDirectory.open(tempDir1);
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query q = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS,
                43, 44, -78, -77);
        TopDocs hits = searcher.search(q, 1);
        assertEquals(1, hits.totalHits.value);
        assertEquals(0, hits.scoreDocs[0].doc);
    }

    @Test
    public void testGetPolygonWithHole() throws Exception {
        Directory directory = FSDirectory.open(tempDir1);
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query q1 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS,
                12.5, 17.5, 12.5, 17.5);
        TopDocs hits1 = searcher.search(q1, 1);
        assertEquals(0, hits1.totalHits.value);

        Query q2 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS,
                2.5, 27.5, 2.5, 27.5);
        TopDocs hits2 = searcher.search(q2, 1);
        assertEquals(1, hits2.totalHits.value);
        assertEquals(1, hits2.scoreDocs[0].doc);
    }

    @Test
    public void testGetMultiPolygon() throws Exception {
        Directory directory = FSDirectory.open(tempDir1);
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query q1 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS,
                10, 25, 55, 65);
        TopDocs hits1 = searcher.search(q1, 5);
        assertEquals(1, hits1.totalHits.value);
        assertEquals(2, hits1.scoreDocs[0].doc);

        Query q2 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.WITHIN,
                40, 60, 55, 65);
        TopDocs hits2 = searcher.search(q2, 5);
        assertEquals(1, hits2.totalHits.value);
        assertEquals(2, hits2.scoreDocs[0].doc);

        Query q3 = LatLonShape.newBoxQuery("geometry", ShapeField.QueryRelation.INTERSECTS,
                -10, 80, 30, 80);
        TopDocs hits3 = searcher.search(q3, 5);
        assertEquals(1, hits3.totalHits.value);
        assertEquals(2, hits3.scoreDocs[0].doc);
    }

    @Test
    public void testGetLine() throws Exception {

    }

    @Test
    public void testGetMultiLine() throws Exception {

    }
}
