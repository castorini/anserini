package io.anserini.encoder.dense;

import io.anserini.encoder.OnnxEncoder;

import java.io.IOException;

import ai.onnxruntime.OrtException;

/**
 * DenseEncoder
 */
public abstract class DenseEncoder extends OnnxEncoder<float[]> {

  public DenseEncoder(String modelName, String modelURL, String vocabName, String vocabURL)
      throws IOException, OrtException {
    super(modelName, modelURL, vocabName, vocabURL);
  }

}