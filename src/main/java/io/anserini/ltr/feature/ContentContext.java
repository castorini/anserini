package io.anserini.ltr.feature;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;

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
    public List<Pair<Integer,String>> positionTerm;
    private Map<String,Integer> docFreqs;
    private Map<String,Long> collectionFreqs;
    private Map<String, Map<Integer,List<Integer>>> postings;
    private Map<Pair<String, String>, Integer> bigramCollectionFreqs;
    private Map<Pair<String, String>, Integer> bigramDocFreqs;

    //todo implement local normalization here
    public Map<String, List<Float>> statsCache;

    public ContentContext(IndexReader reader, String fieldName) throws IOException {
        this.reader = reader;
        this.fieldName = fieldName;
        numDocs = reader.getDocCount(fieldName);
        totalTermFreq = reader.getSumTotalTermFreq(fieldName);
        docFreqs = new HashMap<>();
        collectionFreqs = new HashMap<>();
        postings = new HashMap<>();
        bigramCollectionFreqs = new HashMap<>();
        bigramDocFreqs = new HashMap<>();
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
                int position = positionIter.nextPosition();
                positions.add(position);
                positionTerm.add(Pair.of(position,termString));
            }
            Collections.sort(positions);
            termPositions.put(termString, positions);
            termFreqs.put(termString, termFreq);
        }
        positionTerm.sort(new Comparator<Pair<Integer, String>>() {
            @Override
            public int compare(Pair<Integer, String> p1, Pair<Integer, String> p2) {
                return p1.getLeft() - p2.getLeft();
            }
        });
    }

    public Long getTermFreq(String queryToken) {
        return termFreqs.getOrDefault(queryToken, 0L);
    }

    public int countBigram(String first, String second, int gap) {
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

    public int getBigramCollectionFreqs(String first, String second, int gap) throws IOException {
        Pair<String, String> key = Pair.of(first, second);
        if (bigramCollectionFreqs.containsKey(key)) {
            return bigramCollectionFreqs.get(key);
        } else {
            int cf = 0;
            Map<Integer, List<Integer>> firstPostings, secondPostings;
            firstPostings = getPostings(first);
            secondPostings = getPostings(second);

            Set<Integer> needCheck = firstPostings.keySet();
            needCheck.retainAll(secondPostings.keySet());

            for(int docId:needCheck){
                List<Integer> firstPositions = firstPostings.get(docId);
                List<Integer> secondPositions = secondPostings.get(docId);
                for(int i: firstPositions){
                    for(int j: secondPositions){
                        if (i < j && j <= i+gap){
                            cf++;
                        }
                    }
                }
            }
            bigramCollectionFreqs.put(key, cf);
            return cf;
        }
    }

    public Map<Integer, List<Integer>> getPostings(String term) throws IOException {
        if (postings.containsKey(term)) {
            return postings.get(term);
        } else {
            Map<Integer, List<Integer>> posting = new HashMap<>();
            Term t = new Term(fieldName, term);
            PostingsEnum postingsEnum = MultiTerms.getTermPostingsEnum(reader, fieldName, t.bytes(), PostingsEnum.POSITIONS);
            int docId;
            while ((docId = postingsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                List<Integer> postions = new ArrayList<>();
                int freq = postingsEnum.freq();
                for (int i = 0; i < freq; i++) {
                    postions.add(postingsEnum.nextPosition());
                }
                posting.put(docId, postions);
            }
            postings.put(term, posting);
            return posting;
        }
    }

}
