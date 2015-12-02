package io.anserini.index;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.anserini.document.Collection;
import org.kohsuke.args4j.Option;

/**
 * Common arguments (dataDir, indexPath, numThreads, etc) necessary for indexing.
 */
public class IndexArgs {

  // required arguments

  @Option(name = "-input", metaVar = "[Path]", required = true, usage = "Collection Directory")
  String input;

  @Option(name = "-index", metaVar = "[Path]", required = true, usage = "Lucene index")
  String index;

  @Option(name = "-threads", metaVar = "[Number]", required = true, usage = "Number of Threads")
  int threads;

  @Option(name = "-collection", required = true, usage = "Collection")
  protected Collection collection;

  // optional arguments

  @Option(name = "-positions", usage = "Boolean switch to index positions")
  boolean positions = false;

  @Option(name = "-docvectors", usage = "Boolean switch to store document vectors")
  boolean docvectors = false;

  @Option(name = "-optimize", usage = "Boolean switch to optimize index (force merge)")
  boolean optimize = false;

  @Option(name = "-doclimit", metaVar = "[Number]", required = false, usage = "Maximum number of *.warc documents to index (-1 to index everything)")
  int doclimit = -1;
}