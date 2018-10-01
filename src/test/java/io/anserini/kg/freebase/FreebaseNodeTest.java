package io.anserini.kg.freebase;

import org.junit.Test;
import org.openrdf.rio.ntriples.NTriplesUtil;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;

import static org.junit.Assert.assertEquals;

public class FreebaseNodeTest {
  /**
   * Simple value factory to parse literals using Sesame library.
   */
  private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

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

    Literal parsedLiteral = NTriplesUtil.parseLiteral("\"Hanna Bieluszko\"@en", valueFactory);
    assertEquals(parsedLiteral.getLanguage().toString(), "Optional[en]");
    assertEquals(parsedLiteral.stringValue(), "Hanna Bieluszko");

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

  @Test
  public void freebaseUriToFreebaseId() throws Exception {
    String barackObamaFreebaseUri = "http://rdf.freebase.com/ns/m.02mjmr";
    String barackObamaFreebaseID = FreebaseNode.freebaseUriToFreebaseId(barackObamaFreebaseUri);
    String correctId = "/m/02mjmr";

    assertEquals(correctId, barackObamaFreebaseID);
  }

  @Test
  public void extractValueFromTypedLiteralString() throws Exception {
    String l1 = "\"1954-10-28\"^^<http://www.w3.org/2001/XMLSchema#date>";
    String l2 = "\"Hanna Bieluszko\"@en";

    String l1val = FreebaseNode.extractValueFromTypedLiteralString(l1);
    String l2val = FreebaseNode.extractValueFromTypedLiteralString(l2);

    String l1expectedVal = "1954-10-28";
    String l2expectedVal = "Hanna Bieluszko";

    assertEquals(l1expectedVal, l1val);
    assertEquals(l2expectedVal, l2val);
  }
}