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

public final class WordEmbeddingDictionary {

  public static final class Args {
    @Option(name = "-index", metaVar = "[Path]", required = true, usage = "index path")
    public String index;

    @Option(name = "-term", metaVar = "String", usage = "get the embeddings corresponding to the term")
    public String term = "";
  }

  private final Path indexPath;
  private final FSDirectory directory;
  private final DirectoryReader reader;
  private final IndexSearcher searcher;
  private final WhitespaceAnalyzer analyzer;

  public WordEmbeddingDictionary(String indexPath) throws IOException {
    this.indexPath = Paths.get(indexPath);
    this.directory = FSDirectory.open(this.indexPath);
    this.reader = DirectoryReader.open(directory);
    this.searcher = new IndexSearcher(reader);
    this.analyzer = new WhitespaceAnalyzer();
  }

  public float[] getEmbeddingVector(String term) throws IOException, TermNotFoundException {
    Query query = AnalyzerUtils.buildBagOfWordsQuery(FIELD_ID, analyzer, term);
    TopDocs rs = searcher.search(query, 1);
    ScoredDocuments docs = ScoredDocuments.fromTopDocs(rs, searcher);

    if (rs.totalHits == 0) {
      throw new TermNotFoundException(term);
    }

    byte[] val = docs.documents[0].getField(FIELD_BODY).binaryValue().bytes;
    FloatBuffer floatBuffer = ByteBuffer.wrap(val).asFloatBuffer();
    float[] floatArray = new float[floatBuffer.limit()];
    floatBuffer.get(floatArray);
    return floatArray;
  }

  public static void main(String[] args) throws Exception {
    WordEmbeddingDictionary.Args dictionaryArgs = new WordEmbeddingDictionary.Args();
    CmdLineParser parser = new CmdLineParser(dictionaryArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: "+ WordEmbeddingDictionary.class.getSimpleName() +
              parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    WordEmbeddingDictionary index = new WordEmbeddingDictionary(dictionaryArgs.index);

    if (!dictionaryArgs.term.isEmpty()) {
      System.out.println(Arrays.toString(index.getEmbeddingVector(dictionaryArgs.term)));
    }
  }
}
