/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.search.topicreader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.anserini.analysis.AnalyzerUtils;
import io.anserini.collection.WashingtonPostCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexReaderUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Topic reader for the TREC News Track background linking task.
 */
public class BackgroundLinkingTopicReader extends TopicReader<Integer> {
  public BackgroundLinkingTopicReader(Path topicFile) {
    super(topicFile);
  }

  private static final Pattern TOP_PATTERN =
      Pattern.compile("<top(.*?)</top>", Pattern.DOTALL);
  private static final Pattern NUM_PATTERN =
      Pattern.compile("<num> Number: (\\d+) </num>", Pattern.DOTALL);
  private static final Pattern DOCID_PATTERN =
      Pattern.compile("<docid>\\s*(.*?)\\s*</docid>", Pattern.DOTALL);
  private static final Pattern URL_PATTERN =
      Pattern.compile("<url>\\s*(.*?)\\s*</?url>", Pattern.DOTALL);
  // Note that some TREC 2018 topics don't properly close the </url> tags.

  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();
    Map<String,String> fields = new HashMap<>();

    String number = "";
    Matcher m;
    String line;
    while ((line = bRdr.readLine()) != null) {
      line = line.trim();
      if (line.startsWith("<num>") && line.endsWith("</num>")) {
        m = NUM_PATTERN.matcher(line);
        if (!m.find()) {
          throw new IOException("Error parsing " + line);
        }
        number = m.group(1);
      }
      if (line.startsWith("<docid>") && line.endsWith("</docid>")) {
        m = DOCID_PATTERN.matcher(line);
        if (!m.find()) {
          throw new IOException("Error parsing " + line);
        }
        fields.put("title", m.group(1));
      }
      if (line.startsWith("<url>") && (line.endsWith("</url>") || line.endsWith("<url>"))) {
        // Note that some TREC 2018 topics don't properly close the </url> tags.
        m = URL_PATTERN.matcher(line);
        if (!m.find()) {
          throw new IOException("Error parsing " + line);
        }
        fields.put("url", m.group(1));
      }
      if (line.startsWith("</top>")) {
        map.put(Integer.valueOf(number), fields);
        fields = new HashMap<>();
      }
    }

    return map;
  }
  
  /**
   * Extracts the top <i>k</i> terms from document in terms of tf-idf.
   */
  public static List<String> extractTerms(IndexReader reader, String docid, int k, Analyzer analyzer)
      throws IOException {
    // Fetch the raw JSON representation of the document.
    IndexableField rawField = reader.document(
        IndexReaderUtils.convertDocidToLuceneDocid(reader, docid)).getField(IndexArgs.RAW);
    if (rawField == null) {
      throw new RuntimeException("Raw documents not stored!");
    }

    // Extract plain-text representation and analyze into terms.
    List<String> documentTerms = AnalyzerUtils.analyze(analyzer, extractArticlePlainText(rawField.stringValue()));

    // Priority queue for holding terms based on tf-idf scores.
    class ScoreComparator implements Comparator<Pair<String, Double>> {
      public int compare(Pair<String, Double> a, Pair<String, Double> b) {
        int cmp = Double.compare(b.getRight(), a.getRight());
        if (cmp == 0) {
          return a.getLeft().compareToIgnoreCase(b.getLeft());
        } else {
          return cmp;
        }
      }
    }
    PriorityQueue<Pair<String, Double>> termsQueue = new PriorityQueue<>(new ScoreComparator());

    // Build histogram of tf.
    Map<String, Integer> tfMap = new HashMap<>();
    documentTerms.forEach(token -> {
      if ((token.length() >= 2) && (token.matches("[a-z]+")))
        tfMap.merge(token, 1, Math::addExact);
      }
    );

    // Compute the tf-idf.
    final long docCount = reader.numDocs();
    tfMap.forEach((term, tf) -> termsQueue.add(Pair.of(term,
        tf * Math.log((1.0f + docCount) / IndexReaderUtils.getDF(reader, term)))));

    // Extract the top k terms.
    List<String> extractedTerms = new ArrayList<>();
    for (int j = 0; j < Math.min(termsQueue.size(), k); j++) {
      extractedTerms.add(termsQueue.poll().getKey());
    }

    return extractedTerms;
  }

  // Note that there's code duplication here with the WashingtonPostCollection. We can't just call a method there
  // because the version of the code inside WashingtonPostCollection.Document modifies internal state (e.g., "kicker"
  // and "caption"). Haven't thought of a good solution for this yet.
  private static String extractArticlePlainText(String record) {
    WashingtonPostCollection.Document.WashingtonPostObject wapoObj;
    ObjectMapper mapper = new ObjectMapper();
    try {
      wapoObj = mapper
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unrecognized properties
          .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
          .readValue(record, WashingtonPostCollection.Document.WashingtonPostObject.class);
    } catch (IOException e) {
      // Something is wrong... abort!
      throw new RuntimeException(e);
    }

    StringBuilder contentBuilder = new StringBuilder();
    contentBuilder.append(wapoObj.getTitle()).append("\n");
    
    wapoObj.getContents().ifPresent(contents -> {
      for (WashingtonPostCollection.Document.WashingtonPostObject.Content contentObj : contents) {
        if (contentObj == null) continue;
        if (contentObj.getType().isPresent() && contentObj.getContent().isPresent()) {
          contentObj.getType().ifPresent(type -> {
            contentObj.getContent().ifPresent(content -> {
              if (WashingtonPostCollection.Document.CONTENT_TYPE_TAG.contains(type)) {
                contentBuilder.append(Jsoup.parse(content).text()).append("\n");
              }
            });
          });
        }
        contentObj.getFullCaption().ifPresent(caption -> {
          String fullCaption = contentObj.getFullCaption().get();
          contentBuilder.append(Jsoup.parse(fullCaption).text()).append("\n");
        });
      }
    });
    
    return contentBuilder.toString();
  }
}
