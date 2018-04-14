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

package io.anserini.end2end;

import io.anserini.eval.Eval;
import io.anserini.eval.EvalArgs;
import io.anserini.index.IndexCollection;
import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.search.SearchArgs;
import io.anserini.search.SearchCollection;
import io.anserini.search.query.TopicReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class EndToEndTest extends LuceneTestCase {
  protected IndexCollection.Args indexCollectionArgs = new IndexCollection.Args();
  protected SearchArgs searchArgs = new SearchArgs();
  protected EvalArgs evalArgs = new EvalArgs();

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


  // These are the sources of truth
  protected int fieldNormStatusTotalFields;
  protected int termIndexStatusTermCount;
  protected int termIndexStatusTotFreq;
  protected int termIndexStatusTotPos;
  protected int storedFieldStatusTotalDocCounts;
  protected int storedFieldStatusTotFields;

  protected float evalMetricValue;

  // init the class variables here
  protected abstract void init();

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    init();
  }

  @After
  @Override
  public void tearDown() throws Exception {
    //FileUtils.deleteDirectory(new File(this.indexOutputPrefix+this.collectionClass));
    //new File(this.searchOutputPrefix+this.topicReader).delete();
    super.tearDown();
  }

  protected void checkIndex() throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
    Directory dir = FSDirectory.open(Paths.get(this.indexOutputPrefix+this.collectionClass));
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
  }

  protected void testIndexing() throws Exception {
    setIndexingArgs();
    try {
      new IndexCollection(indexCollectionArgs).run();
      checkIndex();
    } catch (Exception e) {
      System.out.println("Test Indexing failed");
      fail();
    }
  }

  protected void setSearchArgs() {
    // required
    searchArgs.index = this.indexOutputPrefix+this.collectionClass;
    searchArgs.topics = this.topicDirPrefix+this.topicReader;
    searchArgs.output = this.searchOutputPrefix+this.topicReader;
    searchArgs.topicReader = this.topicReader;

    //optional
    searchArgs.topicfield = "title";
    searchArgs.searchtweets = false;
    searchArgs.hits = 1000;
    searchArgs.keepstop = false;
    searchArgs.inmem = true;
  }

  protected void testSearching() {
    setSearchArgs();
    try {
      Path topicsFile = Paths.get(searchArgs.topics);
      TopicReader tr = (TopicReader)Class.forName("io.anserini.search.query."+searchArgs.topicReader+"TopicReader")
          .getConstructor(Path.class).newInstance(topicsFile);
      SortedMap<Integer, Map<String, String>> topics = tr.read();

      Similarity similarity = new BM25Similarity(0.9f, 0.4f);
      RerankerCascade cascade = new RerankerCascade();
      cascade.add(new IdentityReranker());
      SearchCollection searcher = new SearchCollection(searchArgs.index);
      searcher.search(topics, searchArgs.topicfield, searchArgs.output, similarity, searchArgs.hits,
          cascade, false, searchArgs.keepstop, searchArgs.searchtweets);
      searcher.close();
      checkRankingResults();
    } catch (Exception e) {
      System.out.println("Test Searching failed");
      fail();
    }
  }

  protected void checkRankingResults() {

  }

  protected void setEvalArgs() {
    // required
    evalArgs.runPath = this.searchOutputPrefix+this.topicReader;
    evalArgs.qrelPath = this.qrelsDirPrefix+this.topicReader;
  }

  protected void testEval() throws Exception {
    setEvalArgs();
    try {
      Eval.setAllMetrics(this.evalMetrics);
      Eval.eval(evalArgs.runPath, evalArgs.qrelPath);
      assertEquals(Eval.getAllEvals().get(this.evalMetrics[0]).aggregated,
          this.evalMetricValue, 0.001);
    } catch (Exception e) {
      System.out.println("Test Eval failed");
      fail();
    }
  }

  @Test
  public void testAll() throws Exception {
    testIndexing();
    testSearching();
    testEval();
  }
}
