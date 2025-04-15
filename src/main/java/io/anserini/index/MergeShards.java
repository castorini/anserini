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

package io.anserini.index;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.nio.file.Paths;

public class MergeShards {

public static void main(String[] args) throws Exception {
  if (args.length < 2) {
    System.err.println("Usage: MergeShards <output_dir> <shard_dir1> [<shard_dir2> ...]");
    System.exit(1);
  }

  String outputDir = args[0];
  FSDirectory mergedDir = FSDirectory.open(Paths.get(outputDir));
  IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
  IndexWriter writer = new IndexWriter(mergedDir, config);

  for (int i = 1; i < args.length; i++) {
    System.out.println("Adding index: " + args[i]);
    FSDirectory shardDir = FSDirectory.open(Paths.get(args[i]));
    writer.addIndexes(shardDir);
  }

  System.out.println("Merging...");
  writer.forceMerge(1);
  writer.close();
  System.out.println("Done. Merged index at: " + outputDir);
  }
    
}
