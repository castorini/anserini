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

package io.anserini.integration;

import io.anserini.index.IndexCollection;
import io.anserini.index.IndexReaderUtils;
import io.anserini.index.NotStoredException;
import io.anserini.search.SearchCollection;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.apache.lucene.tests.util.TestRuleLimitSysouts;
import org.apache.lucene.util.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

// This automatically tests indexing, retrieval, and evaluation from end to end.
// Subclasses inherit and special to different collections.
@TestRuleLimitSysouts.Limit(bytes = 20000)
public abstract class EndToEndTest extends LuceneTestCase {
  private static final Random RANDOM = new Random();

  protected Map<String, SearchCollection.Args> testQueries = new HashMap<>();

  protected String indexPath;

  protected String topicReader;
  protected String topicFile;
  protected String searchOutputPrefix = "e2eTestSearch";
  protected Map<String, String[]> referenceRunOutput = new HashMap<>();
  protected Map<String, Map<String, String>> referenceDocs = new HashMap<>();
  protected Map<String, Map<String, List<String>>> referenceDocTokens = new HashMap<>();
  protected Map<String, List<String>>  queryTokens = new HashMap<>();

  // These are the sources of truth
  protected int fieldNormStatusTotalFields;
  protected int termIndexStatusTermCount;
  protected int termIndexStatusTotFreq;
  protected int termIndexStatusTotPos;
  protected int storedFieldStatusTotalDocCounts;
  protected int storedFieldStatusTotFields;

  protected int docCount;       // Total number of docs.
  protected int docFieldCount;  // Each doc should have this number of fields.

  // List of files for cleanup in @After.
  protected List<File> cleanup = new ArrayList<>();

  @Override
  @Before
  public void setUp() throws Exception {
    Locale.setDefault(Locale.US);

    // We're going to build an index for every test.
    super.setUp();
    indexPath = "test-index" + RANDOM.nextInt(100000);

    cleanup.clear();

    // Subclasses will override this method and change their own settings.
    IndexCollection.Args indexArgs = getIndexArgs();

    // Note, since we want to test end-to-end, we're going to generate command-line parameters to feed back into main.
    List<String> args = new ArrayList<>(List.of(
        "-index", indexPath,
        "-input", indexArgs.input,
        "-threads", "2",
        "-language", indexArgs.language,
        "-collection", indexArgs.collectionClass,
        "-generator", indexArgs.generatorClass));

    if (indexArgs.tweetMaxId != Long.MAX_VALUE) {
      args.add("-tweet.maxId");
      args.add(indexArgs.tweetMaxId + "");
    }

    if (indexArgs.whitelist != null) {
      args.add("-whitelist");
      args.add(indexArgs.whitelist);
    }

    if (indexArgs.storePositions) {
      args.add("-storePositions");
    }

    if (indexArgs.storeDocvectors) {
      args.add("-storeDocvectors");
    }

    if (indexArgs.storeContents) {
      args.add("-storeContents");
    }

    if (indexArgs.storeRaw) {
      args.add("-storeRaw");
    }

    if (indexArgs.keepStopwords) {
      args.add("-keepStopwords");
    }

    if (indexArgs.stopwords != null) {
      args.add("-stopwords");
      args.add(indexArgs.stopwords);
    }

    if (indexArgs.optimize) {
      args.add("-optimize");
    }

    if (indexArgs.quiet) {
      args.add("-quiet");
    }

    if (indexArgs.shardCount > 1) {
      args.add("-shard.count");
      args.add(Integer.toString(indexArgs.shardCount));
      args.add("-shard.current");
      args.add(Integer.toString(indexArgs.shardCurrent));
    }

    if (indexArgs.pretokenized) {
      args.add("-pretokenized");
    }

    if (indexArgs.fields != null) {
      args.add("-fields");
      for (String field: indexArgs.fields) {
        args.add(field);
      }
    }

    IndexCollection.main(args.toArray(new String[args.size()]));
  }

  // Set the indexing args. Subclasses will override this method and change their own settings.
  abstract IndexCollection.Args getIndexArgs();

  protected IndexCollection.Args createDefaultIndexArgs() {
    IndexCollection.Args args = new IndexCollection.Args();

    args.storePositions = true;
    args.storeDocvectors = true;
    args.storeContents = true;
    args.storeRaw = true;
    args.optimize = true;
    args.quiet = true;

    return args;
  }

  @Override
  @After
  public void tearDown() throws Exception {
    // Clean up the index.
    FileUtils.deleteDirectory(new File(indexPath));

    // Clean up other files we've created along the way.
    for (File file : cleanup) {
      file.delete();
    }
    super.tearDown();
  }

  @Test
  public void checkIndex() throws IOException {
    // Subclasses will override this method and provide the ground truth.
    setCheckIndexGroundTruth();

    ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
    Directory dir = FSDirectory.open(Paths.get(this.indexPath));

    IndexReader reader = DirectoryReader.open(dir);
    assertEquals(docCount, reader.maxDoc());

    for (int i=0; i<reader.maxDoc(); i++) {
      String collectionDocid = IndexReaderUtils.convertLuceneDocidToDocid(reader, i);
      if (referenceDocs.get(collectionDocid).get("raw") != null) {
        assertEquals(referenceDocs.get(collectionDocid).get("raw"), IndexReaderUtils.documentRaw(reader, collectionDocid));
      }
      if (referenceDocs.get(collectionDocid).get("contents") != null) {
        assertEquals(referenceDocs.get(collectionDocid).get("contents"), IndexReaderUtils.documentContents(reader, collectionDocid));
      }

      // Make sure each doc has the right number of fields.
      // If the docFieldCount == -1, it means that documents may have variable number of fields (e.g., AclAnthology),
      // so don't bother testing.
      if (docFieldCount != -1) {
        assertEquals(docFieldCount, IndexReaderUtils.document(reader, collectionDocid).getFields().size());
      }

      // Check list of tokens by calling document vector.
      if (!referenceDocTokens.isEmpty()){
        try {
          List<String> docTokens = IndexReaderUtils.getDocumentTokens(reader, collectionDocid);
          assertEquals(referenceDocTokens.get(collectionDocid).get("contents"), docTokens);
        } catch (NotStoredException e) {
          e.printStackTrace();
        }
      }
    }
    reader.close();

    CheckIndex checker = new CheckIndex(dir);
    checker.setInfoStream(new PrintStream(bos, false, IOUtils.UTF_8));
    if (VERBOSE) checker.setInfoStream(System.out);
    CheckIndex.Status indexStatus = checker.checkIndex();
    if (!indexStatus.clean) {
      System.out.println("CheckIndex failed");
      System.out.println(bos.toString(IOUtils.UTF_8));
      fail();
    }

    final CheckIndex.Status.SegmentInfoStatus seg = indexStatus.segmentInfos.get(0);
    assertTrue(seg.openReaderPassed);

    assertNotNull(seg.diagnostics);

    assertNotNull(seg.fieldNormStatus);
    assertNull(seg.fieldNormStatus.error);
    assertEquals(this.fieldNormStatusTotalFields, seg.fieldNormStatus.totFields);

    assertNotNull(seg.termIndexStatus);
    assertNull(seg.termIndexStatus.error);
    assertEquals(this.termIndexStatusTermCount, seg.termIndexStatus.termCount);
    assertEquals(this.termIndexStatusTotFreq, seg.termIndexStatus.totFreq);
    assertEquals(this.termIndexStatusTotPos, seg.termIndexStatus.totPos);

    assertNotNull(seg.storedFieldStatus);
    assertNull(seg.storedFieldStatus.error);
    assertEquals(this.storedFieldStatusTotalDocCounts, seg.storedFieldStatus.docCount);
    assertEquals(this.storedFieldStatusTotFields, seg.storedFieldStatus.totFields);

    assertTrue(seg.diagnostics.size() > 0);
    final List<String> onlySegments = new ArrayList<>();
    onlySegments.add("_0");

    assertTrue(checker.checkIndex(onlySegments).clean);
    checker.close();
  }

  // Subclasses will override this method and provide the ground truth.
  protected abstract void setCheckIndexGroundTruth();

  protected SearchCollection.Args createDefaultSearchArgs() {
    SearchCollection.Args searchArgs = new SearchCollection.Args();
    // required
    searchArgs.index = this.indexPath;
    searchArgs.output = this.searchOutputPrefix + this.topicReader;
    searchArgs.topicReader = this.topicReader;
    searchArgs.topics = new String[]{this.topicFile};
    searchArgs.bm25 = true;

    // optional
    searchArgs.topicfield = "title";
    searchArgs.searchtweets = false;
    searchArgs.hits = 1000;
    searchArgs.keepstop = false;

    return searchArgs;
  }

  @Test
  public void testSearching() {
    // Subclasses will override this method and provide the ground truth.
    setSearchGroundTruth();

    try {
      for (Map.Entry<String, SearchCollection.Args> entry : testQueries.entrySet()) {
        SearchCollection searcher = new SearchCollection(entry.getValue());
        searcher.runTopics();
        searcher.close();

        checkRankingResults(entry.getKey(), entry.getValue().output);
        // Remember to clean up run files.
        cleanup.add(new File(entry.getValue().output));
      }
    } catch (Exception e) {
      System.out.println("Test Searching failed: ");
      e.printStackTrace();
      fail();
    }
  }

  protected void checkRankingResults(String key, String output) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(output));
    String[] ref = referenceRunOutput.get(key);

    int cnt = 0;
    String s;
    while ((s = br.readLine()) != null) {
      assertEquals(ref[cnt], s);
      cnt++;
    }

    assertEquals(cnt, ref.length);
  }

  // Subclasses will override this method and provide the ground truth.
  protected abstract void setSearchGroundTruth();
}
