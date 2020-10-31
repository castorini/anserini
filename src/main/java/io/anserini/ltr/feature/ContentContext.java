package io.anserini.ltr.feature;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.mockito.internal.matchers.Null;

import java.io.IOException;
import java.util.*;

public class ContentContext {
    private IndexReader reader;
    private String fieldName;
    public long totalTermFreq;
    public long numDocs;

    public Document doc;
    public long docSize;
    public long termCount;
    public Terms termVector;
    public Map<String,Long> termFreqs;
    public Map<String, List<Integer>> termPositions;
    private Map<String,Integer> docFreqs;
    private Map<String,Long> collectionFreqs;

    //todo implement local normalization here
    public Map<String, List<Float>> statsCache;

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
    
    public Long getCollectionFreq(String queryToken) {
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

        termFreqs = new HashMap<>();
        termPositions = new HashMap<>();
        docFreqs = new HashMap<>();
        collectionFreqs = new HashMap<>();
        statsCache = new HashMap<>();

        TermsEnum termIter = termVector.iterator();
        PostingsEnum positionIter = null;
        while (termIter.next() != null) {
            String termString = termIter.term().utf8ToString();
            long termFreq = termIter.totalTermFreq();
            List<Integer> positions = new ArrayList<>();

            positionIter = termIter.postings(positionIter, PostingsEnum.POSITIONS);
            positionIter.nextDoc();
            for ( int i = 0; i < termFreq; i++ ) {
                positions.add(positionIter.nextPosition());
            }
            Collections.sort(positions);
            termPositions.put(termString, positions);
            termFreqs.put(termString, termFreq);
        }
    }

    public Long getTermFreq(String queryToken) {
        return termFreqs.getOrDefault(queryToken, 0L);
    }

    public int CountBigram(String first, String second, int gap) {
        List<Integer> firstPositions = termPositions.get(first);
        List<Integer> secondPositions = termPositions.get(second);
        int count = 0;
        if(firstPositions!=null&&secondPositions!=null) {
            for(int i: firstPositions){
                for(int j: secondPositions){
                    if (i < j && j <= i+gap){
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
