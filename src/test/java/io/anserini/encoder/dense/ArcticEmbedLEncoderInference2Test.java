/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.encoder.dense;

import ai.onnxruntime.OrtException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertArrayEquals;

public class ArcticEmbedLEncoderInference2Test {
  // We're running into this issue on GitHub Java CI:
  // > Error: The operation was canceled.
  // Can't reproduce locally, but separating test cases into separate files seems to fix it...
  @Test
  public void testMaxLength() throws OrtException, IOException, URISyntaxException {
    try(ArcticEmbedLEncoder encoder = new ArcticEmbedLEncoder()) {
      float[] expectedWeights = (float[]) ArcticEmbedLEncoderInferenceTest.LONG_EXAMPLES[0][1];
      String[] inputStrings = (String[]) ArcticEmbedLEncoderInferenceTest.LONG_EXAMPLES[0][0];

      float[] outputs = encoder.encode(inputStrings[0]);
      assertArrayEquals(expectedWeights, outputs, 1e-4f);
    }

    // Specify the directory for which you want to check the free space.
    // You can use the current directory, a specific drive, or a subdirectory.
    File directory = new File("."); // Current directory

    // Get the free space in bytes.
    long freeSpace = directory.getFreeSpace();

    // Get the total space in bytes.
    long totalSpace = directory.getTotalSpace();

    // Get the usable space in bytes (space available to this JVM).
    long usableSpace = directory.getUsableSpace();

    // Convert bytes to gigabytes for easier reading.
    double freeSpaceGB = (double) freeSpace / (1024 * 1024 * 1024);
    double totalSpaceGB = (double) totalSpace / (1024 * 1024 * 1024);
    double usableSpaceGB = (double) usableSpace / (1024 * 1024 * 1024);

    // Print the results.
    System.out.printf("Total space: %.2f GB%n", totalSpaceGB);
    System.out.printf("Usable space: %.2f GB%n", usableSpaceGB);
    System.out.printf("Free space: %.2f GB%n", freeSpaceGB);
  }
}
