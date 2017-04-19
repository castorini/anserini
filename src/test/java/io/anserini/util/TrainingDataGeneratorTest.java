package io.anserini.util;

import org.junit.Test;

import static org.junit.Assert.*;

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

}