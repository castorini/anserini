package io.anserini.ltr.feature;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContentContext {
    private IndexReader reader;
    private String fieldName;
    public long totalTermFreq;
    public long numDocs;

    public Document doc;
    public long docSize;
    public long termCount;
    public Terms termVector;
    public Map<String,Long> termFreqs = new HashMap<>();


    private Map<String,Integer> docFreqs = new HashMap<>();
    private Map<String,Long> collectionFreqs = new HashMap<>();
    
    public ContentContext(IndexReader reader, String fieldName) throws IOException {
        this.reader = reader;
        this.fieldName = fieldName;
        numDocs = reader.getDocCount(fieldName);
        totalTermFreq = reader.getSumTotalTermFreq(fieldName);
    }

    public Integer getDocFreq(String queryToken) {
        try{
            if(!docFreqs.containsKey(queryToken))
                docFreqs.put(queryToken, reader.docFreq(new Term(this.fieldName, queryToken)));
            return docFreqs.get(queryToken);
        } catch (IOException e){
            System.out.println(e);
            return 0;
        }
    }
    
    public Long getCollectionFreq(String queryToken){
        try{
            if(!collectionFreqs.containsKey(queryToken))
                collectionFreqs.put(queryToken, reader.totalTermFreq(new Term(this.fieldName, queryToken)));
            return collectionFreqs.get(queryToken);
        } catch (IOException e){
            System.out.println(e);
            return 0L;
        }
    }

    public void updateDoc(int internalId, Set<String> fieldsToLoad) throws IOException {
        doc = reader.document(internalId, fieldsToLoad);
        termVector = reader.getTermVector(internalId, fieldName);
        docSize = termVector.getSumTotalTermFreq();
        termCount = termVector.size();

        TermsEnum termsEnum = termVector.iterator();
        while (termsEnum.next() != null) {
            String termString = termsEnum.term().utf8ToString();
            termFreqs.put(termString, termsEnum.totalTermFreq());
        }
    }

    public Long getTermFreq(String queryToken) {
        return termFreqs.getOrDefault(queryToken, 0L);
    }

    public Long getTotalTermFreq(String queryToken) {
        try {
             return reader.totalTermFreq(new Term(this.fieldName, queryToken));
        } catch (IOException e) {
            System.out.println(e);
            return 0L;
        }
    }


}
