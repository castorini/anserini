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

package io.anserini;

import io.anserini.index.Constants;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LatLonDocValuesField;
import org.apache.lucene.document.LatLonShape;
import org.apache.lucene.document.StringField;
import org.apache.lucene.geo.Line;
import org.apache.lucene.geo.Polygon;
import org.apache.lucene.geo.SimpleWKTShapeParser;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

public class GeoIndexerTestBase extends LuceneTestCase {
  protected Path tempDir1;

  private void buildTestIndex() throws IOException {
    try {
      Directory dir = FSDirectory.open(tempDir1);

      IndexWriterConfig config = new IndexWriterConfig();
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

      IndexWriter writer = new IndexWriter(dir, config);

      // Index Lake Ontario (a Polygon) using Polygon.fromGeoJSON
      Document doc1 = new Document();
      Path path1 = Paths.get("src/test/resources/sample_docs/geosearch/lake_ontario.geojson");
      byte[] encoded = Files.readAllBytes(path1);
      String s = new String(encoded);
      Polygon lakeOntario = Polygon.fromGeoJSON(s)[0];

      Field[] fields = LatLonShape.createIndexableFields("geometry", lakeOntario);
      for (Field f: fields) {
        doc1.add(f);
      }
      doc1.add(new StringField(Constants.ID, "id", Field.Store.YES));
      writer.addDocument(doc1);

      // Index Polygon with a hole in it using SimpleWKTShapeParser
      Document doc2 = new Document();
      Path path2 = Paths.get("src/test/resources/sample_docs/geosearch/self_containing_polygon.wkt");
      List<String> listLines2 = Files.readAllLines(path2);
      String[] lines2 = listLines2.toArray(new String[0]);
      Polygon polygonWithHole = (Polygon) SimpleWKTShapeParser.parse(lines2[0]);

      Field[] fields2 = LatLonShape.createIndexableFields("geometry", polygonWithHole);
      for (Field f: fields2) {
        doc2.add(f);
      }
      doc2.add(new StringField(Constants.ID, "id", Field.Store.YES));
      writer.addDocument(doc2);

      // Index MultiPolygon using SimpleWKTShapeParser
      Document doc3 = new Document();
      Path path3 = Paths.get("src/test/resources/sample_docs/geosearch/multipolygon.wkt");
      List<String> listLines3 = Files.readAllLines(path3);
      String[] lines3 = listLines3.toArray(new String[0]);
      Polygon[] multipolygon = (Polygon[]) SimpleWKTShapeParser.parse(lines3[0]);

      for (Polygon p: multipolygon) {
        Field[] fields3 = LatLonShape.createIndexableFields("geometry", p);
        for (Field f: fields3) {
          doc3.add(f);
        }
      }
      doc3.add(new StringField(Constants.ID, "id", Field.Store.YES));
      writer.addDocument(doc3);

      // Index LineString using SimpleWKTShapeParser
      Document doc4 = new Document();
      Path path4 = Paths.get("src/test/resources/sample_docs/geosearch/line.wkt");
      List<String> listLines4 = Files.readAllLines(path4);
      String[] lines4 = listLines4.toArray(new String[0]);
      Line lineShape = (Line) SimpleWKTShapeParser.parse(lines4[0]);

      Field[] fields4 = LatLonShape.createIndexableFields("geometry", lineShape);
      for (Field f: fields4) {
        doc4.add(f);
      }
      doc4.add(new StringField(Constants.ID, "id", Field.Store.YES));
      writer.addDocument(doc4);

      // Index MultiLineString using SimpleWKTShapeParser
      Document doc5 = new Document();
      Path path5 = Paths.get("src/test/resources/sample_docs/geosearch/multiline.wkt");
      List<String> listLines5 = Files.readAllLines(path5);
      String[] lines5 = listLines5.toArray(new String[0]);
      Line[] lineShapes = (Line[]) SimpleWKTShapeParser.parse(lines5[0]);

      for (Line l: lineShapes) {
        Field[] fields5 = LatLonShape.createIndexableFields("geometry", l);
        for (Field f : fields5) {
          doc5.add(f);
        }
      }
      doc5.add(new StringField(Constants.ID, "id", Field.Store.YES));
      writer.addDocument(doc5);

      // Index Grand River (a LineString) using SimpleWKTShapeParser
      Document doc6 = new Document();
      Path path6 = Paths.get("src/test/resources/sample_docs/geosearch/grand_river.wkt");
      List<String> listLines6 = Files.readAllLines(path6);
      String[] lines6 = listLines6.toArray(new String[0]);
      Line grand_river = (Line) SimpleWKTShapeParser.parse(lines6[0]);

      Field[] fields6 = LatLonShape.createIndexableFields("geometry", grand_river);
      for (Field f: fields6) {
        doc6.add(f);
      }
      doc6.add(new StringField(Constants.ID, "id", Field.Store.YES));
      writer.addDocument(doc6);

      // Index LineStrings for testing the sorted search feature using SimpleWKTShapeParser
      Path path7 = Paths.get("src/test/resources/sample_docs/geosearch/line_sorted.wkt");
      List<String> listLines7 = Files.readAllLines(path7);
      String[] lines7 = listLines7.toArray(new String[0]);

      // first line in line_sorted.wkt
      Document doc7 = new Document();
      Line lineShapeSorted1 = (Line) SimpleWKTShapeParser.parse(lines7[0]);
      Field[] fields7 = LatLonShape.createIndexableFields("geometry", lineShapeSorted1);
      for (Field f: fields7) {
        doc7.add(f);
      }
      for (int i = 0; i < lineShapeSorted1.numPoints(); ++i) {
        doc7.add(new LatLonDocValuesField("point", lineShapeSorted1.getLat(i), lineShapeSorted1.getLon(i)));
      }
      doc7.add(new StringField(Constants.ID, "id", Field.Store.YES));
      writer.addDocument(doc7);

      // second line in line_sorted.wkt
      Document doc8 = new Document();
      Line lineShapeSorted2 = (Line) SimpleWKTShapeParser.parse(lines7[1]);
      Field[] fields8 = LatLonShape.createIndexableFields("geometry", lineShapeSorted2);
      for (Field f: fields8) {
        doc8.add(f);
      }
      for (int i = 0; i < lineShapeSorted2.numPoints(); ++i) {
        doc8.add(new LatLonDocValuesField("point", lineShapeSorted2.getLat(i), lineShapeSorted2.getLon(i)));
      }
      doc8.add(new StringField(Constants.ID, "id", Field.Store.YES));
      writer.addDocument(doc8);


      writer.commit();
      writer.forceMerge(1);
      writer.close();

      dir.close();

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    tempDir1 = createTempDir();
    buildTestIndex();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    // Call garbage collector for Windows compatibility
    System.gc();
    super.tearDown();
  }
}
