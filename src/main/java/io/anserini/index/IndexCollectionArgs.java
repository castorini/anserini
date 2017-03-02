/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.index;

import org.kohsuke.args4j.Option;

/**
 * Common arguments (dataDir, indexPath, numThreads, etc) necessary for indexing.
 */
public class IndexCollectionArgs {

  // required arguments

  @Option(name = "-input", metaVar = "[Path]", required = true, usage = "collection path")
  public String input;

  @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
  public String index;

  @Option(name = "-threads", metaVar = "[Number]", required = true, usage = "Number of Threads")
  public int threads;

  @Option(name = "-collection", required = true, usage = "collection class in io.anserini.collection")
  public String collectionClass;

  @Option(name = "-generator", required = true, usage = "document generator in io.anserini.index.generator")
  public String generatorClass;

  // optional arguments

  @Option(name = "-keepstopwords", usage = "boolean switch to keep stopwords")
  public boolean keepstop = false;

  @Option(name = "-positions", usage = "boolean switch to index positions")
  public boolean positions = false;

  @Option(name = "-docvectors", usage = "boolean switch to store document vectors")
  public boolean docvectors = false;

  @Option(name = "-optimize", usage = "boolean switch to optimize index (force merge)")
  public boolean optimize = false;
}