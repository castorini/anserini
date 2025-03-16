package io.anserini.encoder.sparse;

import ai.onnxruntime.OrtException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BaseSparseEncoderInferenceTest {
  public void testExamples(SparseExampleOutputPair[] examples, SparseEncoder encoder) throws OrtException, IOException, URISyntaxException {
    for (SparseExampleOutputPair pair: examples) {
      Map<String, Integer> outputs = encoder.getEncodedQueryMap(pair.getExample());
      Map<String, Integer> expectedMap = pair.getOutput();

      assertEquals(expectedMap.size(), outputs.size());
      for (Map.Entry<String, Integer> entry : outputs.entrySet()) {
        String key = entry.getKey();
        Integer value = entry.getValue();
        //System.out.println(entry.getKey() + " " + entry.getValue());

        Integer expectedValue = expectedMap.get(key);
        assertEquals(expectedValue, value);
      }
      //System.out.println("----");
    }
  }

}
