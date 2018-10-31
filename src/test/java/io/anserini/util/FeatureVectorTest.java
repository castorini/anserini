package io.anserini.util;

import org.apache.lucene.util.LuceneTestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class FeatureVectorTest extends LuceneTestCase {
  private final FeatureVector createAndAddFeatureWeights() {
    FeatureVector fv = new FeatureVector();
    fv.addFeatureWeight("a", 0.2f);
    fv.addFeatureWeight("a", 0.3f);
    fv.addFeatureWeight("b", 0.3f);
    fv.addFeatureWeight("b", 0.5f);
    fv.addFeatureWeight("c", 0.4f);
    fv.addFeatureWeight("d", 0.1f);
    return fv;
  }
  
  @Test
  public void pruneToSizeTest() {
    FeatureVector fv1 = createAndAddFeatureWeights();
    assertEquals(fv1.pruneToSize(2).getFeatures().size(), 2);
    FeatureVector fv2 = createAndAddFeatureWeights();
    assertEquals(fv2.pruneToSize(1).getFeatures().size(), 1);
    FeatureVector fv3 = createAndAddFeatureWeights();
    assertEquals(fv3.pruneToSize(2).getFeatures(), new HashSet<>(Arrays.asList(new String[]{"a", "b"})));
  }
}
