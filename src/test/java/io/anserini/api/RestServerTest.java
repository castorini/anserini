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

package io.anserini.api;

import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

public class RestServerTest extends StdOutStdErrRedirectableLuceneTestCase {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  private RestServer server;
  private String baseUrl;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    RestServer.Args args = new RestServer.Args();
    args.host = "127.0.0.1";
    args.port = 0;

    server = new RestServer(args);
    server.start();

    baseUrl = "http://127.0.0.1:" + server.getPort();
  }

  @Override
  public void tearDown() throws Exception {
    try {
      if (server != null) {
        server.close();
      }
    } finally {
      super.tearDown();
    }
  }

  @Test
  public void testInvalidOption() throws Exception {
    redirectStdOut();
    redirectStdErr();
    String output;
    try {
      RestServer.main(new String[] {"--invalid"});
      output = out.toString(StandardCharsets.UTF_8) + err.toString(StandardCharsets.UTF_8);
    } finally {
      restoreStdOut();
      restoreStdErr();
    }

    assertTrue(output.contains("Error:"));
    assertTrue(output.contains("\"--invalid\" is not a valid option"));
    assertTrue(output.contains("Options for RestServer:"));
    assertFalse(output.contains("Anserini REST server listening on"));
  }

  @Test
  public void testInvalidStartupOptions() throws Exception {
    redirectStdOut();
    redirectStdErr();
    String output;
    try {
      RestServer.main(new String[] {"--port", "0"});
      output = out.toString(StandardCharsets.UTF_8) + err.toString(StandardCharsets.UTF_8);
    } finally {
      restoreStdOut();
      restoreStdErr();
    }

    assertTrue(output.contains("Error: --port must be in [1, 65535]"));
    assertFalse(output.contains("Anserini REST server listening on"));
  }

  @Test
  public void testHelp() throws Exception {
    redirectStdOut();
    redirectStdErr();
    String output;
    try {
      RestServer.main(new String[] {"--help"});
      output = out.toString(StandardCharsets.UTF_8) + err.toString(StandardCharsets.UTF_8);
    } finally {
      restoreStdOut();
      restoreStdErr();
    }

    assertTrue(output.contains("Options for RestServer:"));
    assertTrue(output.contains("--host [address]"));
    assertTrue(output.contains("--port [number]"));
    assertTrue(output.contains("--help"));
    assertFalse(output.contains("Anserini REST server listening on"));
  }

  @Test
  public void testRouteValidation() throws Exception {
    TestResponse response = sendGet(baseUrl + "/v1/bad/route");

    assertEquals(404, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertTrue(body.get("error").asText().contains("Expected route"));
  }

  @Test
  public void testMissingQueryValidation() throws Exception {
    TestResponse response = sendGet(baseUrl + "/v1/any-index/search");

    assertEquals(400, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertTrue(body.get("error").asText().contains("query"));
  }

  @Test
  public void testInvalidHitsValidation() throws Exception {
    TestResponse response = sendGet(baseUrl + "/v1/any-index/search?query=text&hits=0");

    assertEquals(400, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertTrue(body.get("error").asText().contains("positive"));
  }

  @Test
  public void testInvalidParseValidation() throws Exception {
    TestResponse response = sendGet(baseUrl + "/v1/any-index/search?query=text&parse=maybe");

    assertEquals(400, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertTrue(body.get("error").asText().contains("parse"));
  }

  @Test
  public void testSearchEndpoint() throws Exception {
    String index = URLEncoder.encode(
        "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2",
        StandardCharsets.UTF_8);
    TestResponse response = sendGet(baseUrl + "/v1/" + index + "/search?query=text&hits=2");

    assertEquals(200, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertEquals("v1", body.get("api").asText());
    assertEquals(2, body.get("candidates").size());
    assertEquals("DOC222", body.get("candidates").get(0).get("docid").asText());
    assertTrue(body.get("candidates").get(0).has("doc"));
    JsonNode doc = body.get("candidates").get(0).get("doc");
    assertTrue(doc.isTextual() || doc.isObject());
  }

  @Test
  public void testSearchEndpointParseTrueUsesParsedRawJson() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=text&hits=1");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);
    String docid = candidate.get("docid").asText();

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(docid, StandardCharsets.UTF_8));
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    JsonNode expected = expectedParsedDocument(documentBody.get("doc"));
    JsonNode actual = candidate.get("doc");
    assertEquals(expected, actual);
  }

  @Test
  public void testSearchEndpointParseFalseUsesStoredFields() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=text&hits=1&parse=false");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertTrue(candidate.get("doc").isTextual());
    assertTrue(candidate.get("doc").asText().contains("<HEAD>HEAD</HEAD>"));
  }

  @Test
  public void testDocumentEndpoint() throws Exception {
    String index = URLEncoder.encode(
        "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2",
        StandardCharsets.UTF_8);
    String docid = URLEncoder.encode("DOC222", StandardCharsets.UTF_8);
    TestResponse response = sendGet(baseUrl + "/v1/" + index + "/doc/" + docid);

    assertEquals(200, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertEquals("v1", body.get("api").asText());
    assertEquals("DOC222", body.get("docid").asText());
    assertTrue(body.has("doc"));
    assertTrue(body.get("doc").isTextual() || body.get("doc").isObject());
  }

  @Test
  public void testDocumentEndpointUsesNormalizedDocumentShape() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);
    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=text&hits=1");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);
    String docid = candidate.get("docid").asText();

    TestResponse response = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(docid, StandardCharsets.UTF_8));
    assertEquals(200, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);

    assertEquals(candidate.get("doc"), body.get("doc"));
  }

  @Test
  public void testCacmRawParseTrue() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_cacm.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=preliminary&hits=1");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertEquals("CACM-0001", candidate.get("docid").asText());
    assertTrue(candidate.get("doc").isTextual());
    assertTrue(candidate.get("doc").asText().contains("Preliminary Report"));

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(candidate.get("docid").asText(), StandardCharsets.UTF_8));
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    assertEquals(candidate.get("docid"), documentBody.get("docid"));
    assertEquals(candidate.get("doc"), documentBody.get("doc"));
  }

  @Test
  public void testCacmRawParseFalse() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_cacm.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=preliminary&hits=1&parse=false");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertEquals("CACM-0001", candidate.get("docid").asText());
    assertTrue(candidate.get("doc").isTextual());
    assertTrue(candidate.get("doc").asText().contains("Preliminary Report"));

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(candidate.get("docid").asText(), StandardCharsets.UTF_8) + "?parse=false");
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    assertEquals(candidate.get("docid"), documentBody.get("docid"));
    assertEquals(candidate.get("doc"), documentBody.get("doc"));
  }

  @Test
  public void testMsMarcoV1PassageRawParseTrue() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_msmarco-v1-passage.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=obliterated&hits=1");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertEquals("0", candidate.get("docid").asText());
    assertTrue(candidate.get("doc").isTextual());
    assertTrue(candidate.get("doc").asText().contains("hundreds of thousands of innocent lives obliterated"));

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(candidate.get("docid").asText(), StandardCharsets.UTF_8));
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    assertEquals(candidate.get("docid"), documentBody.get("docid"));
    assertEquals(candidate.get("doc"), documentBody.get("doc"));
  }

  @Test
  public void testMsMarcoV1PassageRawParseFalse() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_msmarco-v1-passage.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=obliterated&hits=1&parse=false");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertEquals("0", candidate.get("docid").asText());
    assertTrue(candidate.get("doc").isTextual());
    assertTrue(candidate.get("doc").asText().contains("\"id\""));
    assertTrue(candidate.get("doc").asText().contains("\"contents\""));
    assertTrue(candidate.get("doc").asText().contains("hundreds of thousands of innocent lives obliterated"));

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(candidate.get("docid").asText(), StandardCharsets.UTF_8) + "?parse=false");
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    assertEquals(candidate.get("docid"), documentBody.get("docid"));
    assertEquals(candidate.get("doc"), documentBody.get("doc"));
  }

  @Test
  public void testMsMarcoV21DocSegmentedRawParseTrue() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_msmarco-v2.1-doc-segmented.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=demerara&hits=1");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertEquals("msmarco_v2.1_doc_01_0#0_0", candidate.get("docid").asText());
    assertTrue(candidate.get("doc").isObject());
    assertEquals("What’s the difference between golden, brown and demerara sugar? | Edmonton Journal",
        candidate.get("doc").get("title").asText());
    assertTrue(candidate.get("doc").get("segment").asText().contains("Not all brown sugars are the same"));

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(candidate.get("docid").asText(), StandardCharsets.UTF_8));
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    assertEquals(candidate.get("docid"), documentBody.get("docid"));
    assertEquals(candidate.get("doc"), documentBody.get("doc"));
  }

  @Test
  public void testMsMarcoV21DocSegmentedRawParseFalse() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_msmarco-v2.1-doc-segmented.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=demerara&hits=1&parse=false");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertEquals("msmarco_v2.1_doc_01_0#0_0", candidate.get("docid").asText());
    assertTrue(candidate.get("doc").isTextual());
    assertTrue(candidate.get("doc").asText().contains("\"title\""));
    assertTrue(candidate.get("doc").asText().contains("\"segment\""));
    assertTrue(candidate.get("doc").asText().contains("Not all brown sugars are the same"));

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(candidate.get("docid").asText(), StandardCharsets.UTF_8) + "?parse=false");
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    assertEquals(candidate.get("docid"), documentBody.get("docid"));
    assertEquals(candidate.get("doc"), documentBody.get("doc"));
  }

  @Test
  public void testBeirNfcorpusRawParseTrue() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_beir-nfcorpus.flat.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=statin&hits=1");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertEquals("MED-10", candidate.get("docid").asText());
    assertTrue(candidate.get("doc").isObject());
    assertEquals("Statin Use and Breast Cancer Survival: A Nationwide Cohort Study from Finland",
        candidate.get("doc").get("title").asText());
    assertTrue(candidate.get("doc").get("text").asText().contains("Recent studies have suggested"));

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(candidate.get("docid").asText(), StandardCharsets.UTF_8));
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    assertEquals(candidate.get("docid"), documentBody.get("docid"));
    assertEquals(candidate.get("doc"), documentBody.get("doc"));
  }

  @Test
  public void testBeirNfcorpusRawParseFalse() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_beir-nfcorpus.flat.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);

    TestResponse searchResponse = sendGet(baseUrl + "/v1/" + index + "/search?query=statin&hits=1&parse=false");
    assertEquals(200, searchResponse.statusCode);
    JsonNode searchBody = JSON_MAPPER.readTree(searchResponse.body);
    JsonNode candidate = searchBody.get("candidates").get(0);

    assertEquals("MED-10", candidate.get("docid").asText());
    assertTrue(candidate.get("doc").isTextual());
    assertTrue(candidate.get("doc").asText().contains("\"_id\""));
    assertTrue(candidate.get("doc").asText().contains("\"title\""));
    assertTrue(candidate.get("doc").asText().contains("\"text\""));
    assertTrue(candidate.get("doc").asText().contains("Statin Use and Breast Cancer Survival"));

    TestResponse documentResponse = sendGet(baseUrl + "/v1/" + index + "/doc/" +
        URLEncoder.encode(candidate.get("docid").asText(), StandardCharsets.UTF_8) + "?parse=false");
    assertEquals(200, documentResponse.statusCode);
    JsonNode documentBody = JSON_MAPPER.readTree(documentResponse.body);

    assertEquals(candidate.get("docid"), documentBody.get("docid"));
    assertEquals(candidate.get("doc"), documentBody.get("doc"));
  }

  @Test
  public void testDocumentNotFound() throws Exception {
    String rawIndex = "src/test/resources/prebuilt_indexes/lucene-inverted.sample_cacm.store_raw";
    String index = URLEncoder.encode(rawIndex, StandardCharsets.UTF_8);
    String docid = URLEncoder.encode("NOT_A_REAL_DOC", StandardCharsets.UTF_8);
    TestResponse response = sendGet(baseUrl + "/v1/" + index + "/doc/" + docid);

    assertEquals(404, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertTrue(body.get("error").asText().contains("Document not found"));
  }

  @Test
  public void testOpenApiEndpoint() throws Exception {
    TestResponse response = sendGet(baseUrl + "/openapi.yaml");

    assertEquals(200, response.statusCode);
    assertTrue(response.body.contains("openapi: 3.0.3"));
    assertTrue(response.body.contains("url: /v1"));
    assertTrue(response.body.contains("/{index}/search:"));
    assertTrue(response.body.contains("/{index}/doc/{docid}:"));
  }

  @Test
  public void testDocsEndpoint() throws Exception {
    TestResponse response = sendGet(baseUrl + "/docs");

    assertEquals(200, response.statusCode);
    assertTrue(response.body.contains("SwaggerUIBundle"));
    assertTrue(response.body.contains("url: '/openapi.yaml'"));
  }

  private TestResponse sendGet(String url) throws Exception {
    URLConnection connection = URI.create(url).toURL().openConnection();
    connection.setDoInput(true);
    java.net.HttpURLConnection http = (java.net.HttpURLConnection) connection;
    http.setRequestMethod("GET");
    int statusCode = http.getResponseCode();

    String body;
    try (Scanner scanner = new Scanner(
        statusCode >= 400 ? http.getErrorStream() : http.getInputStream(), StandardCharsets.UTF_8)) {
      scanner.useDelimiter("\\A");
      body = scanner.hasNext() ? scanner.next() : "";
    }

    return new TestResponse(statusCode, body);
  }

  private static JsonNode expectedParsedDocument(JsonNode document) throws Exception {
    if (!document.has("raw")) {
      return document;
    }

    try {
      JsonNode rawJson = JSON_MAPPER.readTree(document.get("raw").asText());
      if (rawJson.isObject()) {
        Map<String, Object> normalized = new LinkedHashMap<>();
        rawJson.properties().forEach(field -> {
          if ("id".equals(field.getKey()) || "_id".equals(field.getKey()) || "docid".equals(field.getKey())) {
            return;
          }
          JsonNode value = field.getValue();
          normalized.put(field.getKey(), value.isValueNode() ? value.asText() : JSON_MAPPER.convertValue(value, Object.class));
        });

        if (normalized.size() == 1) {
          return JSON_MAPPER.valueToTree(normalized.values().iterator().next());
        }

        return JSON_MAPPER.valueToTree(normalized);
      }
    } catch (Exception e) {
      // Fall back to stored fields below.
    }

    return document;
  }

  private static class TestResponse {
    private final int statusCode;
    private final String body;

    private TestResponse(int statusCode, String body) {
      this.statusCode = statusCode;
      this.body = body;
    }
  }
}
