/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.collection.DocumentCollection;
import io.anserini.collection.FileSegment;
import io.anserini.collection.SourceDocument;
import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerUtils {

  static public List<String> analyze(String s) {
    return analyze(IndexCollection.DEFAULT_ANALYZER, s);
  }

  static public List<String> analyze(Analyzer analyzer, String s) {
    List<String> list = new ArrayList<>();

    try {
      TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(s));
      CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
      tokenStream.reset();
      while (tokenStream.incrementToken()) {
        if (cattr.toString().length() == 0) {
          continue;
        }
        list.add(cattr.toString());
      }
      tokenStream.end();
      tokenStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return list;
  }

  static public Map<String, Long> computeDocumentVector(Analyzer analyzer, String s) {
    Map<String, Long> termFreqMap = new HashMap<>();

    try {
      TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(s));
      CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
      tokenStream.reset();
      while (tokenStream.incrementToken()) {
        String termString = cattr.toString();
        if (termString.length() == 0) {
          continue;
        }
        termFreqMap.merge(termString, (long) 1, Long::sum);
      }
      tokenStream.end();
      tokenStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return termFreqMap;
  }

  static public Map<String, Long> computeDocumentVector(Analyzer analyzer, Class parser, String s) {
    ObjectMapper mapper = new ObjectMapper();
    String content = "";

    // TODO: analyze each collection case more carefully to catch as many case as possible
    try {
      DocumentCollection collection = (DocumentCollection) parser.getConstructor().newInstance();
      Reader inputString = new StringReader(s);
      BufferedReader bufferedReader = new BufferedReader(inputString);
      FileSegment<SourceDocument> segment = collection.createFileSegment(bufferedReader);
      for (SourceDocument d : segment) {
        content = d.contents();
        // should have only one doc.
        break;
      }
      segment.close();
//      System.out.println("computeDocumentVector: " + content);
    } catch (Exception e) {
      return computeDocumentVector(analyzer, s);
    }
    return computeDocumentVector(analyzer, content);
  }
}
