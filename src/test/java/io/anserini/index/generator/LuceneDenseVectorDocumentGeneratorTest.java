package io.anserini.index.generator;

import java.io.IOException;
import java.nio.file.Files;

import io.anserini.IndexerTestBase;
import io.anserini.collection.SourceDocument;
import io.anserini.index.IndexHnswDenseVectors;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.ByteVectorValues;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

public class LuceneDenseVectorDocumentGeneratorTest extends IndexerTestBase {

  @Test
  public void test() throws InvalidDocumentException, IOException {
    IndexHnswDenseVectors.Args args = new IndexHnswDenseVectors.Args();
    LuceneDenseVectorDocumentGenerator<SourceDocument> generator = new LuceneDenseVectorDocumentGenerator<>(args);
    SourceDocument source = new SourceDocument() {
      @Override
      public String id() {
        return "123";
      }

      @Override
      public String contents() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < ByteVectorValues.MAX_DIMENSIONS; i++) {
          if (stringBuilder.length() > 1) {
            stringBuilder.append(",");
          }
          stringBuilder.append(i);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
      }

      @Override
      public String raw() {
        return null;
      }

      @Override
      public boolean indexable() {
        return false;
      }
    };
    Document document = generator.createDocument(source);
    try (Directory directory = FSDirectory.open(Files.createTempDirectory("hnsw-sample"));
         IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig())) {
      writer.addDocument(document);
      writer.commit();
    }
  }
}