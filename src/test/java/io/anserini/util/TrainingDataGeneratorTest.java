package io.anserini.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Mina Farid
 */
public class TrainingDataGeneratorTest {
  @Test
  public void freebaseUriToFreebaseId() throws Exception {
    String barackObamaFreebaseUri = "http://rdf.freebase.com/ns/m.02mjmr";
    String barackObamaFreebaseID = TrainingDataGenerator.freebaseUriToFreebaseId(barackObamaFreebaseUri);
    String correctId = "/m/02mjmr";

    assertEquals(correctId, barackObamaFreebaseID);
  }

  @Test
  public void extractValueFromTypedLiteralString() throws Exception {
    String l1 = "\"1954-10-28\"^^<http://www.w3.org/2001/XMLSchema#date>";
    String l2 = "\"Hanna Bieluszko\"@en";

    String l1val = TrainingDataGenerator.extractValueFromTypedLiteralString(l1);
    String l2val = TrainingDataGenerator.extractValueFromTypedLiteralString(l2);

    String l1expectedVal = "1954-10-28";
    String l2expectedVal = "Hanna Bieluszko";

    assertEquals(l1expectedVal, l1val);
    assertEquals(l2expectedVal, l2val);
  }
}