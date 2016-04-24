package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.RerankerContext;
import io.anserini.util.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Terms;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MockDirectoryWrapper;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will contain setup and teardown code for testing feature extractors
 */
abstract public class BaseFeatureExtractorTest extends LuceneTestCase {
  protected static final String TEST_FIELD_NAME = "text";
  protected static final Analyzer TEST_ANALYZER = new EnglishAnalyzer();
  protected static final QueryParser TEST_PARSER = new QueryParser(TEST_FIELD_NAME, TEST_ANALYZER);
  protected static final String DEFAULT_QID = "1";

  // Acceptable delta for float assert
  protected static final float DELTA = 0.01f;
  protected Directory DIRECTORY;

  protected IndexWriter testWriter;

  /**
   * A lot of feature extractors are tested individually, easy way to wrap the chain
   * @param extractors  The extractors
   * @return
   */
  protected static FeatureExtractors getChain(FeatureExtractor... extractors ) {
    FeatureExtractors chain = new FeatureExtractors();
    for (FeatureExtractor extractor : extractors) {
      chain.add(extractor);
    }
    return chain;
  }

  protected Document addTestDocument(String testText) throws IOException {
    FieldType fieldType = new FieldType();
    fieldType.setStored(true);
    fieldType.setStoreTermVectors(true);
    fieldType.setStoreTermVectorOffsets(true);
    fieldType.setStoreTermVectorPositions(true);
    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    Field field = new Field(TEST_FIELD_NAME, testText, fieldType);
    Document doc = new Document();
    doc.add(field);
    testWriter.addDocument(doc);
    testWriter.commit();
    return doc;
  }

  /**
   * The reranker context constructed will return with a searcher
   * and the query we want with dummy query ids and null filter
   * @return
   */
  protected RerankerContext makeTestContext(String queryText) {
    try {
      RerankerContext context = new RerankerContext(new IndexSearcher(DirectoryReader.open(DIRECTORY)), TEST_PARSER.parse(queryText), DEFAULT_QID,
              queryText, AnalyzerUtils.tokenize(TEST_ANALYZER, queryText), TEST_FIELD_NAME, null);
      return context;
    } catch (ParseException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
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
    // Use a RAMDirectory instead of MemoryIndex because we might test with multiple documents
    DIRECTORY = new MockDirectoryWrapper(new Random(), new RAMDirectory());
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
                                     FeatureExtractors extractors) throws IOException {
    assertFeatureValues(expected, queryText, Lists.newArrayList(docText), extractors,0);
  }

  // just add a signature for single extractor
  protected void assertFeatureValues(float[] expected, String queryText, String docText,
                                     FeatureExtractor extractor) throws IOException {
    assertFeatureValues(expected, queryText, Lists.newArrayList(docText), getChain(extractor),0);
  }

  /**
   * Used to test features involving multiple documents in the collection at the same time
   * @param expected            An array of expected values for the computed features
   * @param queryText           Query
   * @param docTexts            A list of document texts representing documents in the collection
   * @param extractors          The chain of feature extractors to use
   * @param docToExtract        Index of the document we want to compute features for
   */
  protected void assertFeatureValues(float[] expected, String queryText, List<String> docTexts,
                                     FeatureExtractors extractors, int docToExtract) throws IOException {
    List<Document> addedDocs = new ArrayList<>();
    for (String docText : docTexts) {
      Document testDoc = addTestDocument(docText);
      addedDocs.add(testDoc);
    }
    testWriter.forceMerge(1);

    Document testDoc = addedDocs.get(docToExtract);
    RerankerContext context = makeTestContext(queryText);
    IndexReader reader = context.getIndexSearcher().getIndexReader();
    Terms terms = reader.getTermVector(docToExtract, TEST_FIELD_NAME);
    float[] extractedFeatureValues = extractors.extractAll(testDoc, terms, context);

    assertArrayEquals(expected, extractedFeatureValues, DELTA);
  }

}
