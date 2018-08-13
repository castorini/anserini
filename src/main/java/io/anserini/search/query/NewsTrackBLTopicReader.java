/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.search.query;

import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Topic reader for TREC2018 news track background linking task
 */
public class NewsTrackBLTopicReader extends TopicReader<Integer> {
  public NewsTrackBLTopicReader(Path topicFile) {
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
  
  private static int convertDocidToLuceneDocid(IndexReader reader, String docid) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    
    Query q = new TermQuery(new Term(LuceneDocumentGenerator.FIELD_ID, docid));
    TopDocs rs = searcher.search(q, 1);
    ScoreDoc[] hits = rs.scoreDocs;
    
    if (hits == null) {
      throw new RuntimeException("Docid not found!");
    }
    
    return hits[0].doc;
  }
  
  /**
   * For TREC2018 News Track Background linking task, the query string is actually a document id.
   * In order to make sense of the query we extract the top terms with higher tf-idf scores from the
   * raw document of that docId from the index.
   * @param reader Index reader
   * @param docid the query docid
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException any io exception
   */
  public static String generateQueryString(IndexReader reader, String docid) throws IOException {
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
    Terms terms = reader.getTermVector(convertDocidToLuceneDocid(reader, docid), LuceneDocumentGenerator.FIELD_BODY);
    TermsEnum it = terms.iterator();
    while (it.next() != null) {
      String term = it.term().utf8ToString();
      if (term.length() < 2) continue;
      if (!term.matches("[a-z]+")) continue;
      long tf = it.totalTermFreq();
      double tfIdf = tf * Math.log((1.0f + docCount) / reader.docFreq(new Term(LuceneDocumentGenerator.FIELD_BODY, term)));
      termsTfIdfPQ.add(Pair.of(term, tfIdf));
    }
    
    String queryString = "";
    for (int i = 0; i < Math.min(termsTfIdfPQ.size(), 10); i++) {
      queryString += termsTfIdfPQ.poll().getKey() + " ";
    }
    return queryString;
  }
}
