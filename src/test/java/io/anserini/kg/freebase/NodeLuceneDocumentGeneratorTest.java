package io.anserini.kg.freebase;

import io.anserini.kg.freebase.IndexNodes.NodeLuceneDocumentGenerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mfathy on 2017-05-05.
 */
public class NodeLuceneDocumentGeneratorTest {
  @Test
  public void cleanUri() throws Exception {
    String barackObamaFreebaseUri = "<http://rdf.freebase.com/ns/m.02mjmr>";
    String barackObamaCleanUri = NodeLuceneDocumentGenerator.cleanUri(barackObamaFreebaseUri);
    String expectedValue = "http://rdf.freebase.com/ns/m.02mjmr";

    assertEquals(expectedValue, barackObamaCleanUri);
  }

  @Test
  public void getObjectType() throws Exception {
    String o1 = "<http://rdf.freebase.com/ns/m.02mjmr>";
    String o1Type = NodeLuceneDocumentGenerator.getObjectType(o1);
    String o1TypeExpected = NodeLuceneDocumentGenerator.VALUE_TYPE_URI;

    String o2 = "\"1954-10-28\"^^<http://www.w3.org/2001/XMLSchema#date>";
    String o2Type = NodeLuceneDocumentGenerator.getObjectType(o2);
    String o2TypeExpected = NodeLuceneDocumentGenerator.VALUE_TYPE_TEXT;

    String o3 = "\"Hanna Bieluszko\"@en";
    String o3Type = NodeLuceneDocumentGenerator.getObjectType(o3);
    String o3TypeExpected = NodeLuceneDocumentGenerator.VALUE_TYPE_TEXT;

    String o4 = "\"Hanna Bieluszko\"";
    String o4Type = NodeLuceneDocumentGenerator.getObjectType(o4);
    String o4TypeExpected = NodeLuceneDocumentGenerator.VALUE_TYPE_STRING;

    assertEquals(o1Type, o1TypeExpected);
    assertEquals(o2Type, o2TypeExpected);
    assertEquals(o3Type, o3TypeExpected);
    assertEquals(o4Type, o4TypeExpected);
  }
}