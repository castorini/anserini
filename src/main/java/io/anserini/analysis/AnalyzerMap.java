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

import org.apache.lucene.analysis.Analyzer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerMap {
  public static final Map<String, String> analyzerMap = new HashMap<>() {
    {
      put("ar", "org.apache.lucene.analysis.ar.ArabicAnalyzer");
      put("bn", "org.apache.lucene.analysis.bn.BengaliAnalyzer");
      put("da", "org.apache.lucene.analysis.da.DanishAnalyzer");
      put("es", "org.apache.lucene.analysis.es.SpanishAnalyzer");
      put("fa", "org.apache.lucene.analysis.fa.PersianAnalyzer");
      put("fi", "org.apache.lucene.analysis.fi.FinnishAnalyzer");
      put("fr", "org.apache.lucene.analysis.fr.FrenchAnalyzer");
      put("de", "org.apache.lucene.analysis.de.GermanAnalyzer");
      put("hi", "org.apache.lucene.analysis.hi.HindiAnalyzer");
      put("hu", "org.apache.lucene.analysis.hu.HungarianAnalyzer");
      put("id", "org.apache.lucene.analysis.id.IndonesianAnalyzer");
      put("it", "org.apache.lucene.analysis.it.ItalianAnalyzer");
      put("ja", "org.apache.lucene.analysis.ja.JapaneseAnalyzer");
      put("ko", "org.apache.lucene.analysis.cjk.CJKAnalyzer");
      put("nl", "org.apache.lucene.analysis.nl.DutchAnalyzer");
      put("no", "org.apache.lucene.analysis.no.NorwegianAnalyzer");
      put("pl", "org.apache.lucene.analysis.morfologik.MorfologikAnalyzer");
      put("pt", "org.apache.lucene.analysis.pt.PortugueseAnalyzer");
      put("ru", "org.apache.lucene.analysis.ru.RussianAnalyzer");
      put("sv", "org.apache.lucene.analysis.sv.SwedishAnalyzer");
      put("te", "org.apache.lucene.analysis.te.TeluguAnalyzer");
      put("th", "org.apache.lucene.analysis.th.ThaiAnalyzer");
      put("tr", "org.apache.lucene.analysis.tr.TurkishAnalyzer");
      put("uk", "org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer");
      put("zh", "org.apache.lucene.analysis.cjk.CJKAnalyzer");
    }
  };

  public static Analyzer getLanguageSpecificAnalyzer(String language) {
    String analyzerClazz = analyzerMap.get(language);

    try {
      return (Analyzer) Class.forName(analyzerClazz).getDeclaredConstructor().newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    // If we have any issues, eat the exception and return null.
    return null;
  }
}
