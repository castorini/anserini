/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import io.anserini.search.SearchArgs;
import io.anserini.search.SearchCollection;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.TestRuleLimitSysouts;
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
import java.util.List;

// This automatically tests indexing, retrieval, and evaluation from end to end.
// Subclasses inherit and special to different collections.
@TestRuleLimitSysouts.Limit(bytes=20000)
public abstract class EndToEndTest extends LuceneTestCase {
  protected IndexArgs indexCollectionArgs = new IndexArgs();
  protected SearchArgs searchArgs = new SearchArgs();
  //protected EvalArgs evalArgs = new EvalArgs();

  protected String dataDirPath;
  protected String dataDirPrefix = "src/test/resources/sample_docs/";
  protected String indexOutputPrefix = "e2eTestIndex";
  protected String collectionClass;
  protected String generator;
  protected String topicDirPrefix = "src/test/resources/sample_topics/";
  protected String topicReader;
  protected String searchOutputPrefix = "e2eTestSearch";
  protected String qrelsDirPrefix = "src/test/resources/sample_qrels/";
  protected String[] evalMetrics = new String[]{"map"};
  protected String[] referenceRunOutput;

  // These are the sources of truth
  protected int fieldNormStatusTotalFields;
  protected int termIndexStatusTermCount;
  protected int termIndexStatusTotFreq;
  protected int termIndexStatusTotPos;
  protected int storedFieldStatusTotalDocCounts;
  protected int storedFieldStatusTotFields;
  protected int docCount;

  protected int counterIndexed;
  protected int counterEmpty;
  protected int counterUnindexable;
  protected int counterSkipped;
  protected int counterErrors;

  // init the class variables here
  protected abstract void init() throws Exception;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    init();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    FileUtils.deleteDirectory(new File(this.indexOutputPrefix+this.collectionClass));
    new File(this.searchOutputPrefix+this.topicReader).delete();
    super.tearDown();
  }

  protected void checkCounters(IndexCollection.Counters counters) {
    assertEquals(counterIndexed, counters.indexed.get());
    assertEquals(counterEmpty, counters.empty.get());
    assertEquals(counterUnindexable, counters.unindexable.get());
    assertEquals(counterSkipped, counters.skipped.get());
    assertEquals(counterErrors, counters.errors.get());
  }

  protected void checkIndex() throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
    Directory dir = FSDirectory.open(Paths.get(this.indexOutputPrefix+this.collectionClass));

    IndexReader reader = DirectoryReader.open(dir);
    assertEquals(docCount, reader.maxDoc());
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

  protected void setIndexingArgs() {
    // required
    indexCollectionArgs.collectionClass = this.collectionClass+"Collection";
    indexCollectionArgs.generatorClass = this.generator+"Generator";
    indexCollectionArgs.threads = 2;
    indexCollectionArgs.input = this.dataDirPrefix+this.dataDirPath;
    indexCollectionArgs.index = this.indexOutputPrefix+this.collectionClass;

    //optional
    indexCollectionArgs.storePositions = true;
    indexCollectionArgs.storeDocvectors = true;
    indexCollectionArgs.storeTransformedDocs = true;
    indexCollectionArgs.storeRawDocs = true;
    indexCollectionArgs.optimize = true;
    indexCollectionArgs.quiet = true;
  }

  protected void testIndexing() throws Exception {
    setIndexingArgs();
    try {
      IndexCollection.Counters counters = new IndexCollection(indexCollectionArgs).run();
      checkCounters(counters);
      checkIndex();
    } catch (Exception e) {
      System.out.println("Test Indexing failed");
      fail();
    }
  }

  protected void setSearchArgs() {
    // required
    searchArgs.index = this.indexOutputPrefix+this.collectionClass;
    searchArgs.topics = new String[]{this.topicDirPrefix+this.topicReader};
    searchArgs.output = this.searchOutputPrefix+this.topicReader;
    searchArgs.topicReader = this.topicReader;
    searchArgs.bm25 = true;

    //optional
    searchArgs.topicfield = "title";
    searchArgs.searchtweets = false;
    searchArgs.hits = 1000;
    searchArgs.keepstop = false;
  }

  protected<K> void testSearching() {
    setSearchArgs();
    try {
      SearchCollection searcher = new SearchCollection(searchArgs);
      searcher.runTopics();
      searcher.close();
      checkRankingResults(searchArgs.output);
    } catch (Exception e) {
      System.out.println("Test Searching failed: ");
      e.printStackTrace();
      fail();
    }
  }

  protected void checkRankingResults(String output) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(output));

    int cnt = 0;
    String s;
    while ((s = br.readLine()) != null) {
      assertEquals(referenceRunOutput[cnt], s);
      cnt++;
    }

    assertEquals(cnt, referenceRunOutput.length);
  }

  @Test
  public void testAll() throws Exception {
    testIndexing();
    testSearching();
  }
}
