/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.kg;

import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FreebaseNodeTest {
  /**
   * Simple value factory to parse literals using Sesame library.
   */
  private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

  @Test
  public void cleanUri() {
    assertEquals("fb:m.02mjmr",
        FreebaseNode.cleanUri("<http://rdf.freebase.com/ns/m.02mjmr>"));
  }

  @Test
  public void normalizeObject() {
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

    assertEquals("fb:m.0x2spfl", FreebaseNode.normalizeObjectValue("<http://rdf.freebase.com/ns/m.0x2spfl>"));
  }

  @Test
  public void getObjectType() {
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
  public void freebaseUriToFreebaseId() {
    String barackObamaFreebaseUri = "http://rdf.freebase.com/ns/m.02mjmr";
    String barackObamaFreebaseID = FreebaseNode.freebaseUriToFreebaseId(barackObamaFreebaseUri);
    String correctId = "/m/02mjmr";

    assertEquals(correctId, barackObamaFreebaseID);
  }

  @Test
  public void extractValueFromTypedLiteralString() {
    String l1 = "\"1954-10-28\"^^<http://www.w3.org/2001/XMLSchema#date>";
    String l2 = "\"Hanna Bieluszko\"@en";

    String l1val = FreebaseNode.extractValueFromTypedLiteralString(l1);
    String l2val = FreebaseNode.extractValueFromTypedLiteralString(l2);

    String l1expectedVal = "1954-10-28";
    String l2expectedVal = "Hanna Bieluszko";

    assertEquals(l1expectedVal, l1val);
    assertEquals(l2expectedVal, l2val);

    // Example showing how to use the API
    ValueFactory valueFactory = SimpleValueFactory.getInstance();
    Literal parsedLiteral = NTriplesUtil.parseLiteral(l2, valueFactory);
    assertTrue(parsedLiteral.getLanguage().toString().equals("Optional[en]"));
  }
}