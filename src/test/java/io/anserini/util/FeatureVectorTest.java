package io.anserini.util;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class FeatureVectorTest extends LuceneTestCase {
  @Test
  public void pruneToSizeTest() {
    FeatureVector fv = new FeatureVector();
    fv.addFeatureWeight("a", 0.2f);
    fv.addFeatureWeight("a", 0.3f);
    fv.addFeatureWeight("b", 0.3f);
    fv.addFeatureWeight("b", 0.5f);
    fv.addFeatureWeight("c", 0.4f);
  
    assertEquals(fv.pruneToSize(2).getFeatures().size(), 2);
    assertEquals(fv.pruneToSize(1).getFeatures().size(), 1);
    assertEquals(fv.pruneToSize(2).getFeatures(), new HashSet<>(Arrays.asList(new String[]{"a", "b"})));
  }
}
