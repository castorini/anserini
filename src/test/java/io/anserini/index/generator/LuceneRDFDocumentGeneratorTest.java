package io.anserini.index.generator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by mfathy on 2017-05-05.
 */
public class LuceneRDFDocumentGeneratorTest {
  @Test
  public void cleanUri() throws Exception {
    String barackObamaFreebaseUri = "<http://rdf.freebase.com/ns/m.02mjmr>";
    String barackObamaCleanUri = LuceneRDFDocumentGenerator.cleanUri(barackObamaFreebaseUri);
    String expectedValue = "http://rdf.freebase.com/ns/m.02mjmr";

    assertEquals(expectedValue, barackObamaCleanUri);
  }

  @Test
  public void getObjectType() throws Exception {
    String o1 = "<http://rdf.freebase.com/ns/m.02mjmr>";
    String o1Type = LuceneRDFDocumentGenerator.getObjectType(o1);
    String o1TypeExpected = LuceneRDFDocumentGenerator.VALUE_TYPE_URI;

    String o2 = "\"1954-10-28\"^^<http://www.w3.org/2001/XMLSchema#date>";
    String o2Type = LuceneRDFDocumentGenerator.getObjectType(o2);
    String o2TypeExpected = LuceneRDFDocumentGenerator.VALUE_TYPE_TEXT;

    String o3 = "\"Hanna Bieluszko\"@en";
    String o3Type = LuceneRDFDocumentGenerator.getObjectType(o3);
    String o3TypeExpected = LuceneRDFDocumentGenerator.VALUE_TYPE_TEXT;

    String o4 = "\"Hanna Bieluszko\"";
    String o4Type = LuceneRDFDocumentGenerator.getObjectType(o4);
    String o4TypeExpected = LuceneRDFDocumentGenerator.VALUE_TYPE_STRING;

    assertEquals(o1Type, o1TypeExpected);
    assertEquals(o2Type, o2TypeExpected);
    assertEquals(o3Type, o3TypeExpected);
    assertEquals(o4Type, o4TypeExpected);
  }

  @Test
  public void normalizeStringValue() throws Exception {
  }

  @Test
  public void normalizeTextValue() throws Exception {
  }

  @Test
  public void normalizeObjectValue() throws Exception {
  }

}