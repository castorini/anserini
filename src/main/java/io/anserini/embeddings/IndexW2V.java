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

package io.anserini.embeddings;

import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.AnalyzerUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

public final class IndexW2V {
  private static final Logger LOG = LogManager.getLogger(WordEmbeddingDictionary.class);

  public static final class Args {
    // required arguments
    @Option(name = "-input", metaVar = "[Path]", required = true, usage = "collection path")
    public String input;

    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    public String index;
  }

  private final IndexW2V.Args args;
  private final Path indexPath;
  private final FSDirectory directory;

  public IndexW2V(IndexW2V.Args args) throws Exception {
    this.args = args;
    this.indexPath = Paths.get(args.index);

    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }

    this.directory = FSDirectory.open(indexPath);
  }

  public void indexEmbeddings() throws IOException, InterruptedException {
    LOG.info("Starting indexer...");
    long startTime = System.currentTimeMillis();
    final WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);
    final IndexWriter writer = new IndexWriter(directory, config);

    BufferedReader bRdr = new BufferedReader(new FileReader(args.input));
    String line = null;
    bRdr.readLine();

    Document document = new Document();
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    int cnt = 0;

    while ((line = bRdr.readLine()) != null){
      String[] termEmbedding = line.trim().split("\t");
      document.add(new StringField(LuceneDocumentGenerator.FIELD_ID, termEmbedding[0], Field.Store.NO));
      String[] parts = termEmbedding[1].split(" ");

      for (int i = 0; i < parts.length; ++i) {
        byteStream.write(ByteBuffer.allocate(4).putFloat(Float.parseFloat(parts[i])).array());
      }
      document.add(new StoredField(FIELD_BODY, byteStream.toByteArray()));

      byteStream.flush();
      byteStream.reset();
      writer.addDocument(document);
      document.clear();
      cnt++;

      if (cnt % 100000 == 0) {
        LOG.info(cnt + " terms indexed");
      }
    }

    LOG.info(String.format("Total of %s terms added", cnt));

    try {
      writer.commit();
      writer.forceMerge(1);
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        LOG.error(e);
      }
    }

    LOG.info("Total elapsed time: " + (System.currentTimeMillis() - startTime) + "ms");
  }

  public static void main(String[] args) throws Exception {
    IndexW2V.Args indexCollectionArgs = new IndexW2V.Args();
    CmdLineParser parser = new CmdLineParser(indexCollectionArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ WordEmbeddingDictionary.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    new IndexW2V(indexCollectionArgs).indexEmbeddings();
  }
}
