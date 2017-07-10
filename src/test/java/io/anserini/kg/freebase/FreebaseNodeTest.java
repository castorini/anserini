package io.anserini.kg.freebase;

import org.junit.Test;
import org.openrdf.rio.ntriples.NTriplesUtil;

import static org.junit.Assert.assertEquals;

public class FreebaseNodeTest {
  @Test
  public void cleanUri() throws Exception {
    assertEquals("fb:m.02mjmr",
        FreebaseNode.cleanUri("<http://rdf.freebase.com/ns/m.02mjmr>"));
  }

  @Test
  public void normalizeObject() throws Exception {
    // FYI - to demonstrate behavior of NTriplesUtil.unescapeString
    assertEquals("\"Hanna Bieluszko\"@en",
        NTriplesUtil.unescapeString("\"Hanna Bieluszko\"@en"));

    // See: https://dvcs.w3.org/hg/rdf/raw-file/default/rdf-turtle/n-triples.html
    assertEquals("This is a multi-line\nliteral with many quotes (\"\"\"\"\")\nand two apostrophes ('').",
        NTriplesUtil.unescapeString("This is a multi-line\nliteral with many quotes (\"\"\"\"\")\nand two apostrophes ('')."));

    // Test MQL key escaping (see method for more details):
    assertEquals("Barack_Hussein_Obama,_Jr.",
        FreebaseNode.normalizeObjectValue("\"Barack_Hussein_Obama$002C_Jr$002E\""));
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