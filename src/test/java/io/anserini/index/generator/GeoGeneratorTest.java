package io.anserini.index.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.anserini.collection.JsonCollection;
import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.LatLonDocValuesField;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.ShapeField;
import org.apache.lucene.index.IndexableField;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeoGeneratorTest {
  private JsonCollection.Document geoDoc;
  private Document doc;

  @Before
  public void riverSetUp() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonObj = mapper.createObjectNode();
    jsonObj.set("HYRIV_ID", TextNode.valueOf("90000003"));
    jsonObj.set("NEXT_DOWN", TextNode.valueOf("0"));
    jsonObj.set("MAIN_RIV", TextNode.valueOf("90000003"));
    jsonObj.set("LENGTH_KM", TextNode.valueOf("3.02"));
    jsonObj.set("DIST_DN_KM", TextNode.valueOf("0.0"));
    jsonObj.set("DIST_UP_KM", TextNode.valueOf("35.3"));
    jsonObj.set("CATCH_SKM", TextNode.valueOf("12.24"));
    jsonObj.set("UPLAND_SKM", TextNode.valueOf("12.2"));
    jsonObj.set("ENDORHEIC", TextNode.valueOf("0"));
    jsonObj.set("DIS_AV_CMS", TextNode.valueOf("0.03"));
    jsonObj.set("ORD_STRA", TextNode.valueOf("1"));
    jsonObj.set("ORD_CLAS", TextNode.valueOf("1"));
    jsonObj.set("ORD_FLOW", TextNode.valueOf("8"));
    jsonObj.set("HYBAS_L12", TextNode.valueOf("9120016580"));
    jsonObj.set("geometry", TextNode.valueOf("LINESTRING (-29.737500000000722 83.54583333333295, -29.731250000000642 83.55208333333294, -29.731250000000642 83.57291666666629)"));
    jsonObj.set("id", TextNode.valueOf("90000003"));

    geoDoc = new JsonCollection.Document(jsonObj);

    GeoGenerator generator = new GeoGenerator(new IndexCollection.Args());
    doc = generator.createDocument(geoDoc);
  }

  @Test
  public void testRiverDocumentFields() {
    // Check if the field types were inferred correctly, id field is omitted since it's a repeat of HYRIV_ID
    assertEquals(LongPoint.class, doc.getField("HYRIV_ID").getClass());
    assertEquals(90000003L, doc.getField("HYRIV_ID").numericValue());

    assertEquals(LongPoint.class, doc.getField("NEXT_DOWN").getClass());
    assertEquals(0L, doc.getField("NEXT_DOWN").numericValue());

    assertEquals(LongPoint.class, doc.getField("MAIN_RIV").getClass());
    assertEquals(90000003L, doc.getField("MAIN_RIV").numericValue());

    assertEquals(DoublePoint.class, doc.getField("LENGTH_KM").getClass());
    assertEquals(3.02, doc.getField("LENGTH_KM").numericValue());

    assertEquals(DoublePoint.class, doc.getField("DIST_DN_KM").getClass());
    assertEquals(0.0, doc.getField("DIST_DN_KM").numericValue());

    assertEquals(DoublePoint.class, doc.getField("DIST_UP_KM").getClass());
    assertEquals(35.3, doc.getField("DIST_UP_KM").numericValue());

    assertEquals(DoublePoint.class, doc.getField("CATCH_SKM").getClass());
    assertEquals(12.24, doc.getField("CATCH_SKM").numericValue());

    assertEquals(DoublePoint.class, doc.getField("UPLAND_SKM").getClass());
    assertEquals(12.2, doc.getField("UPLAND_SKM").numericValue());

    assertEquals(LongPoint.class, doc.getField("ENDORHEIC").getClass());
    assertEquals(0L, doc.getField("ENDORHEIC").numericValue());

    assertEquals(DoublePoint.class, doc.getField("DIS_AV_CMS").getClass());
    assertEquals(0.03, doc.getField("DIS_AV_CMS").numericValue());

    assertEquals(LongPoint.class, doc.getField("ORD_STRA").getClass());
    assertEquals(1L, doc.getField("ORD_STRA").numericValue());

    assertEquals(LongPoint.class, doc.getField("ORD_CLAS").getClass());
    assertEquals(1L, doc.getField("ORD_CLAS").numericValue());

    assertEquals(LongPoint.class, doc.getField("ORD_FLOW").getClass());
    assertEquals(8L, doc.getField("ORD_FLOW").numericValue());

    assertEquals(LongPoint.class, doc.getField("HYBAS_L12").getClass());
    assertEquals(9120016580L, doc.getField("HYBAS_L12").numericValue());

    assertEquals(2, doc.getFields("geometry").length);
    for (IndexableField f: doc.getFields("geometry")) {
      assertEquals(ShapeField.Triangle.class, f.getClass());
    }

    assertEquals(3, doc.getFields("point").length);
    for (IndexableField f: doc.getFields("point")) {
      assertEquals(LatLonDocValuesField.class, f.getClass());
    }

    assertEquals("90000003", doc.getField(Constants.ID).stringValue());
  }
}
