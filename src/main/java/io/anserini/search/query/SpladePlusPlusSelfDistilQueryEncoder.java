package io.anserini.search.query;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.io.*;
import java.util.*;

public class SpladePlusPlusSelfDistilQueryEncoder extends QueryEncoder {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/splade-pp-sd-optimized.onnx";

  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/wordpiece-vocab.txt";

  static private final String MODEL_NAME = "splade-pp-sd-optimized.onnx";

  static private final String VOCAB_NAME = "splade-pp-sd-vocab.txt";

  private final BertFullTokenizer tokenizer;

  private final DefaultVocabulary vocab;

  private final OrtEnvironment environment;

  private final OrtSession session;
  public SpladePlusPlusSelfDistilQueryEncoder() throws IOException, OrtException {
    super(5, 256);
    this.vocab = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath(VOCAB_NAME, VOCAB_URL))
        .optUnknownToken("[UNK]")
        .build();
    this.tokenizer = new BertFullTokenizer(vocab, true);
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(getModelPath(MODEL_NAME, MODEL_URL).toString(),
        new OrtSession.SessionOptions());
    System.out.println("Model loaded.");
  }

  @Override
  public String encode(String query) throws OrtException {
    Map<String, Float> tokenWeightMap = getTokenWeightMap(query);
    return generateEncodedQuery(tokenWeightMap);
  }

  @Override
  protected Map<String, Float> getTokenWeightMap(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(tokenizer.tokenize(query));
    queryTokens.add("[SEP]");

    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(tokenizer, queryTokens, vocab);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];

    inputTokenIds[0] = queryTokenIds;
    long[][] attentionMask = new long[1][queryTokenIds.length];
    long[][] tokenTypeIds = new long[1][queryTokenIds.length];

    // initialize attention mask with all 1s
    Arrays.fill(attentionMask[0], 1);
    inputs.put("input_ids", OnnxTensor.createTensor(environment, inputTokenIds));
    inputs.put("token_type_ids", OnnxTensor.createTensor(environment, tokenTypeIds));
    inputs.put("attention_mask", OnnxTensor.createTensor(environment, attentionMask));
    Map<String, Float> tokenWeightMap = null;
    try (OrtSession.Result results = session.run(inputs)) {
      long[] indexes = (long[]) results.get("output_idx").get().getValue();
      float[] weights = (float[]) results.get("output_weights").get().getValue();
      tokenWeightMap = getTokenWeightMap(indexes, weights, vocab);
    } catch (OrtException e) {
      e.printStackTrace();
    }
    return tokenWeightMap;
  }

}