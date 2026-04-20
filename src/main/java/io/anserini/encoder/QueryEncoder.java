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

package io.anserini.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.encoder.dense.ArcticEmbedLEncoder;
import io.anserini.encoder.dense.BgeBaseEn15Encoder;
import io.anserini.encoder.dense.BgeLargeEn15Encoder;
import io.anserini.encoder.dense.CosDprDistilEncoder;
import io.anserini.encoder.dense.DenseEncoder;
import io.anserini.encoder.sparse.SpladePlusPlusEnsembleDistilEncoder;
import io.anserini.encoder.sparse.SpladePlusPlusSelfDistilEncoder;
import io.anserini.encoder.sparse.SpladeV3Encoder;
import io.anserini.encoder.sparse.SparseEncoder;
import io.anserini.encoder.sparse.UniCoilEncoder;
import io.anserini.search.topicreader.TopicReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.GZIPOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encodes a set of queries with any Anserini ONNX encoder and writes the
 * results to a JSONL file (one JSON object per line).
 *
 * <p>Topics are read via the standard {@link TopicReader} infrastructure, using
 * the same {@code -topicReader} and {@code -topicField} convention as
 * {@code SearchCollection}.
 *
 * <p>Dense output — feed to SearchCollection with
 * {@code -topicReader JsonStringVector -topicField vector}:
 * <pre>{"qid":"301","vector":[0.021,-0.003,...]}</pre>
 *
 * <p>Sparse output — feed to SearchCollection with
 * {@code -topicReader JsonString -impact -pretokenized}:
 * <pre>{"id":"301","title":"retrieval retrieval information information ..."}</pre>
 *
 * <p>Supported encoders:
 * <ul>
 *   <li>Dense:  ArcticEmbedL, BgeBaseEn15, BgeLargeEn15, CosDprDistil</li>
 *   <li>Sparse: SpladePlusPlusEnsembleDistil, SpladePlusPlusSelfDistil,
 *               SpladeV3, UniCoil</li>
 * </ul>
 *
 * <p>Example invocation via fatjar:
 * <pre>
 * bin/run.sh io.anserini.encoder.QueryEncoder \
 *   -encoder SpladeV3 \
 *   -queries tools/topics-and-qrels/topics.rag25.test.jsonl \
 *   -topicReader JsonString \
 *   -output encoded-queries/topics.rag25.test.splade-v3.jsonl
 * </pre>
 */
public class QueryEncoder {
  private static final Logger LOG = LogManager.getLogger(QueryEncoder.class);

  private static final String TOPIC_READER_PACKAGE = "io.anserini.search.topicreader.";

  public static final Set<String> DENSE_ENCODER_NAMES = Set.of(
      "ArcticEmbedL", "BgeBaseEn15", "BgeLargeEn15", "CosDprDistil"
  );

  public static final Set<String> SPARSE_ENCODER_NAMES = Set.of(
      "SpladePlusPlusEnsembleDistil", "SpladePlusPlusSelfDistil",
      "SpladeV3", "UniCoil"
  );

  public static class Args {
    @Option(name = "-encoder", metaVar = "[name]", required = true,
        usage = "Encoder name. Dense: ArcticEmbedL | BgeBaseEn15 | BgeLargeEn15 | CosDprDistil. " +
                "Sparse: SpladePlusPlusEnsembleDistil | SpladePlusPlusSelfDistil | SpladeV3 | UniCoil.")
    public String encoder;

    @Option(name = "-queries", metaVar = "[path]", required = true,
        usage = "Path to the queries file.")
    public String queriesPath;

    @Option(name = "-topicReader", metaVar = "[class]",
        usage = "TopicReader class to use, e.g. JsonString, TsvString, TsvInt. " +
                "Resolved to io.anserini.search.topicreader.{topicReader}TopicReader. " +
                "Default: JsonString.")
    public String topicReader = "JsonString";

    @Option(name = "-topicField", metaVar = "[field]",
        usage = "Topic field to use as query text. Default: title.")
    public String topicField = "title";

    @Option(name = "-output", metaVar = "[path]", required = true,
        usage = "Output file path.")
    public String outputPath;

    @Option(name = "-outputFormat", metaVar = "[format]",
        usage = "Output format: jsonl or tsv. " +
                "Defaults to tsv when -topicReader is TsvString or TsvInt, otherwise jsonl. " +
                "jsonl — Dense: {\"qid\":\"...\",\"vector\":[...]}; Sparse: {\"id\":\"...\",\"title\":\"tok tok ...\"}. " +
                "tsv  — Dense: id<TAB>f1 f2 ...; Sparse: id<TAB>tok tok tok ...")
    public String outputFormat = null;  // null = auto-detect from topicReader

    @Option(name = "-compress", usage = "Gzip-compress the output file.")
    public boolean compress = false;
  }

  /** Instantiates the named encoder. Models are downloaded and cached on first use. */
  public static OnnxEncoder<?> buildEncoder(String name) throws Exception {
    switch (name) {
      case "ArcticEmbedL":                 return new ArcticEmbedLEncoder();
      case "BgeBaseEn15":                  return new BgeBaseEn15Encoder();
      case "BgeLargeEn15":                 return new BgeLargeEn15Encoder();
      case "CosDprDistil":                 return new CosDprDistilEncoder();
      case "SpladePlusPlusEnsembleDistil": return new SpladePlusPlusEnsembleDistilEncoder();
      case "SpladePlusPlusSelfDistil":     return new SpladePlusPlusSelfDistilEncoder();
      case "SpladeV3":                     return new SpladeV3Encoder();
      case "UniCoil":                      return new UniCoilEncoder();
      default:
        throw new IllegalArgumentException(
            "Unknown encoder '" + name + "'. Valid names: " + allEncoderNames());
    }
  }

  /** Returns a sorted list of all supported encoder names. */
  public static List<String> allEncoderNames() {
    List<String> names = new ArrayList<>();
    names.addAll(DENSE_ENCODER_NAMES);
    names.addAll(SPARSE_ENCODER_NAMES);
    Collections.sort(names);
    return names;
  }

  public static void main(String[] argv) throws Exception {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(100));
    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    if (!DENSE_ENCODER_NAMES.contains(args.encoder) && !SPARSE_ENCODER_NAMES.contains(args.encoder)) {
      System.err.println("Unknown encoder '" + args.encoder + "'. Valid names: " + allEncoderNames());
      return;
    }

    // Use the TopicReader infrastructure.
    String readerClass = TOPIC_READER_PACKAGE + args.topicReader + "TopicReader";
    Map<String, Map<String, String>> topics =
        TopicReader.getTopicsWithStringIdsFromFileWithTopicReaderClass(readerClass, args.queriesPath);

    if (topics == null || topics.isEmpty()) {
      System.err.println("No topics loaded. Check -queries path and -topicReader class.");
      return;
    }
    LOG.info("Read {} topics from {} using {}", topics.size(), args.queriesPath, args.topicReader);

    boolean isDense = DENSE_ENCODER_NAMES.contains(args.encoder);
    // Auto-detect output format from topicReader when not explicitly set.
    String outputFormat = args.outputFormat != null ? args.outputFormat.toLowerCase()
        : (args.topicReader.startsWith("Tsv") ? "tsv" : "jsonl");
    if (!outputFormat.equals("jsonl") && !outputFormat.equals("tsv")) {
      System.err.println("Unknown -outputFormat '" + outputFormat + "'. Must be jsonl or tsv.");
      return;
    }
    LOG.info("Output format: {}", outputFormat);

    ObjectMapper mapper = new ObjectMapper();
    int encoded = 0;

    Path outputPath = Paths.get(args.outputPath);
    if (outputPath.getParent() != null) {
      Files.createDirectories(outputPath.getParent());
    }

    BufferedWriter writer = args.compress
        ? new BufferedWriter(new OutputStreamWriter(
              new GZIPOutputStream(Files.newOutputStream(outputPath)), StandardCharsets.UTF_8))
        : Files.newBufferedWriter(outputPath);

    try (OnnxEncoder<?> encoder = buildEncoder(args.encoder); writer) {

      for (Map.Entry<String, Map<String, String>> entry : topics.entrySet()) {
        String queryId   = entry.getKey();
        String queryText = entry.getValue().get(args.topicField);

        if (queryText == null) {
          LOG.warn("Topic {} has no '{}' field — skipping.", queryId, args.topicField);
          continue;
        }

        final String line;
        if (isDense) {
          float[] vector = ((DenseEncoder) encoder).encode(queryText);
          if (outputFormat.equals("tsv")) {
            // id <TAB> f1 f2 f3 ...  (space-separated floats)
            StringBuilder sb = new StringBuilder(queryId).append('\t');
            for (int i = 0; i < vector.length; i++) {
              if (i > 0) sb.append(' ');
              sb.append(vector[i]);
            }
            line = sb.toString();
          } else {
            // {"qid":"...","vector":[...]} — SearchCollection: -topicReader JsonStringVector -topicField vector
            Map<String, Object> record = new LinkedHashMap<>();
            record.put("qid", queryId);
            record.put("vector", floatArrayToList(vector));
            line = mapper.writeValueAsString(record);
          }
        } else {
          Map<String, Integer> weights = ((SparseEncoder) encoder).encode(queryText);
          String flat = SparseEncoder.flatten(weights);
          if (outputFormat.equals("tsv")) {
            // id <TAB> tok tok tok ...
            line = queryId + '\t' + flat;
          } else {
            // {"id":"...","title":"tok tok ..."} — SearchCollection: -topicReader JsonString -impact -pretokenized
            Map<String, Object> record = new LinkedHashMap<>();
            record.put("id", queryId);
            record.put("title", flat);
            line = mapper.writeValueAsString(record);
          }
        }

        writer.write(line);
        writer.newLine();

        encoded++;
        if (encoded % 500 == 0) {
          LOG.info("Encoded {}/{} queries", encoded, topics.size());
        }
      }
    }

    LOG.info("Done — encoded {} queries to {}", encoded, args.outputPath);
  }

  /**
   * Converts a primitive float array to a List&lt;Float&gt; so that Jackson
   * serialises it as a plain JSON number array rather than a base64 blob.
   */
  private static List<Float> floatArrayToList(float[] arr) {
    List<Float> list = new ArrayList<>(arr.length);
    for (float v : arr) {
      list.add(v);
    }
    return list;
  }
}
