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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import io.anserini.index.IndexReaderUtils;
import io.anserini.reproduce.ReproductionUtils;
import io.anserini.search.ScoredDoc;
import io.anserini.search.SimpleSearcher;
import io.anserini.util.LoggingBootstrap;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.util.JavalinLogger;

public final class RestServer implements Closeable {
  public static class Args {
    @Option(name = "--host", metaVar = "[address]", usage = "Address to bind server to")
    public String host = "0.0.0.0";

    @Option(name = "--port", metaVar = "[number]", usage = "Port to bind server to")
    public int port = 8080;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
      "--host", "--port", "--help"};

  private static final int DEFAULT_HITS = 10;
  private static final String OPENAPI_PATH = "/openapi.yaml";
  private static final String DOCS_PATH = "/docs";
  private static final byte[] OPENAPI_SPEC = loadOpenApiSpec();
  private static final byte[] DOCS_PAGE = loadResource("/rest/docs.html");
  private static final String ROUTE_ERROR = "Expected route /v1/{index}/search or /v1/{index}/doc/{docid}";
  private static final String CONTEXT_ERROR_MESSAGE = "errorMessage";

  private final Javalin app;
  private final String host;
  private final int port;
  private final ConcurrentHashMap<String, SimpleSearcher> searchers = new ConcurrentHashMap<>();

  RestServer(Args args) throws IOException {
    this.host = args.host;
    this.port = args.port;
    JavalinLogger.enabled = false;
    Configurator.setLevel("io.javalin", Level.ERROR);
    Configurator.setLevel("org.eclipse.jetty", Level.ERROR);
    this.app = Javalin.create(config -> config.showJavalinBanner = false);
    registerRoutes();
    app.exception(Exception.class, (e, ctx) -> writeError(ctx, 500, e.getMessage()));
    app.error(404, ctx -> writeJson(ctx, 404,
        Map.of("error", ctx.attribute(CONTEXT_ERROR_MESSAGE) == null ? ROUTE_ERROR : ctx.attribute(CONTEXT_ERROR_MESSAGE))));
    app.error(405, ctx -> writeJson(ctx, 405, Map.of("error", "Only GET is supported")));
  }

  void start() {
    app.start(host, port);
    System.out.printf("Anserini REST server listening on %s:%d%n", host, getPort());
    System.out.printf("v1 endpoint: GET /v1/{index}/search?query=...&hits=%d%n", DEFAULT_HITS);
  }

  int getPort() {
    return app.port();
  }

  private void registerRoutes() {
    app.get(OPENAPI_PATH, ctx -> writePlainText(ctx, "application/yaml; charset=utf-8", OPENAPI_SPEC));
    app.get(DOCS_PATH, ctx -> writePlainText(ctx, "text/html; charset=utf-8", DOCS_PAGE));
    app.get("/v1/{index}/search", this::handleSearch);
    app.get("/v1/{index}/doc/{docid}", this::handleDocumentFetch);
  }

  private void handleSearch(Context ctx) throws IOException {
    String index = decode(ctx.pathParam("index"));
    String query = ctx.queryParam("query");
    if (query == null || query.isBlank()) {
      writeError(ctx, 400, "Parameter 'query' is required");
      return;
    }

    int hits = DEFAULT_HITS;
    String hitsValue = ctx.queryParam("hits");
    if (hitsValue != null) {
      try {
        hits = Integer.parseInt(hitsValue);
      } catch (NumberFormatException e) {
        writeError(ctx, 400, "Parameter 'hits' must be an integer");
        return;
      }
    }

    if (hits <= 0) {
      writeError(ctx, 400, "Parameter 'hits' must be positive");
      return;
    }

    SimpleSearcher searcher = searchers.computeIfAbsent(index, this::createSearcher);
    if (searcher == null) {
      writeError(ctx, 400, "Unable to open index: " + index);
      return;
    }

    ScoredDoc[] results;
    List<Map<String, Object>> candidates = new ArrayList<>();
    synchronized (searcher) {
      results = searcher.search(query, hits);
      for (int rank = 0; rank < results.length; rank++) {
        candidates.add(toJsonCandidate(results[rank], rank + 1, searcher.doc(results[rank].docid)));
      }
    }

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("api", "v1");
    response.put("index", index);
    response.put("query", new LinkedHashMap<>(Map.of("text", query)));
    response.put("candidates", candidates);
    writeJson(ctx, 200, response);
  }

  private void handleDocumentFetch(Context ctx) throws IOException {
    String index = decode(ctx.pathParam("index"));
    String docid = decode(ctx.pathParam("docid"));
    if (docid == null || docid.isBlank()) {
      writeError(ctx, 400, "Path parameter 'docid' is required");
      return;
    }

    SimpleSearcher searcher = searchers.computeIfAbsent(index, this::createSearcher);
    if (searcher == null) {
      writeError(ctx, 400, "Unable to open index: " + index);
      return;
    }

    Document document;
    synchronized (searcher) {
      document = searcher.doc(docid);
    }

    if (document == null) {
      writeError(ctx, 404, "Document not found: " + docid);
      return;
    }

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("api", "v1");
    response.put("index", index);
    response.put("docid", docid);
    response.put("document", toDocumentJson(document));
    writeJson(ctx, 200, response);
  }

  private SimpleSearcher createSearcher(String index) {
    try {
      return new SimpleSearcher(IndexReaderUtils.getIndex(index).toString());
    } catch (Exception e) {
      return null;
    }
  }

  private static Map<String, Object> toJsonCandidate(ScoredDoc hit, int rank, Document document) {
    Map<String, Object> candidate = new LinkedHashMap<>();
    candidate.put("docid", hit.docid);
    candidate.put("score", hit.score);
    candidate.put("rank", rank);
    candidate.put("doc", toDocumentJson(document));

    return candidate;
  }

  private static Map<String, Object> toDocumentJson(Document document) {
    if (document == null) {
      return null;
    }

    Map<String, Object> fields = new LinkedHashMap<>();
    for (IndexableField field : document.getFields()) {
      String value = field.stringValue();
      if (value != null && !fields.containsKey(field.name())) {
        fields.put(field.name(), value);
      }
    }

    return fields;
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

  private static void writePlainText(Context ctx, String contentType, byte[] body) {
    ctx.contentType(contentType);
    ctx.result(new String(body, StandardCharsets.UTF_8));
  }

  private static void writeJson(Context ctx, int status, Map<String, ?> payload) {
    ctx.status(status);
    ctx.contentType("application/json; charset=utf-8");
    ctx.json(payload);
  }

  private static void writeError(Context ctx, int status, String message) {
    if (status == 404) {
      ctx.attribute(CONTEXT_ERROR_MESSAGE, message);
      ctx.status(404);
      return;
    }
    writeJson(ctx, status, Map.of("error", message));
  }

  @Override
  public void close() throws IOException {
    for (SimpleSearcher searcher : searchers.values()) {
      if (searcher != null) {
        searcher.close();
      }
    }
    app.stop();
  }

  public static void main(String[] args) {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format("Error: %s", e.getMessage()));
      ReproductionUtils.printUsage(parser, RestServer.class, argsOrdering);
      return;
    }

    if (parsedArgs.help) {
      ReproductionUtils.printUsage(parser, RestServer.class, argsOrdering);
      return;
    }

    if (parsedArgs.port <= 0 || parsedArgs.port > 65535) {
      System.err.println("Error: --port must be in [1, 65535]");
      return;
    }

    run(parsedArgs);
  }

  private static void run(Args args) {
    try {
      RestServer server = new RestServer(args);
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
