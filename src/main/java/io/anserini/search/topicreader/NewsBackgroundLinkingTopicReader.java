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
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.index.generator.WapoGenerator;
import io.anserini.search.SearchCollection;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

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

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_RAW;

/**
 * Topic reader for TREC2018 news track background linking task
 */
public class NewsBackgroundLinkingTopicReader extends TopicReader<Integer> {
  public NewsBackgroundLinkingTopicReader(Path topicFile) {
    super(topicFile);
  }

  private static final Pattern TOP_PATTERN =
      Pattern.compile("<top(.*?)</top>", Pattern.DOTALL);
  private static final Pattern NUM_PATTERN =
      Pattern.compile("<num> Number: (\\d+) </num>", Pattern.DOTALL);
  // TREC 2011 topics uses <title> tag
  private static final Pattern DOCID_PATTERN =
      Pattern.compile("<docid>\\s*(.*?)\\s*</docid>", Pattern.DOTALL);
  // TREC 2012 topics use <query> tag
  private static final Pattern URL_PATTERN =
      Pattern.compile("<url>\\s*(.*?)\\s*</url>", Pattern.DOTALL);

  /**
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException any io exception
   */
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
      if (line.startsWith("<url>") && line.endsWith("</url>")) {
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
   * For TREC2018 News Track Background linking task, the query string is actually a document id.
   * In order to make sense of the query we extract the top terms with higher tf-idf scores from the
   * raw document of that docId from the index.
   * @param reader index reader
   * @param docid the query docid
   * @param paragraph paragraph
   * @param k how many terms will be picked from the query document
   * @param isWeighted whether to include terms' tf-idf score as their weights
   * @param qc Query Constructor
   * @param analyzer Analyzer
   * @return Strings constructed query strings
   * @throws IOException any IO exception
   */
  public static List<String> generateQueryString(IndexReader reader, String docid, boolean paragraph, int k,
     boolean isWeighted, SearchCollection.QueryConstructor qc, Analyzer analyzer) throws IOException {
    List<String> queryStrings = new ArrayList<>();
    IndexableField rawDocStr = reader.document(convertDocidToLuceneDocid(reader, docid)).getField(FIELD_RAW);
    if (rawDocStr == null) {
      throw new RuntimeException("Raw documents not stored and Unfortunately SDM query for News Background Linking " +
          "task needs to read the raw document to full construct the query string");
    }
    if (paragraph) {
      queryStrings = getParagraphs(rawDocStr.stringValue());
      queryStrings = queryStrings.subList(0, Math.min(5, queryStrings.size()));
    } else {
      queryStrings.add(getRawContents(rawDocStr.stringValue()));
    }
    for (int i = 0; i < queryStrings.size(); i++) {
      List<String> queryTokens = AnalyzerUtils.tokenize(analyzer, queryStrings.get(i));
      if (qc == SearchCollection.QueryConstructor.SequentialDependenceModel) {
        String queryString = String.join(" ", queryTokens.subList(0, Math.min(queryTokens.size(), k)));
        queryStrings.set(i, queryString);
      } else {
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
    
        PriorityQueue<Pair<String, Double>> termsTfIdfPQ = new PriorityQueue<>(new ScoreComparator());
        long docCount = reader.numDocs();
        Map<String, Integer> termsMap = new HashMap<>();
        queryTokens.forEach(token -> {
          if ((token.length() >= 2) && (token.matches("[a-z]+")))
            termsMap.merge(token, 1, Math::addExact);
          }
        );
        termsMap.forEach((term, count) -> {
          try {
            double tfIdf = count * Math.log((1.0f + docCount) / reader.docFreq(new Term(FIELD_BODY, term)));
            termsTfIdfPQ.add(Pair.of(term, tfIdf));
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
        String constructedQueryStr = "";
        for (int j = 0; j < Math.min(termsTfIdfPQ.size(), k); j++) {
          Pair<String, Double> termScores = termsTfIdfPQ.poll();
          constructedQueryStr += termScores.getKey() + (isWeighted ? String.format("^%f ", termScores.getValue()) : " ");
        }
        queryStrings.set(i, constructedQueryStr);
      }
      System.out.println(String.format("Query %d: %s", i, queryStrings.get(i)));
    }
    
    return queryStrings;
  }
  
  
  public static int convertDocidToLuceneDocid(IndexReader reader, String docid) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    
    Query q = new TermQuery(new Term(LuceneDocumentGenerator.FIELD_ID, docid));
    TopDocs rs = searcher.search(q, 1);
    ScoreDoc[] hits = rs.scoreDocs;
    
    if (hits == null) {
      throw new RuntimeException("Docid not found!");
    }
    
    return hits[0].doc;
  }
  
  private static WashingtonPostCollection.Document.WashingtonPostObject getWapoObj(String record) {
    ObjectMapper mapper = new ObjectMapper();
    WashingtonPostCollection.Document.WashingtonPostObject wapoObj = null;
    try {
      wapoObj = mapper
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unrecognized properties
          .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
          .readValue(record, WashingtonPostCollection.Document.WashingtonPostObject.class);
    } catch (IOException e) {
      // For current dataset, we can make sure all record has unique id and
      // published date. So we just simply throw an RuntimeException
      // here in case future data may bring up this issue
      throw new RuntimeException(e);
    }
    return wapoObj;
  }
  
  private static String getRawContents(String record) {
    WashingtonPostCollection.Document.WashingtonPostObject wapoObj = getWapoObj(record);
    
    StringBuilder contentBuilder = new StringBuilder();
    contentBuilder.append(wapoObj.getTitle()).append("\n\n");
    
    wapoObj.getContents().ifPresent(contents -> {
      for (WashingtonPostCollection.Document.WashingtonPostObject.Content contentObj : contents) {
        if (contentObj == null) continue;
        if (contentObj.getType().isPresent() && contentObj.getContent().isPresent()) {
          contentObj.getType().ifPresent(type -> {
            contentObj.getContent().ifPresent(content -> {
              if (WapoGenerator.CONTENT_TYPE_TAG.contains(type)) {
                contentBuilder.append(WapoGenerator.removeTags(content)).append("\n");
              }
            });
          });
        }
        contentObj.getFullCaption().ifPresent(caption -> {
          String fullCaption = contentObj.getFullCaption().get();
          contentBuilder.append(WapoGenerator.removeTags(fullCaption)).append("\n");
        });
      }
    });
    
    return contentBuilder.toString();
  }
  
  private static List<String> getParagraphs(String record) {
    List<String> paragraphs = new ArrayList<>();
    WashingtonPostCollection.Document.WashingtonPostObject wapoObj = getWapoObj(record);
    wapoObj.getContents().ifPresent(contents -> {
      for (WashingtonPostCollection.Document.WashingtonPostObject.Content contentObj : contents) {
        if (contentObj == null) continue;
        if (contentObj.getType().isPresent() && contentObj.getContent().isPresent()) {
          contentObj.getType().ifPresent(type -> {
            contentObj.getContent().ifPresent(content -> {
              if (WapoGenerator.CONTENT_TYPE_TAG.contains(type)) {
                String sanityContent = WapoGenerator.removeTags(content);
                if (sanityContent.trim().length() > 0) {
                  paragraphs.add(sanityContent);
                }
              }
            });
          });
        }
      }
    });
    paragraphs.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.length() - o1.length();
      }
    });
    return paragraphs;
  }
}
