package io.anserini.analysis;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class EnglishStemmingAnalyzer extends StopwordAnalyzerBase {
  private final String stemmer;
  private final CharArraySet stemExclusionSet;
  
  public EnglishStemmingAnalyzer() {
    this("", StandardAnalyzer.STOP_WORDS_SET);
  }
  
  public EnglishStemmingAnalyzer(String stemmer) {
    this(stemmer, StandardAnalyzer.STOP_WORDS_SET, CharArraySet.EMPTY_SET);
  }
  
  public EnglishStemmingAnalyzer(CharArraySet stopwords) {
    this("", stopwords, CharArraySet.EMPTY_SET);
  }
  
  public EnglishStemmingAnalyzer(String stemmer, CharArraySet stopwords) {
    this(stemmer, stopwords, CharArraySet.EMPTY_SET);
  }
  
  public EnglishStemmingAnalyzer(String stemmer, CharArraySet stopwords, CharArraySet stemExclusionSet) {
    super(stopwords);
    this.stemmer = stemmer;
    this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
  }
  
  protected TokenStreamComponents createComponents(String fieldName) {
    Tokenizer source = new StandardTokenizer();
    TokenStream result = null;
    result = new StandardFilter(source);
    result = new EnglishPossessiveFilter(result);
    result = new LowerCaseFilter(result);
    result = new StopFilter(result, this.stopwords);
    if (!this.stemExclusionSet.isEmpty()) {
      result = new SetKeywordMarkerFilter((TokenStream)result, this.stemExclusionSet);
    }
    
    if (this.stemmer.compareToIgnoreCase("porter") == 0 || this.stemmer.compareToIgnoreCase("p") == 0) {
      result = new PorterStemFilter((TokenStream)result);
    } else if (this.stemmer.compareToIgnoreCase("krovetz") == 0 || this.stemmer.compareToIgnoreCase("k") == 0) {
      result = new KStemFilter((TokenStream)result);
    }
    
    return new TokenStreamComponents(source, result);
  }
  
  protected TokenStream normalize(String fieldName, TokenStream in) {
    TokenStream result = new StandardFilter(in);
    result = new LowerCaseFilter(result);
    return result;
  }
}
