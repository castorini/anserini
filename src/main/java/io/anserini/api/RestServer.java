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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.document.Document;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.anserini.cli.CliUtils;
import io.anserini.index.IndexReaderUtils;
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

    @Option(name = "--index-config", metaVar = "[path]", usage = "Path to YAML config containing REST index aliases")
    public String indexConfig;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
      "--host", "--port", "--index-config", "--help"};

  private static final int DEFAULT_HITS = 10;
  private static final String OPENAPI_PATH = "/openapi.yaml";
  private static final String DOCS_PATH = "/docs";
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
  private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static final byte[] OPENAPI_SPEC = loadOpenApiSpec();
  private static final byte[] DOCS_PAGE = loadResource("/rest/docs.html");
  private static final String ROUTE_ERROR = "Expected route /v1/{index}/search or /v1/{index}/doc/{docid}";
  private static final String CONTEXT_ERROR_MESSAGE = "errorMessage";

  private final Javalin app;
  private final String host;
  private final int port;
  private final Map<String, String> configuredIndexes;
  private final ConcurrentHashMap<String, SimpleSearcher> searchers = new ConcurrentHashMap<>();

  private static class IndexConfig {
    public Map<String, String> indexes = Collections.emptyMap();
  }

  RestServer(Args args) throws IOException {
    this.host = args.host;
    this.port = args.port;
    this.configuredIndexes = loadConfig(args.indexConfig);
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
    if (!configuredIndexes.isEmpty()) {
      System.out.printf("Configured index aliases: %s%n", String.join(", ", configuredIndexes.keySet()));
    }
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

    Boolean parse = parseBooleanQueryParam(ctx, "parse", true);
    if (parse == null) {
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
        candidates.add(toJsonCandidate(results[rank], rank + 1, searcher.doc(results[rank].docid), parse));
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

    Boolean parse = parseBooleanQueryParam(ctx, "parse", true);
    if (parse == null) {
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
    response.put("doc", toDocumentResponseJson(document, parse));
    writeJson(ctx, 200, response);
  }

  private SimpleSearcher createSearcher(String index) {
    try {
      return new SimpleSearcher(resolveIndex(index).toString());
    } catch (Exception e) {
      return null;
    }
  }

  private Path resolveIndex(String index) throws IOException {
    String configuredIndex = configuredIndexes.get(index);
    if (configuredIndex != null) {
      return Paths.get(configuredIndex);
    }

    return IndexReaderUtils.getIndex(index);
  }

  private static Map<String, String> loadConfig(String configPath) throws IOException {
    if (configPath == null || configPath.isBlank()) {
      return Map.of();
    }

    try (InputStream inputStream = new FileInputStream(configPath)) {
      IndexConfig config = YAML_MAPPER.readValue(inputStream, IndexConfig.class);
      if (config == null || config.indexes == null || config.indexes.isEmpty()) {
        return Map.of();
      }

      LinkedHashMap<String, String> indexes = new LinkedHashMap<>();
      for (Map.Entry<String, String> entry : config.indexes.entrySet()) {
        String alias = entry.getKey();
        String configuredPath = entry.getValue();
        if (alias == null || alias.isBlank()) {
          throw new IllegalArgumentException("Index aliases in --index-config must be non-empty");
        }
        if (configuredPath == null || configuredPath.isBlank()) {
          throw new IllegalArgumentException("Index alias \"" + alias + "\" must map to a non-empty path");
        }

        Path resolvedPath = Paths.get(configuredPath);
        if (!resolvedPath.isAbsolute()) {
          Path configParent = Paths.get(configPath).toAbsolutePath().getParent();
          resolvedPath = (configParent == null ? resolvedPath.toAbsolutePath() : configParent.resolve(resolvedPath)).normalize();
        }

        if (!Files.exists(resolvedPath)) {
          throw new IllegalArgumentException("Index alias \"" + alias + "\" points to missing path: " + resolvedPath);
        }

        indexes.put(alias, resolvedPath.toString());
      }

      return Collections.unmodifiableMap(indexes);
    }
  }

  private static Map<String, Object> toJsonCandidate(ScoredDoc hit, int rank, Document document, boolean parse) {
    Map<String, Object> candidate = new LinkedHashMap<>();
    candidate.put("docid", hit.docid);
    candidate.put("score", hit.score);
    candidate.put("rank", rank);
    candidate.put("doc", toCandidateDocumentJson(document, parse));

    return candidate;
  }

  private static Boolean parseBooleanQueryParam(Context ctx, String name, boolean defaultValue) {
    String rawValue = ctx.queryParam(name);
    if (rawValue == null) {
      return defaultValue;
    }

    if ("true".equalsIgnoreCase(rawValue)) {
      return true;
    }

    if ("false".equalsIgnoreCase(rawValue)) {
      return false;
    }

    writeError(ctx, 400, "Parameter '" + name + "' must be 'true' or 'false'");
    return null;
  }

  private static Object toCandidateDocumentJson(Document document, boolean parse) {
    return CliUtils.formatDocument(document, parse, JSON_MAPPER);
  }

  private static Object toDocumentResponseJson(Document document, boolean parse) {
    return CliUtils.formatDocument(document, parse, JSON_MAPPER);
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
    Server jettyServer = app.jettyServer().server();
    app.stop();
    try {
      jettyServer.join();
      if (jettyServer.getThreadPool() instanceof QueuedThreadPool threadPool) {
        threadPool.join();
      }
      interruptStartupWatchdog();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while waiting for REST server shutdown", e);
    } finally {
      jettyServer.destroy();
    }
  }

  private static void interruptStartupWatchdog() throws InterruptedException {
    for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
      Thread thread = entry.getKey();
      if (!thread.isAlive()) {
        continue;
      }
      for (StackTraceElement element : entry.getValue()) {
        if ("io.javalin.jetty.JettyUtil".equals(element.getClassName())
            && element.getMethodName().contains("maybeLogIfServerNotStarted")) {
          thread.setUncaughtExceptionHandler((t, e) -> { });
          thread.interrupt();
          thread.join();
          break;
        }
      }
    }
  }

  public static void main(String[] args) {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(String.format("Error: %s", e.getMessage()));
      CliUtils.printUsage(parser, RestServer.class, argsOrdering);
      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, RestServer.class, argsOrdering);
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
    } catch (Exception e) {
      System.err.printf("Error: %s%n", e.getMessage());
    }
  }
}
