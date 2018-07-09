package io.anserini.ltr.feature;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.anserini.rerank.RerankerContext;

import java.io.FileReader;
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
    JsonParser extractorJson = new JsonFactory().createParser(new FileReader(filePath));
    return FeatureExtractors.fromJson(extractorJson);
  }

  public static FeatureExtractors fromJson(JsonParser jsonParser) throws Exception {
    FeatureExtractors extractors = new FeatureExtractors();

    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(UnorderedSequentialPairsFeatureExtractor.class,
            new UnorderedSequentialPairsFeatureExtractor.Deserializer());
    module.addDeserializer(OrderedSequentialPairsFeatureExtractor.class,
            new OrderedSequentialPairsFeatureExtractor.Deserializer());
    module.addDeserializer(OrderedQueryPairsFeatureExtractor.class,
            new OrderedQueryPairsFeatureExtractor.Deserializer());
    module.addDeserializer(UnorderedQueryPairsFeatureExtractor.class,
            new UnorderedQueryPairsFeatureExtractor.Deserializer());
    objectMapper.registerModule(module);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    jsonParser.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    JsonNode node = objectMapper.readTree(jsonParser);
    for (JsonNode extractor : node.get(JSON_KEY)) {
      String extractorName = extractor.get(NAME_KEY).asText();
      if (!FeatureExtractor.EXTRACTOR_MAP.containsKey(extractorName)) {
        LOG.warn(String.format("Unknown extractor %s encountered, skipping", extractorName));
        continue;
      }

      if (extractor.has(CONFIG_KEY)) {
        JsonNode config = extractor.get(CONFIG_KEY);
        JsonParser configJsonParser = objectMapper.treeAsTokens(config);
        FeatureExtractor parsedExtractor = (FeatureExtractor) objectMapper
          .readValue(configJsonParser, FeatureExtractor.EXTRACTOR_MAP.get(extractorName));
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
