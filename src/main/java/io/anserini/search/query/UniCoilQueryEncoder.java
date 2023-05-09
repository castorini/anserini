package io.anserini.search.query;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.io.*;
import java.util.*;

public class UniCoilQueryEncoder extends QueryEncoder {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/unicoil.onnx";

  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/wordpiece-vocab.txt";

  static private final String MODEL_NAME = "unicoil.onnx";

  static private final String VOCAB_NAME = "unicoil-vocab.txt";

  private final BertFullTokenizer tokenizer;

  private final DefaultVocabulary vocab;

  private final OrtEnvironment environment;

  private final OrtSession session;

  public UniCoilQueryEncoder() throws IOException, OrtException {
    super(5, 256);
    this.vocab = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath(VOCAB_NAME, VOCAB_URL))
        .optUnknownToken("[UNK]")
        .build();
    this.tokenizer = new BertFullTokenizer(vocab, true);
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(getModelPath(MODEL_NAME, MODEL_URL).toString(), new OrtSession.SessionOptions());
  }

  @Override
  public String encode(String query) throws OrtException {
    String encodedQuery = "";
    Map<String, Float> tokenWeightMap = getTokenWeightMap(query);
    encodedQuery = generateEncodedQuery(tokenWeightMap);
    return encodedQuery;
  }


  private float[] flatten(Object obj) {
    List<Float> weightsList = new ArrayList<>();
    Object[] inputs = (Object[]) obj;
    for (Object input : inputs) {
      float[][] weights = (float[][]) input;
      for (float[] weight : weights) {
        weightsList.add(weight[0]);
      }
    }
    return toArray(weightsList);
  }

  private float[] toArray(List<Float> input) {
    float[] output = new float[input.size()];
    for (int i = 0; i < output.length; i++) {
      output[i] = input.get(i);
    }
    return output;
  }

  private Map<String, Float> getTokenWeightMap(List<String> tokens, float[] computedWeights) {
    Map<String, Float> tokenWeightMap = new LinkedHashMap<>();
    for (int i = 0; i < tokens.size(); ++i) {
      String token = tokens.get(i);
      float tokenWeight = computedWeights[i];
      if (token.equals("[CLS]")) {
        continue;
      } else if (token.equals("[PAD]")) {
        break;
      } else if (tokenWeightMap.containsKey(token)) {
        Float accumulatedWeight = tokenWeightMap.get(token);
        tokenWeightMap.put(token, accumulatedWeight + tokenWeight);
      } else {
        tokenWeightMap.put(token, tokenWeight);
      }
    }
    return tokenWeightMap;
  }

  @Override
  public Map<String, Float> getTokenWeightMap(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(tokenizer.tokenize(query));
    queryTokens.add("[SEP]");

    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(tokenizer, queryTokens, vocab);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];
    inputTokenIds[0] = queryTokenIds;
    inputs.put("inputIds", OnnxTensor.createTensor(environment, inputTokenIds));

    Map<String, Float> tokenWeightMap = null;
    try (OrtSession.Result results = session.run(inputs)) {
      float[] computedWeights = flatten(results.get(0).getValue());
      tokenWeightMap = getTokenWeightMap(queryTokens, computedWeights);
    } catch (OrtException e) {
      e.printStackTrace();
    }
    return tokenWeightMap;
  }

}