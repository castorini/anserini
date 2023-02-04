package io.anserini.analysis;

import org.apache.lucene.analysis.Analyzer;
import java.util.HashMap;
import java.util.Map;

public class AnalyzerMap {
    public static final Map<String, String> analyzerMap = new HashMap<String, String>() {
      {
        analyzerMap.put("ar", "org.apache.lucene.analysis.ar.ArabicAnalyzer");
        analyzerMap.put("bn", "org.apache.lucene.analysis.bn.BengaliAnalyzer");
        analyzerMap.put("da", "org.apache.lucene.analysis.da.DanishAnalyzer");
        analyzerMap.put("es", "org.apache.lucene.analysis.es.SpanishAnalyzer");
        analyzerMap.put("fa", "org.apache.lucene.analysis.fa.PersianAnalyzer");
        analyzerMap.put("fi", "org.apache.lucene.analysis.fi.FinnishAnalyzer");
        analyzerMap.put("fr", "org.apache.lucene.analysis.fr.FrenchAnalyzer");
        analyzerMap.put("de", "org.apache.lucene.analysis.de.GermanAnalyzer");
        analyzerMap.put("hi", "org.apache.lucene.analysis.hi.HindiAnalyzer");
        analyzerMap.put("hu", "org.apache.lucene.analysis.hu.HungarianAnalyzer");
        analyzerMap.put("id", "org.apache.lucene.analysis.id.IndonesianAnalyzer");
        analyzerMap.put("it", "org.apache.lucene.analysis.it.ItalianAnalyzer");
        analyzerMap.put("ja", "org.apache.lucene.analysis.ja.JapaneseAnalyzer");
        analyzerMap.put("ko", "org.apache.lucene.analysis.cjk.CJKAnalyzer");
        analyzerMap.put("nl", "org.apache.lucene.analysis.nl.DutchAnalyzer");
        analyzerMap.put("no", "org.apache.lucene.analysis.no.NorwegianAnalyzer");
        analyzerMap.put("pl", "org.apache.lucene.analysis.morfologik.MorfologikAnalyzer");
        analyzerMap.put("pt", "org.apache.lucene.analysis.pt.PortugueseAnalyzer");
        analyzerMap.put("ru", "org.apache.lucene.analysis.ru.RussianAnalyzer");
        analyzerMap.put("sv", "org.apache.lucene.analysis.sv.SwedishAnalyzer");
        analyzerMap.put("te", "org.apache.lucene.analysis.te.TeluguAnalyzer");
        analyzerMap.put("th", "org.apache.lucene.analysis.th.ThaiAnalyzer");
        analyzerMap.put("tr", "org.apache.lucene.analysis.tr.TurkishAnalyzer");
        analyzerMap.put("uk", "org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer");
        analyzerMap.put("zh", "org.apache.lucene.analysis.cjk.CJKAnalyzer");
      }
    };

    public static Analyzer getLangSpecificAnalyzer(String language) throws Exception {
      String analyzerClazz = analyzerMap.get(language); 
    return (Analyzer) Class.forName(analyzerClazz).getDeclaredConstructor().newInstance();
    }
}
