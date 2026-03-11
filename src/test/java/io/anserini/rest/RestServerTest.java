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

package io.anserini.rest;

import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.SuppresedLoggingLuceneTestCase;

public class RestServerTest extends SuppresedLoggingLuceneTestCase {
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
  }

  @Test
  public void testDocumentEndpoint() throws Exception {
    String index = URLEncoder.encode(
        "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2",
        StandardCharsets.UTF_8);
    String docid = URLEncoder.encode("DOC222", StandardCharsets.UTF_8);
    TestResponse response = sendGet(baseUrl + "/v1/" + index + "/documents/" + docid);

    assertEquals(200, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertEquals("v1", body.get("api").asText());
    assertEquals("DOC222", body.get("docid").asText());
    assertEquals("DOC222", body.get("document").get("id").asText());
  }

  @Test
  public void testDocumentNotFound() throws Exception {
    String index = URLEncoder.encode(
        "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2",
        StandardCharsets.UTF_8);
    String docid = URLEncoder.encode("NOT_A_REAL_DOC", StandardCharsets.UTF_8);
    TestResponse response = sendGet(baseUrl + "/v1/" + index + "/documents/" + docid);

    assertEquals(404, response.statusCode);
    JsonNode body = JSON_MAPPER.readTree(response.body);
    assertTrue(body.get("error").asText().contains("Document not found"));
  }

  @Test
  public void testOpenApiEndpoint() throws Exception {
    TestResponse response = sendGet(baseUrl + "/openapi.yaml");

    assertEquals(200, response.statusCode);
    assertTrue(response.body.contains("openapi: 3.0.3"));
    assertTrue(response.body.contains("/v1/{index}/search:"));
    assertTrue(response.body.contains("/v1/{index}/documents/{docid}:"));
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

  private static class TestResponse {
    private final int statusCode;
    private final String body;

    private TestResponse(int statusCode, String body) {
      this.statusCode = statusCode;
      this.body = body;
    }
  }
}
