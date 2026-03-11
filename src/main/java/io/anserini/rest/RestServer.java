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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import io.anserini.index.Constants;
import io.anserini.index.IndexReaderUtils;
import io.anserini.search.ScoredDoc;
import io.anserini.search.SimpleSearcher;
import io.anserini.util.LoggingBootstrap;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

/**
 * Minimal REST API server for lexical search.
 */
public final class RestServer implements Closeable {
  public static class Args {
    @Option(name = "--host", metaVar = "[address]", usage = "Address to bind server to")
    public String host = "0.0.0.0";

    @Option(name = "--port", metaVar = "[number]", usage = "Port to bind server to")
    public int port = 8080;

    @Option(name = "--options", usage = "Print information about options.")
    public Boolean options = false;
  }

  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
  private static final int DEFAULT_HITS = 10;
  private static final String OPENAPI_PATH = "/openapi.yaml";
  private static final String DOCS_PATH = "/docs";
  private static final byte[] OPENAPI_SPEC = loadOpenApiSpec();
  private static final byte[] DOCS_PAGE = loadResource("/rest/docs.html");

  private final HttpServer server;
  private final ExecutorService executor;
  private final ConcurrentHashMap<String, SimpleSearcher> searchers = new ConcurrentHashMap<>();

  RestServer(Args args) throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(args.host, args.port), 0);
    this.server.createContext("/", this::handleRequest);
    this.executor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
    this.server.setExecutor(executor);
  }

  void start() {
    server.start();
    System.out.printf("Anserini REST server listening on %s%n", server.getAddress());
    System.out.printf("v1 endpoint: GET /v1/{index}/search?query=...&hits=%d%n", DEFAULT_HITS);
  }

  int getPort() {
    return server.getAddress().getPort();
  }

  private void handleRequest(HttpExchange exchange) throws IOException {
    try {
      if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        writeJson(exchange, 405, Map.of("error", "Only GET is supported"));
        return;
      }

      URI uri = exchange.getRequestURI();
      if (OPENAPI_PATH.equals(uri.getPath())) {
        writePlainText(exchange, 200, "application/yaml; charset=utf-8", OPENAPI_SPEC);
        return;
      }
      if (DOCS_PATH.equals(uri.getPath())) {
        writePlainText(exchange, 200, "text/html; charset=utf-8", DOCS_PAGE);
        return;
      }

      String[] parts = Arrays.stream(uri.getRawPath().split("/"))
          .filter(s -> !s.isBlank())
          .toArray(String[]::new);

      if (parts.length < 3 || !"v1".equals(parts[0])) {
        writeJson(exchange, 404, Map.of("error", "Expected route /v1/{index}/search or /v1/{index}/documents/{docid}"));
        return;
      }

      String index = decode(parts[1]);
      if (parts.length == 3 && "search".equals(parts[2])) {
        handleSearch(index, uri, exchange);
        return;
      }

      if (parts.length == 4 && "documents".equals(parts[2])) {
        handleDocumentFetch(index, decode(parts[3]), exchange);
        return;
      }

      writeJson(exchange, 404, Map.of("error", "Expected route /v1/{index}/search or /v1/{index}/documents/{docid}"));
    } catch (Exception e) {
      writeJson(exchange, 500, Map.of("error", e.getMessage()));
    } finally {
      exchange.close();
    }
  }

  private void handleSearch(String index, URI uri, HttpExchange exchange) throws IOException {
    Map<String, String> params = parseQuery(uri.getRawQuery());

    String query = params.get("query");
    if (query == null || query.isBlank()) {
      writeJson(exchange, 400, Map.of("error", "Parameter 'query' is required"));
      return;
    }

    int hits = DEFAULT_HITS;
    if (params.containsKey("hits")) {
      try {
        hits = Integer.parseInt(params.get("hits"));
      } catch (NumberFormatException e) {
        writeJson(exchange, 400, Map.of("error", "Parameter 'hits' must be an integer"));
        return;
      }
    }

    if (hits <= 0) {
      writeJson(exchange, 400, Map.of("error", "Parameter 'hits' must be positive"));
      return;
    }

    SimpleSearcher searcher = searchers.computeIfAbsent(index, this::createSearcher);
    if (searcher == null) {
      writeJson(exchange, 400, Map.of("error", "Unable to open index: " + index));
      return;
    }

    ScoredDoc[] results;
    synchronized (searcher) {
      results = searcher.search(query, hits);
    }

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("api", "v1");
    response.put("index", index);
    response.put("query", new LinkedHashMap<>(Map.of("text", query)));
    List<Map<String, Object>> candidates = new ArrayList<>();
    for (int rank = 0; rank < results.length; rank++) {
      candidates.add(toJsonCandidate(results[rank], rank + 1));
    }
    response.put("candidates", candidates);
    writeJson(exchange, 200, response);
  }

  private void handleDocumentFetch(String index, String docid, HttpExchange exchange) throws IOException {
    if (docid == null || docid.isBlank()) {
      writeJson(exchange, 400, Map.of("error", "Path parameter 'docid' is required"));
      return;
    }

    SimpleSearcher searcher = searchers.computeIfAbsent(index, this::createSearcher);
    if (searcher == null) {
      writeJson(exchange, 400, Map.of("error", "Unable to open index: " + index));
      return;
    }

    Document document;
    synchronized (searcher) {
      document = searcher.doc(docid);
    }

    if (document == null) {
      writeJson(exchange, 404, Map.of("error", "Document not found: " + docid));
      return;
    }

    Map<String, Object> fields = new LinkedHashMap<>();
    for (IndexableField field : document.getFields()) {
      String value = field.stringValue();
      if (value != null && !fields.containsKey(field.name())) {
        fields.put(field.name(), value);
      }
    }

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("api", "v1");
    response.put("index", index);
    response.put("docid", docid);
    response.put("document", fields);
    writeJson(exchange, 200, response);
  }

  private SimpleSearcher createSearcher(String index) {
    try {
      return new SimpleSearcher(IndexReaderUtils.getIndex(index).toString());
    } catch (Exception e) {
      return null;
    }
  }

  private static Map<String, Object> toJsonCandidate(ScoredDoc hit, int rank) {
    Map<String, Object> candidate = new LinkedHashMap<>();
    candidate.put("docid", hit.docid);
    candidate.put("score", hit.score);
    candidate.put("rank", rank);

    try {
      String raw = hit.lucene_document == null ? null : hit.lucene_document.get(Constants.RAW);
      if (raw == null) {
        candidate.put("doc", null);
      } else {
        JsonNode doc = JSON_MAPPER.readTree(raw);
        candidate.put("doc", doc);
      }
    } catch (IOException e) {
      Map<String, Object> rawDoc = new LinkedHashMap<>();
      String raw = hit.lucene_document == null ? null : hit.lucene_document.get(Constants.RAW);
      rawDoc.put("raw", raw);
      candidate.put("doc", rawDoc);
    }

    return candidate;
  }

  private static Map<String, String> parseQuery(String rawQuery) {
    Map<String, String> params = new LinkedHashMap<>();
    if (rawQuery == null || rawQuery.isBlank()) {
      return params;
    }

    for (String pair : rawQuery.split("&")) {
      int idx = pair.indexOf('=');
      if (idx < 0) {
        params.put(decode(pair), "");
      } else {
        params.put(decode(pair.substring(0, idx)), decode(pair.substring(idx + 1)));
      }
    }
    return params;
  }

  private static String decode(String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }

  private static byte[] loadOpenApiSpec() {
    try (InputStream in = RestServer.class.getResourceAsStream("/rest/openapi.yaml")) {
      if (in == null) {
        throw new IllegalStateException("Missing OpenAPI spec resource: /rest/openapi.yaml");
      }
      return in.readAllBytes();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load OpenAPI spec resource", e);
    }
  }

  private static byte[] loadResource(String path) {
    try (InputStream in = RestServer.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new IllegalStateException("Missing resource: " + path);
      }
      return in.readAllBytes();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load resource: " + path, e);
    }
  }

  private static void writePlainText(HttpExchange exchange, int status, String contentType, byte[] body) throws IOException {
    exchange.getResponseHeaders().set("Content-Type", contentType);
    exchange.sendResponseHeaders(status, body.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(Objects.requireNonNull(body));
    }
  }

  private static void writeJson(HttpExchange exchange, int status, Map<String, ?> payload) throws IOException {
    byte[] body = JSON_MAPPER.writeValueAsBytes(payload);
    writePlainText(exchange, status, "application/json; charset=utf-8", body);
  }

  @Override
  public void close() throws IOException {
    for (SimpleSearcher searcher : searchers.values()) {
      if (searcher != null) {
        searcher.close();
      }
    }
    server.stop(0);
    executor.shutdownNow();
  }

  public static void main(String[] args) {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsed = new Args();
    CmdLineParser parser = new CmdLineParser(parsed, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (parsed.options) {
        System.err.printf("Options for %s:%n%n", RestServer.class.getSimpleName());
        parser.printUsage(System.err);
      } else {
        System.err.printf("Error: %s%n", e.getMessage());
        System.err.printf("For help, use \"--options\" to print out information about options.%n");
      }
      return;
    }

    if (parsed.port <= 0 || parsed.port > 65535) {
      System.err.println("Error: --port must be in [1, 65535]");
      return;
    }

    try {
      RestServer server = new RestServer(parsed);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try {
          server.close();
        } catch (IOException ignored) {
          // Ignore shutdown exceptions.
        }
      }));
      server.start();
    } catch (IOException e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }
}
