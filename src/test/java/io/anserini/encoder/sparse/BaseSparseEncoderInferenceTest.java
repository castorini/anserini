package io.anserini.encoder.sparse;

import ai.onnxruntime.OrtException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
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

    System.out.printf("Deleting %s...\n", encoder.getModelPath());
    Files.delete(encoder.getModelPath());

    Runtime runtime = Runtime.getRuntime();

    long maxMemory = runtime.maxMemory(); //Maximum amount of memory that the JVM will attempt to use
    long totalMemory = runtime.totalMemory(); // Total memory currently available to the JVM
    long freeMemory = runtime.freeMemory();  // Amount of free memory available in the JVM

    // Calculate used memory
    long usedMemory = totalMemory - freeMemory;

    System.out.println("Max memory: " + maxMemory / (1024 * 1024) + "MB");
    System.out.println("Total memory: " + totalMemory / (1024 * 1024) + "MB");
    System.out.println("Free memory: " + freeMemory / (1024 * 1024) + "MB");
    System.out.println("Used memory: " + usedMemory / (1024 * 1024) + "MB");
  }

}
