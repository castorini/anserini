package io.anserini.ltr.feature;

import io.anserini.index.IndexArgs;
import io.anserini.index.IndexReaderUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.SmallFloat;

import java.io.IOException;
import java.util.*;

public class FieldContext {
    private IndexReader reader;
    private IndexSearcher searcher;
    private String fieldName;
    public long totalTermFreq;
    public long numDocs;

    public long docSize;
    public long termCount;
    public Map<String,Long> termFreqs;
    public Map<String, List<Integer>> termPositions;
    public List<Pair<Integer,String>> positionTerm;
    private Map<String,Integer> docFreqs;
    private Map<String,Long> collectionFreqs;
    private Map<String, Map<Integer,List<Integer>>> postings;
    private Map<Pair<String, String>, Integer> bigramCollectionFreqs;

    //todo implement local normalization here
    public Map<String, List<Float>> statsCache;

    public FieldContext(IndexReader reader, IndexSearcher searcher, String fieldName){
        this.reader = reader;
        this.searcher = searcher;
        this.fieldName = fieldName;
        try {
            numDocs = reader.getDocCount(fieldName);
            totalTermFreq = reader.getSumTotalTermFreq(fieldName);
        } catch (IOException e) {
//            e.printStackTrace();
            numDocs = 0;
            totalTermFreq = 0;
        }
        docFreqs = new HashMap<>();
        collectionFreqs = new HashMap<>();
        postings = new HashMap<>();
        bigramCollectionFreqs = new HashMap<>();
    }

    public Integer getDocFreq(String queryToken) {
        try{
            if(!docFreqs.containsKey(queryToken))
                docFreqs.put(queryToken, reader.docFreq(new Term(this.fieldName, queryToken)));
            return docFreqs.get(queryToken);
        } catch (IOException e){
//            e.printStackTrace();
            return 0;
        }
    }
    
    public Long getCollectionFreq(String queryToken) {
        try{
            if(!collectionFreqs.containsKey(queryToken))
                collectionFreqs.put(queryToken, reader.totalTermFreq(new Term(this.fieldName, queryToken)));
            return collectionFreqs.get(queryToken);
        } catch (IOException e){
//            e.printStackTrace();
            return 0L;
        }
    }

    public void updateDoc(int internalId){
        try {
            Terms termVector = reader.getTermVector(internalId, fieldName);
            docSize = termVector.getSumTotalTermFreq();
            termCount = termVector.size();

            termFreqs = new HashMap<>();
            termPositions = new HashMap<>();
            positionTerm = new ArrayList<>();
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
        } catch (IOException e) {
//            e.printStackTrace();
            docSize = 0;
            termCount = 0;

            termFreqs = new HashMap<>();
            termPositions = new HashMap<>();

            statsCache = new HashMap<>();
        }

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

    public int getBigramCollectionFreqs(String first, String second, int gap){
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

    public Map<Integer, List<Integer>> getPostings(String term) {
        if (postings.containsKey(term)) {
            return postings.get(term);
        } else {
            Map<Integer, List<Integer>> posting = new HashMap<>();
            try {
                Term t = new Term(fieldName, term);
                PostingsEnum postingsEnum = MultiTerms.getTermPostingsEnum(reader, fieldName, t.bytes(), PostingsEnum.POSITIONS);
                if(postingsEnum!=null) {
                    int docId;
                    while ((docId = postingsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                        List<Integer> postions = new ArrayList<>();
                        int freq = postingsEnum.freq();
                        for (int i = 0; i < freq; i++) {
                            postions.add(postingsEnum.nextPosition());
                        }
                        posting.put(docId, postions);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            postings.put(term, posting);
            return posting;
        }
    }

    public List<Integer> getAllDocID() {
        Query q = new DocValuesFieldExistsQuery(fieldName);
        List<Integer> DocIDs = new ArrayList<>();
        try {
            ScoreDoc[] scoreDocs = searcher.search(q, reader.maxDoc()).scoreDocs;
            for (int i = 0; i < scoreDocs.length; i++) {
                DocIDs.add(scoreDocs[i].doc);
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return DocIDs;
    }

    private void buildFieldStat(List<Integer> docids){
        List<Long> fieldDocLength = new ArrayList<>();
        List<Long> fieldTermCount = new ArrayList<>();
        Terms terms = null;
        for (int i: docids) {
            try {
                terms = reader.getTermVector(i, fieldName);
                fieldDocLength.add(terms.getSumTotalTermFreq());
                fieldTermCount.add(terms.size());
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        long sum = 0;
        long squareSum = 0;
        long min = 0;
        long max = 0;
        for (long v : fieldDocLength) {
            sum += v;
            squareSum += v * v;
            if(v > max) max = v;
            if(v < min) min = v;
        }
        double avg = sum / fieldDocLength.size();
        double var = (squareSum / fieldDocLength.size() - avg * avg);
    }

    /**
     * We will implement this according to the Lucene specification
     * the formula used:
     * sum ( IDF(qi) * (df(qi,D) * (k+1)) / (df(qi,D) + k * (1-b + b*|D| / avgFL))
     * IDF and avgFL computation are described above.
     */
    public List<Float> generateBM25Mean(List<String> terms, Double k1, Double b){
        List<Float> score = new ArrayList<Float>();
        double avgFL = (double)totalTermFreq/numDocs;
        for (String queryToken : terms) {
            //mean of ( BM25 score for a single term )
            Map<Integer, List<Integer>> post = this.getPostings(queryToken);
            float totalSingleTerm = 0.0f;
            int docFreq = this.getDocFreq(queryToken);
            //iterate across all documents has this word
            for (Map.Entry<Integer, List<Integer>> entry : post.entrySet()) {
                List<Integer> positions = entry.getValue();
                long termFreq = positions.size();
                double numerator = (k1 + 1) * termFreq;
                double docLengthFactor = b * (docSize / avgFL);
                double denominator = termFreq + (k1) * (1 - b + docLengthFactor);
                double idf = Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d)); // ok
                totalSingleTerm += idf * numerator / denominator;
            }
            totalSingleTerm = totalSingleTerm / post.size();
            score.add(totalSingleTerm);
        }
        return score;
    }

}
