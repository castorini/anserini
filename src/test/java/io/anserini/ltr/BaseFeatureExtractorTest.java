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

package io.anserini.ltr;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.FeatureExtractor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * This class will contain setup and teardown code for testing feature extractors
 */
abstract public class BaseFeatureExtractorTest<T> extends LuceneTestCase {
  protected static final String TEST_FIELD_NAME = IndexArgs.CONTENTS;
  protected static final Analyzer TEST_ANALYZER = new EnglishAnalyzer();
  protected static final Analyzer NON_STOP_TEST_ANALYZER = new WhitespaceAnalyzer();

  // Acceptable delta for float assert
  protected static final float DELTA = 0.01f;

  protected Directory DIRECTORY;
  protected IndexWriter testWriter;

  protected static List<FeatureExtractor> getChain(FeatureExtractor... extractors ) {
    return Arrays.asList(extractors);
  }

  protected void addTestDocument(String testText, String docId) throws IOException {
    FieldType fieldType = new FieldType();
    fieldType.setStored(true);
    fieldType.setStoreTermVectors(true);
    fieldType.setStoreTermVectorPositions(true);
    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    Field field = new Field(TEST_FIELD_NAME, testText, fieldType);
    Document doc = new Document();
    doc.add(field);
    doc.add(new StringField(IndexArgs.ID, docId, Field.Store.YES));
    testWriter.addDocument(doc);
    testWriter.commit();
  }

  /**
   * MUST call super
   * constructs the necessary rerankers and extractorchains
   * @throws Exception
   */
  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    DIRECTORY = FSDirectory.open(createTempDir());
    testWriter = new IndexWriter(DIRECTORY, new IndexWriterConfig(TEST_ANALYZER));
  }

  /**
   * MUST call super
   * @throws Exception
   */
  @Override
  @After
  public void tearDown() throws Exception {
    super.tearDown();
    testWriter.close();
  }

  /**
   * Main test method, will run the extractors on the queryText supplied, assert that the feature values are the same
   * as expected
   * @param expected
   * @param queryText
   */
  protected void assertFeatureValues(float[] expected, String queryText, String docText,
                                     List<FeatureExtractor> extractors) throws IOException, ExecutionException, InterruptedException {
    assertFeatureValues(expected, "-1", queryText, Arrays.asList(docText), extractors,0);
  }

  // just add a signature for single extractor
  protected void assertFeatureValues(float[] expected, String queryText, String docText,
                                     FeatureExtractor extractor) throws IOException, ExecutionException, InterruptedException {
    assertFeatureValues(expected, "-1", queryText, Arrays.asList(docText), Arrays.asList(extractor),0);
  }

  // just add a signature for single extractor
  protected void assertFeatureValues(float[] expected, String queryText, List<String> docTexts,
                                     FeatureExtractor extractor, int docToExtract) throws IOException, ExecutionException, InterruptedException {
    assertFeatureValues(expected, "-1" , queryText, docTexts, Arrays.asList(extractor),docToExtract);
  }

  // just add a signature for single extractor
  protected void assertFeatureValues(float[] expected, String qid, String queryText, String docTexts,
                                     FeatureExtractor extractor) throws IOException, ExecutionException, InterruptedException {
    assertFeatureValues(expected, qid , queryText, Arrays.asList(docTexts), Arrays.asList(extractor),0);
  }

  // just add a signature for single extractor
  protected void assertFeatureValues(float[] expected, String queryText, List<String> docTexts,
                                     List<FeatureExtractor> extractors, int docToExtract) throws IOException, ExecutionException, InterruptedException {
    assertFeatureValues(expected, "-1" , queryText, docTexts, extractors, docToExtract);
  }

  /**
   * Used to test features involving multiple documents in the collection at the same time
   * @param expected            An array of expected values for the computed features
   * @param queryText           Query
   * @param docTexts            A list of document texts representing documents in the collection
   * @param extractors          The chain of feature extractors to use
   * @param docToExtract        Index of the document we want to compute features for
   */
  protected void assertFeatureValues(float[] expected, String qid, String queryText, List<String> docTexts,
                                     List<FeatureExtractor> extractors, int docToExtract) throws IOException, ExecutionException, InterruptedException {
    int id = 0;
    for (String docText : docTexts) {
      addTestDocument(docText, String.format("doc%s", id));
      id += 1;
    }
    testWriter.commit();

    FeatureExtractorUtils utils = new FeatureExtractorUtils(DirectoryReader.open(DIRECTORY));
    for(FeatureExtractor extractor: extractors){
      utils.add(extractor);
    }
    String docIdToExtract = String.format("doc%s", docToExtract);


    List<debugOutput> extractedFeatureValues = utils.extract(qid, Arrays.asList(docIdToExtract), AnalyzerUtils.analyze(TEST_ANALYZER, queryText));
    List<Float> extractFeatures = null;
    for(debugOutput doc: extractedFeatureValues) {
      if(doc.pid.equals(docIdToExtract))
        if(extractFeatures == null)
          extractFeatures = doc.features;
    }
    float[] extractFeaturesArray = new float[extractFeatures.size()];
    for (int i=0; i < extractFeaturesArray.length; i++)
    {
      extractFeaturesArray[i] = extractFeatures.get(i).floatValue();
    }
    assertArrayEquals(expected, extractFeaturesArray, DELTA);
    utils.close();
  }

}
