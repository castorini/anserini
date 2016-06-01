package io.anserini.ltr.feature;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import io.anserini.rerank.RerankerContext;

import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Terms;

import com.google.common.collect.Lists;

/**
 * A collection of {@link FeatureExtractor}s.
 */
public class FeatureExtractors {
  private static final Logger LOG = LogManager.getLogger(FeatureExtractors.class);

  //********************************************************
  //********************************************************
  private static final String JSON_KEY = "extractors";
  private static final String NAME_KEY = "name";
  private static final String CONFIG_KEY = "params";

  public static FeatureExtractors loadExtractor(String filePath) throws Exception {
      JsonObject extractorJson  = new JsonParser().parse(new FileReader(filePath)).getAsJsonObject();
      return FeatureExtractors.fromJson(extractorJson);
  }

  public static FeatureExtractors fromJson(JsonObject json) throws Exception {
    FeatureExtractors extractors = new FeatureExtractors();

    Gson gson = FeatureExtractor.BUILDER.create();
    for (JsonElement extractor : json.getAsJsonArray(JSON_KEY)) {
      JsonObject extractorJson = extractor.getAsJsonObject();
      String extractorName = extractorJson.get(NAME_KEY).getAsString();
      if (!FeatureExtractor.EXTRACTOR_MAP.containsKey(extractorName)) {
        LOG.warn(String.format("Unknown extractor %s encountered, skipping", extractorName));
        continue;
      }

      if (extractorJson.has(CONFIG_KEY)) {
        JsonObject config = extractorJson.get(CONFIG_KEY).getAsJsonObject();
        FeatureExtractor parsedExtractor = (FeatureExtractor) gson.fromJson(config,
                FeatureExtractor.EXTRACTOR_MAP.get(extractorName));
        extractors.add(parsedExtractor);
      } else {
        FeatureExtractor parsedExtractor = (FeatureExtractor) FeatureExtractor.EXTRACTOR_MAP.get(extractorName)
                .getConstructor().newInstance();
        extractors.add(parsedExtractor);
      }
    }
    return extractors;
  }

  public static FeatureExtractors createFeatureExtractorChain(FeatureExtractor... extractors) {
    FeatureExtractors chain = new FeatureExtractors();
    for (FeatureExtractor extractor : extractors) {
      chain.add(extractor);
    }

    return chain;
  }

  //********************************************************

  public List<FeatureExtractor> extractors = Lists.newArrayList();

  public FeatureExtractors() {}

  public FeatureExtractors add(FeatureExtractor extractor) {
    extractors.add(extractor);
    return this;
  }

  public float[] extractAll(Document doc, Terms terms, RerankerContext context) {
    float[] features = new float[extractors.size()];

    for (int i=0; i<extractors.size(); i++) {
      features[i] = extractors.get(i).extract(doc, terms, context);
    }

    return features;
  }
}
