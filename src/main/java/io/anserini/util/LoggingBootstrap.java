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

package io.anserini.util;

import org.slf4j.bridge.SLF4JBridgeHandler;

public final class LoggingBootstrap {
  private LoggingBootstrap() {}

  public static void installJulToSlf4jBridge() {
    // Remove default JUL handlers attached to the root logger
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    // Route JUL logs into SLF4J
    SLF4JBridgeHandler.install();
  }
}
