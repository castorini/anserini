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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LatLonShape;
import org.apache.lucene.geo.Polygon;
import org.apache.lucene.geo.SimpleWKTShapeParser;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GeoIndexerTestBase extends LuceneTestCase {
    protected Path tempDir1;

    @Test
    public void buildTestIndex() throws IOException {
        try {
            Directory dir = FSDirectory.open(tempDir1);

            IndexWriterConfig config = new IndexWriterConfig();
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(dir, config);


            Document doc1 = new Document();
            Path path1 = Paths.get("src/test/resources/sample_docs/geosearch/lake_ontario.geojson");
            byte[] encoded = Files.readAllBytes(path1);
            String s = new String(encoded);
            Polygon lakeOntario = Polygon.fromGeoJSON(s)[0];

            Field[] fields = LatLonShape.createIndexableFields("geometry", lakeOntario);
            for (Field f: fields) {
                doc1.add(f);
            }
            writer.addDocument(doc1);


            Document doc2 = new Document();
            Path path2 = Paths.get("src/test/resources/sample_docs/geosearch/self_containing_polygon.wkt");
            List<String> listLines2 = Files.readAllLines(path2);
            String[] lines2 = listLines2.toArray(new String[0]);
            Polygon polygonWithHole = (Polygon) SimpleWKTShapeParser.parse(lines2[0]);

            Field[] fields2 = LatLonShape.createIndexableFields("geometry", polygonWithHole);
            for (Field f: fields2) {
                doc2.add(f);
            }
            writer.addDocument(doc2);


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
            writer.addDocument(doc3);






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