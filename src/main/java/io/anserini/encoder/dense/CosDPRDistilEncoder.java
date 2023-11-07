package io.anserini.encoder.dense;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

/**
 * CosDPRDistil
 */
public class CosDPRDistilEncoder extends DenseEncoder {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/cos-dpr-distil-optimized.onnx";

  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/cos-dpr-distil-vocab.txt";

  static private final String MODEL_NAME = "cos-dpr-distil-optimized.onnx";

  static private final String VOCAB_NAME = "cos-dpr-distil-vocab.txt";

  private final BertFullTokenizer tokenizer;

  private final DefaultVocabulary vocab;

  private final OrtEnvironment environment;

  private final OrtSession session;

  public CosDPRDistilEncoder() throws IOException, OrtException {
    super();
    this.vocab = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath(VOCAB_NAME, VOCAB_URL))
        .optUnknownToken("[UNK]")
        .build();
    this.tokenizer = new BertFullTokenizer(vocab, true);
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(getModelPath(MODEL_NAME, MODEL_URL).toString(), new OrtSession.SessionOptions());
    System.out.println("Model loaded.");
  }

  @Override
  public float[] encode(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(tokenizer.tokenize(query));
    queryTokens.add("[SEP]");
    
    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(tokenizer, queryTokens, vocab);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];

    inputTokenIds[0] = queryTokenIds;
    inputs.put("input_ids", OnnxTensor.createTensor(environment, inputTokenIds));
    float[] weights = null;
    try (OrtSession.Result results = session.run(inputs)) {
      weights = ((float[][]) results.get("pooler_output").get().getValue())[0];
    } catch (OrtException e) {
      e.printStackTrace();
    }
    return weights;
  }

}