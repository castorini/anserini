package io.anserini.kg.freebase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FreebaseNodeTest {
  @Test
  public void cleanUri() throws Exception {
    assertEquals("http://rdf.freebase.com/ns/m.02mjmr",
        FreebaseNode.cleanUri("<http://rdf.freebase.com/ns/m.02mjmr>"));
  }

  @Test
  public void getObjectType() throws Exception {
    assertEquals(FreebaseNode.RdfObjectType.URI,
        FreebaseNode.getObjectType("<http://rdf.freebase.com/ns/m.02mjmr>"));

    assertEquals(FreebaseNode.RdfObjectType.TEXT,
        FreebaseNode.getObjectType("\"1954-10-28\"^^<http://www.w3.org/2001/XMLSchema#date>"));

    assertEquals(FreebaseNode.RdfObjectType.TEXT,
        FreebaseNode.getObjectType("\"Hanna Bieluszko\"@en"));

    assertEquals(FreebaseNode.RdfObjectType.STRING,
        FreebaseNode.getObjectType("\"Hanna Bieluszko\""));
  }
}