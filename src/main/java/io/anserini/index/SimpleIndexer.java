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

import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.collection.JsonCollection;
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;
import io.anserini.index.generator.GeneratorException;
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleIndexer {
  private final Path indexPath;
  private final IndexWriter writer;
  private final LuceneDocumentGenerator generator = new DefaultLuceneDocumentGenerator();

  public SimpleIndexer(String indexPath) throws IOException {
    this.indexPath = Paths.get(indexPath);
    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }

    final Directory dir = FSDirectory.open(this.indexPath);
    final DefaultEnglishAnalyzer analyzer = DefaultEnglishAnalyzer.newDefaultInstance();

    final IndexWriterConfig config;
    config = new IndexWriterConfig(analyzer);

    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(2048);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    writer = new IndexWriter(dir, config);

  }

  public boolean addDocument(String docid, String contents) {
    JsonCollection.Document doc = new JsonCollection.Document(docid, contents);
    try {
      writer.addDocument(generator.createDocument(doc));
    } catch (GeneratorException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  public void close() {
    boolean optimize = false;

    // Do a final commit
    try {
      if (writer != null) {
        writer.commit();
        if (optimize) {
          writer.forceMerge(1);
        }
      }
    } catch (IOException e) {
      // It is possible that this happens... but nothing much we can do at this point,
      // so just log the error and move on.
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        // It is possible that this happens... but nothing much we can do at this point,
        // so just log the error and move on.
      }
    }
  }
}
