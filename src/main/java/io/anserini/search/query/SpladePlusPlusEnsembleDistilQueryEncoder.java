package io.anserini.search.query;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class SpladePlusPlusEnsembleDistilQueryEncoder extends QueryEncoder {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/splade-pp-ed-optimized.onnx";

  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/wordpiece-vocab.txt";

  static private final String MODEL_NAME = "splade-pp-ed-optimized.onnx";

  static private final String VOCAB_NAME = "splade-pp-ed-vocab.txt";

  private final BertFullTokenizer tokenizer;

  private final DefaultVocabulary vocab;

  private final OrtEnvironment environment;

  private final OrtSession session;

  static private Path getModelPath() throws IOException {
    File modelFile = new File(getCacheDir(), MODEL_NAME);
    FileUtils.copyURLToFile(new URL(MODEL_URL), modelFile);
    return modelFile.toPath();
  }

  static private Path getVocabPath() throws IOException {
    File vocabFile = new File(getCacheDir(), VOCAB_NAME);
    FileUtils.copyURLToFile(new URL(VOCAB_URL), vocabFile);
    return vocabFile.toPath();
  }

  public SpladePlusPlusEnsembleDistilQueryEncoder() throws IOException, OrtException {
    super(5, 256);

    vocab = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath())
        .optUnknownToken("[UNK]")
        .build();
    this.tokenizer = new BertFullTokenizer(vocab, true);
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(getModelPath().toString(), new OrtSession.SessionOptions());
    System.out.println("Model loaded.");
  }

  @Override
  public String encode(String query) throws OrtException {
    String encodedQuery = "";
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(tokenizer.tokenize(query));
    queryTokens.add("[SEP]");

    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(tokenizer, queryTokens);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];

    inputTokenIds[0] = queryTokenIds;
    long[][] attentionMask = new long[1][queryTokenIds.length];
    long[][] tokenTypeIds = new long[1][queryTokenIds.length];

    // initialize attention mask with all 1s
    Arrays.fill(attentionMask[0], 1);
    inputs.put("input_ids", OnnxTensor.createTensor(environment, inputTokenIds));
    inputs.put("token_type_ids", OnnxTensor.createTensor(environment, tokenTypeIds));
    inputs.put("attention_mask", OnnxTensor.createTensor(environment, attentionMask));

    try (OrtSession.Result results = session.run(inputs)) {
      long[] indexes = (long[]) results.get("output_idx").get().getValue();
      float[] weights = (float[]) results.get("output_weights").get().getValue();
      Map<String, Float> tokenWeightMap = getTokenWeightMap(indexes, weights);
      encodedQuery = generateEncodedQuery(tokenWeightMap);
    }
    return encodedQuery;
  }

  private long[] convertTokensToIds(BertFullTokenizer tokenizer, List<String> tokens) {
    int numTokens = tokens.size();
    long[] tokenIds = new long[numTokens];
    for (int i = 0; i < numTokens; ++i) {
      tokenIds[i] = vocab.getIndex(tokens.get(i));
    }
    return tokenIds;
  }

  @Override
  Map<String, Float> getTokenWeightMap(long[] indexes, float[] computedWeights) {
    /*
     * This function returns a map of token to its weight.
     */
    Map<String, Float> tokenWeightMap = new LinkedHashMap<>();

    for (int i = 0; i < indexes.length; i++) {
      if (indexes[i] == 101 || indexes[i] == 102 || indexes[i] == 0) {
        continue;
      }
      tokenWeightMap.put(vocab.getToken(indexes[i]), computedWeights[i]);
    }
    return tokenWeightMap;
  }

  private String generateEncodedQuery(Map<String, Float> tokenWeightMap) {
    /*
     * This function generates the encoded query.
     */
    List<String> encodedQuery = new ArrayList<>();
    for (Map.Entry<String, Float> entry : tokenWeightMap.entrySet()) {
      String token = entry.getKey();
      Float tokenWeight = entry.getValue();
      int weightQuanted = Math.round(tokenWeight / weightRange * quantRange);
      for (int i = 0; i < weightQuanted; ++i) {
        encodedQuery.add(token);
      }
    }
    return String.join(" ", encodedQuery);
  }

  @Override
  Map<String, Float> getTokenWeightMap(List<String> tokens, float[] computedWeights) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getTokenWeightMap'");
  }
}