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

package io.anserini.encoder.sparse;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxJavaType;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.util.CacheDirectoryResolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Query encoder for the shared SlideOn ONNX graph. Terms are decimal latent IDs. */
public class SlideOnEncoder extends SparseEncoder {
  private static final String MODEL_URL = "https://huggingface.co/castorini/slideon-onnx/resolve/main/slideon-optimized.onnx";
  private static final String TOKENIZER_URL = "https://huggingface.co/castorini/slideon-onnx/resolve/main/tokenizer.json";
  private static final String CONFIG_URL = "https://huggingface.co/castorini/slideon-onnx/resolve/main/config.json";
  private static final String MODEL_NAME = "slideon-optimized.onnx";
  private static final String TOKENIZER_NAME = "slideon-tokenizer.json";
  private static final String MODEL_INPUT_IDS = "input_ids";
  private static final String MODEL_ATTENTION_MASK = "attention_mask";
  private static final String MODEL_IS_DOCUMENT = "is_document";
  private static final String MODEL_OUTPUT_IDX = "output_idx";
  private static final String MODEL_OUTPUT_WEIGHTS = "output_weights";

  private final HuggingFaceTokenizer hfTokenizer;
  private final String prefix;
  private final int maxLength;
  private final int dimensions;
  private final float scale;

  /** Uses the same model/config and tokenizer-asset download flow as SPLADE. */
  public SlideOnEncoder() throws IOException, OrtException, URISyntaxException {
    super(1, 100, MODEL_NAME, MODEL_URL, TOKENIZER_NAME, TOKENIZER_URL, CONFIG_URL, false);
    try {
      Path configPath = CacheDirectoryResolver.getEncodersCachePath()
          .resolve(getClass().getSimpleName() + "-config.json");
      JsonNode config = new ObjectMapper().readTree(configPath.toFile());
      if (!"slideon".equals(config.path("model_type").asText())) {
        throw new IOException("Expected a SlideOn HF config.json");
      }
      if (!config.path("tokenizer_settings").path("query_prefix").asText("").isEmpty()) {
        throw new IOException("SlideOn query_prefix token insertion is not supported");
      }
      JsonNode inference = config.required("inference");
      this.prefix = inference.required("prompts").required("query").asText();
      int modelMaxLength = inference.required("query_length").asInt();
      this.maxLength = Integer.parseInt(System.getProperty("anserini.slideon.maxLength",
          Integer.toString(modelMaxLength)));
      this.dimensions = config.required("head_config").required("n_latents").asInt();
      this.scale = Float.parseFloat(System.getProperty("anserini.slideon.scale", "100"));
      if (maxLength < 2 || maxLength > modelMaxLength || dimensions <= 0
          || !Float.isFinite(scale) || scale <= 0) {
        throw new IOException("Invalid SlideOn maximum length, latent dimension, or scale");
      }

      // Reject old query-only bundles or incompatible graphs before processing queries.
      Map<String, NodeInfo> inputs = session.getInputInfo();
      Map<String, NodeInfo> outputs = session.getOutputInfo();
      if (!inputs.keySet().equals(Set.of(MODEL_INPUT_IDS, MODEL_ATTENTION_MASK, MODEL_IS_DOCUMENT))
          || !outputs.keySet().equals(Set.of(MODEL_OUTPUT_IDX, MODEL_OUTPUT_WEIGHTS))) {
        throw new IOException("Expected shared SlideOn ONNX inputs input_ids, attention_mask, is_document "
            + "and outputs output_idx, output_weights");
      }
      checkTensor(inputs.get(MODEL_INPUT_IDS), OnnxJavaType.INT64, 2);
      checkTensor(inputs.get(MODEL_ATTENTION_MASK), OnnxJavaType.INT64, 2);
      checkTensor(inputs.get(MODEL_IS_DOCUMENT), OnnxJavaType.BOOL, 0);
      checkTensor(outputs.get(MODEL_OUTPUT_IDX), OnnxJavaType.INT64, 1);
      checkTensor(outputs.get(MODEL_OUTPUT_WEIGHTS), OnnxJavaType.FLOAT, 1);
      String exportedDimensions = session.getMetadata().getCustomMetadata().get("n_latents");
      if (exportedDimensions != null && Integer.parseInt(exportedDimensions) != dimensions) {
        throw new IOException("ONNX and HF config disagree on n_latents");
      }

      this.hfTokenizer = HuggingFaceTokenizer.newInstance(getVocabPath(), Map.of(
          "addSpecialTokens", "true", "padding", "false", "truncation", "true",
          "maxLength", Integer.toString(maxLength), "modelMaxLength", Integer.toString(maxLength)));
    } catch (IOException | OrtException | URISyntaxException | RuntimeException e) {
      super.close();
      throw e;
    }
  }

  private static void checkTensor(NodeInfo node, OnnxJavaType type, int rank) throws IOException {
    if (!(node.getInfo() instanceof TensorInfo tensor) || tensor.type != type || tensor.getShape().length != rank) {
      throw new IOException("Unexpected SlideOn tensor type/shape for " + node.getName());
    }
  }

  private Encoding tokenize(String query) {
    // Python str.strip also removes NBSP and NEL, unlike Java String.strip.
    String text = prefix + java.util.Objects.requireNonNull(query, "query");
    return hfTokenizer.encode(text.replaceAll(
        "^[\\p{javaWhitespace}\\p{Z}\\u0085]+|[\\p{javaWhitespace}\\p{Z}\\u0085]+$", ""));
  }

  @Override
  public long[] tokenizeToIds(String query) {
    return tokenize(query).getIds();
  }

  @Override
  protected Map<String, Float> computeFloatWeights(String query) throws OrtException {
    Encoding encoding = tokenize(query);
    if (encoding.getIds().length > maxLength) {
      throw new IllegalStateException("Tokenizer did not apply max_length truncation");
    }
    try (OnnxTensor ids = OnnxTensor.createTensor(environment, new long[][] {encoding.getIds()});
         OnnxTensor mask = OnnxTensor.createTensor(environment, new long[][] {encoding.getAttentionMask()});
         OnnxTensor isDocument = OnnxTensor.createTensor(environment, Boolean.FALSE);
         OrtSession.Result result = session.run(Map.of(
             MODEL_INPUT_IDS, ids, MODEL_ATTENTION_MASK, mask, MODEL_IS_DOCUMENT, isDocument))) {
      long[] indices = (long[]) result.get(MODEL_OUTPUT_IDX).orElseThrow().getValue();
      float[] weights = (float[]) result.get(MODEL_OUTPUT_WEIGHTS).orElseThrow().getValue();
      if (indices.length != weights.length) {
        throw new IllegalStateException("Mismatched SlideOn output lengths");
      }
      Map<String, Float> vector = new LinkedHashMap<>();
      for (int i = 0; i < indices.length; i++) {
        if (indices[i] < 0 || indices[i] >= dimensions || !Float.isFinite(weights[i]) || weights[i] < 0) {
          throw new IllegalStateException("Invalid SlideOn latent ID or weight");
        }
        // Unlike SPLADE vocab IDs, latent IDs 0, 101, and 102 are ordinary features.
        if (vector.put(Long.toString(indices[i]), weights[i]) != null) {
          throw new IllegalStateException("Duplicate SlideOn latent ID");
        }
      }
      return vector;
    }
  }

  /** Existing impact-index convention, not a workaround for float-vector parity failures. */
  public static int quantizeWeight(float weight, float scale) {
    if (!Float.isFinite(weight) || weight < 0 || !Float.isFinite(scale) || scale <= 0) {
      throw new IllegalArgumentException("Weights must be nonnegative and scale positive, both finite");
    }
    // Float32 multiplication followed by ties-to-even rounding, like numpy rint.
    double rounded = Math.rint(weight * scale);
    if (rounded > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Quantized weight exceeds Java int range");
    }
    return (int) rounded;
  }

  @Override
  public Map<String, Integer> quantizeFloatWeights(Map<String, Float> weights) {
    Map<String, Integer> result = new LinkedHashMap<>();
    weights.forEach((term, weight) -> {
      int value = quantizeWeight(weight, scale);
      if (value > 0) {
        result.put(term, value);
      }
    });
    return result;
  }

  @Override
  public void close() {
    try {
      hfTokenizer.close();
    } finally {
      super.close();
    }
  }
}
