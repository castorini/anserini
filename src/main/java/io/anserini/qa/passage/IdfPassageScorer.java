package io.anserini.qa.passage;

import edu.stanford.nlp.simple.Sentence;
import io.anserini.index.IndexUtils;
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TermMatchIdfPassageScorer implements PassageScorer{
  public Map<String, Double> sentenceScore;

  @Override
  public void score(List<String> sentences, String index, String output, Context context) throws Exception {
    IndexUtils util = new IndexUtils(index);
    FSDirectory directory = FSDirectory.open(new File(index).toPath());
    DirectoryReader reader = DirectoryReader.open(directory);

    EnglishAnalyzer ea = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
    QueryParser qp = new QueryParser(LuceneDocumentGenerator.FIELD_BODY, ea);

    Map<String, Double> sentenceIDF = new HashMap();
    ClassicSimilarity similarity = new ClassicSimilarity();

    for (String sent: sentences) {
      double idf = 0.0;
      String[] terms = sent.split(" ");
      for (String term: terms) {
        try {
          TermQuery q = (TermQuery) qp.parse(term);
          Term t = q.getTerm();
          idf += similarity.idf(reader.docFreq(t), reader.numDocs());
        } catch (Exception e){
          continue;
        }
      }
      sentenceScore.put(sent, idf/sent.length());
      context.setState(sentenceScore);
    }
  }
}
