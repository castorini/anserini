package io.anserini.ltr.feature.base;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContentContext {
    private IndexReader reader;
    private String fieldName;
    public long totalTermFreq;
    public long nonEmptyDocs;
    public double avgFL;

    public long docSize;
    public long termCount;
    public Terms termVector;

    private Map<String,Integer> docFreqs = new HashMap<>();
    private Map<String,Long> collectFreqs = new HashMap<>();
    
    ContentContext(IndexReader reader, String fieldName) throws IOException {
        this.reader = reader;
        this.fieldName = fieldName;
        nonEmptyDocs = reader.getDocCount(fieldName);
        totalTermFreq = reader.getSumTotalTermFreq(fieldName);
        avgFL = (double)totalTermFreq/nonEmptyDocs;
    }

    public Integer getDocFreq(String queryToken) throws IOException {
        if(!docFreqs.containsKey(queryToken))
            docFreqs.put(queryToken, reader.docFreq(new Term(this.fieldName, queryToken)));
        return docFreqs.get(queryToken);
    }
    
    public Long getcollectFreq(String queryToken) throws IOException {
        if(!collectFreqs.containsKey(queryToken))
            collectFreqs.put(queryToken, reader.totalTermFreq(new Term(this.fieldName, queryToken)));
        return collectFreqs.get(queryToken);
    }

    public void updateDoc(int internalId) throws IOException {
        termVector = reader.getTermVector(internalId, fieldName);
        docSize = termVector.getSumTotalTermFreq();
        termCount = termVector.size();
    }
}
