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

package io.anserini.reproduce;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static io.anserini.reproduce.ReproductionUtils.Status.FAIL;
import static io.anserini.reproduce.ReproductionUtils.Status.OK;
import static io.anserini.reproduce.ReproductionUtils.Status.OKISH;
import static io.anserini.reproduce.ReproductionUtils.compareScores;

public class ReproductionUtilsTest {
  @Test
  public void testEquality() {
    assertEquals(OK, compareScores(0.0, 0.0, null));
    assertEquals(OK, compareScores(0.3123, 0.3123, null));
    assertEquals(OK, compareScores(0.0, 0.0, 0.0));
    assertEquals(OK, compareScores(0.3123, 0.3123, 0.0));
  }

  @Test
  public void testFloatingPointNoise() {
    assertEquals(OK, compareScores(0.3, 0.1 + 0.2, 0.0));
    assertEquals(OK, compareScores(0.1 + 0.2, 0.3, 0.0));
    double allowance = ReproductionUtils.NUMERICAL_TOLERANCE;
    assertEquals(OK, compareScores(allowance, 0.0, 0.0));
    assertEquals(OK, compareScores(0.0, allowance, 0.0));
    assertEquals(FAIL, compareScores(Math.nextUp(allowance), 0.0, 0.0));
    assertEquals(OKISH, compareScores(0.0, Math.nextUp(allowance), 0.0));
    // The numerical allowance is absolute, not relative to the score's scale.
    assertEquals(FAIL, compareScores(100.00000001, 100.0, 0.0));
    assertEquals(OK, compareScores(allowance, 0.0, null));
    assertEquals(OKISH, compareScores(Math.nextUp(allowance), 0.0, null));
  }

  @Test
  public void testNumericalToleranceDominatesSmallerConfiguredTolerance() {
    assertEquals(OK, compareScores(1e-9, 0.0, 1e-10));
    assertEquals(FAIL, compareScores(2e-9, 0.0, 1e-10));
  }

  @Test
  public void testImprovements() {
    assertEquals(OK, compareScores(0.5, 0.5625, 0.0625));
    assertEquals(OKISH, compareScores(0.5, 0.75, 0.0625));
    assertEquals(OKISH, compareScores(0.5, 0.75, 0.0));
    assertEquals(OKISH, compareScores(0.5, 0.75, null));
  }

  @Test
  public void testConfiguredToleranceBoundary() {
    // Exactly representable binary fractions avoid subtraction noise at boundaries.
    double tolerance = 0.0625;
    assertEquals(OK, compareScores(Math.nextDown(tolerance), 0.0, tolerance));
    assertEquals(OK, compareScores(tolerance, 0.0, tolerance));
    assertEquals(OKISH, compareScores(Math.nextUp(tolerance), 0.0, tolerance));
  }

  @Test
  public void testRelaxedToleranceBoundary() {
    double tolerance = 0.0625;
    double boundary = 1.5 * tolerance;
    assertEquals(OKISH, compareScores(Math.nextDown(boundary), 0.0, tolerance));
    assertEquals(OKISH, compareScores(boundary, 0.0, tolerance));
    assertEquals(FAIL, compareScores(Math.nextUp(boundary), 0.0, tolerance));
  }

  @Test
  public void testStrictFallbackWithoutConfiguredTolerance() {
    assertEquals(OKISH, compareScores(Math.nextDown(0.0002), 0.0, null));
    assertEquals(FAIL, compareScores(0.0002, 0.0, null));
    assertEquals(FAIL, compareScores(Math.nextUp(0.0002), 0.0, null));
    assertEquals(FAIL, compareScores(0.5, 0.25, null));
  }

  @Test
  public void testAbsentAndZeroToleranceProduceDifferentStatuses() {
    assertEquals(OKISH, compareScores(0.0001, 0.0, null));
    assertEquals(FAIL, compareScores(0.0001, 0.0, 0.0));
  }

  @Test
  public void testConfiguredToleranceDisablesFallback() {
    double tolerance = 0.0001;
    double boundary = 1.5 * tolerance;
    assertEquals(OK, compareScores(tolerance, 0.0, tolerance));
    assertEquals(OKISH, compareScores(boundary, 0.0, tolerance));
    assertEquals(FAIL, compareScores(Math.nextUp(boundary), 0.0, tolerance));
    assertEquals(FAIL, compareScores(0.00018, 0.0, tolerance));
    assertEquals(FAIL, compareScores(Math.nextDown(0.0002), 0.0, tolerance));
  }
}
