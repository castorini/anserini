package io.anserini.collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EpidemicQACollectionTest  extends DocumentCollectionTest<EpidemicQACollection.Document>
{
  @Before
  public void setUp() throws Exception {
    super.setUp();

    collectionPath = Paths.get("src/test/resources/sample_docs/epidemic_qa");
    collection = new EpidemicQACollection(collectionPath);

    Path segment = Paths.get("src/test/resources/sample_docs/epidemic_qa/info.txt");

    segmentPaths.add(segment);
    segmentDocCounts.put(segment, 1);

    totalSegments = 1;
    totalDocs = 1;

    Map<String, String> doc1 = new HashMap<>();
    doc1.put("id", "b5329o75");
    doc1.put("contents_starts_with", "Perspectives on monoclonal antibody therapy as potential therapeutic");
    doc1.put("contents_ends_with", "therapeutic targets for aMPV/C infection in the future.");
    doc1.put("contents_length", "32749");
    doc1.put("raw_length", "66689");
    doc1.put("title", "Perspectives on monoclonal antibody therapy as potential therapeutic intervention for Coronavirus disease-19 (COVID-19)");
    // Only the first URL is stored.
    doc1.put("url", "https://www.ncbi.nlm.nih.gov/pubmed/32134278/");
    doc1.put("authorsString", "Shanmugaraj, B.; Siriwattananon, K.; Wangkanont, K.; Phoolcharoen, W.");
    expected.put("b5329o75", doc1);
  }

  @Override
  void checkDocument(SourceDocument doc, Map<String, String> expected) {
    EpidemicQACollection.Document covidDoc = (EpidemicQACollection.Document) doc;

    assertEquals(expected.get("id"), covidDoc.id());
    assertTrue(covidDoc.contents().startsWith(expected.get("contents_starts_with")));
    assertTrue(covidDoc.contents().endsWith(expected.get("contents_ends_with")));
    assertEquals(Integer.parseInt(expected.get("contents_length")), covidDoc.contents().length());
    assertEquals(expected.get("title"), covidDoc.title());
    assertEquals(expected.get("url"), covidDoc.url());
    assertEquals(expected.get("authorsString"), covidDoc.authorsString());

    // Make sure raw() is a JSON containing proper fields, and check length.
    ObjectMapper mapper = new ObjectMapper();

    try {
      JsonNode jsonNode = mapper.readTree(covidDoc.raw());
      assertEquals(expected.get("id"), jsonNode.get("document_id").asText());
      JsonNode metadataNode = jsonNode.get("metadata");
      assertEquals(expected.get("title"), metadataNode.get("title").asText());
    } catch (Exception e) {
      fail("Failed to parse raw JSON");
    }
    assertEquals(Integer.parseInt(expected.get("raw_length")), covidDoc.raw().length());
  }
}
