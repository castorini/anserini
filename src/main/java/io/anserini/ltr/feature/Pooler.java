package io.anserini.ltr.feature;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.util.List;

public interface Pooler {
    List<Float> iter(Document doc, Terms terms, String queryText, List<String> queryTokens, IndexReader reader);
}
