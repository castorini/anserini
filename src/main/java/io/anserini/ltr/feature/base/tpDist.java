package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.*;

public class tpDist implements FeatureExtractor {
    private String field;
    private String qfield;

    public tpDist() {
        this.field = IndexArgs.CONTENTS;
        this.qfield = "analyzed";
    }

    public tpDist(String field, String qfield) {
        this.field = field;
        this.qfield = qfield;
    }

    class TermPos {
        public Integer mOrder;
        public Integer mPos;
        public TermPos() {
            this.mOrder = null;
            this.mPos = null;
        }
        public TermPos(int order, int pos) {
            this.mOrder = order;
            this.mPos = pos;
        }
    }
    private void accDist(TermPos lhs, TermPos rhs, float wi, float wj, List<Float> ret, List<Integer> prevRhs, int window){
        int dist = rhs.mPos - lhs.mPos + 1 ;
        if (dist > window) return;
        float distWeight = (float) ((Math.min(wi,1.0) + Math.min(wj,1.0)) / dist);
        distWeight = wi*wj*distWeight*distWeight;
        if(rhs.mPos - lhs.mPos == 1 && rhs.mOrder - lhs.mOrder == 1){
            //bigram case
            if(ret.get(0) == 0 || lhs.mPos > prevRhs.get(0)){
                ret.set(0, ret.get(0)+distWeight);
                prevRhs.set(0,rhs.mPos);
            }
        }
        if(ret.get(1) == 0 || lhs.mPos > prevRhs.get(1)){
            ret.set(1, ret.get(1)+distWeight);
            prevRhs.set(1,rhs.mPos);
        }
    }

    /**
     * Calculate the distance between two terms.
     */
    private List<Float> tpDist(List<Integer> posi, List<Integer> posj, float wi, float wj, int window){
        List<Float> ret = new ArrayList<Float>();
        ret.add(0.0f);
        ret.add(0.0f);
        List<Integer> prevRhs = new ArrayList<Integer>();
        prevRhs.add(0);
        prevRhs.add(0);
        List<Integer> cur = new ArrayList<Integer>(); // [index in posi, index in posj]
        List<Integer> end = new ArrayList<Integer>();
        cur.add(0);
        cur.add(0);
        end.add(posi.size());
        end.add(posj.size());
        TermPos lhs = new TermPos();
        TermPos rhs = new TermPos();
        lhs.mOrder = cur.get(0) < cur.get(1)? 0 : 1;
        rhs.mOrder = lhs.mOrder == 1 ? 0 : 1;
        lhs.mPos =  cur.get(lhs.mOrder);
        rhs.mPos =  cur.get(rhs.mOrder);
        int nextPos =Integer.MAX_VALUE;
        TermPos tmp;
        while(cur.get(lhs.mOrder) != end.get(lhs.mOrder)) {
            cur.set(lhs.mOrder, cur.get(lhs.mOrder) + 1);
            if (cur.get(lhs.mOrder) == end.get(lhs.mOrder)) {
                //finish this case
                accDist(lhs, rhs, wi, wj, ret, prevRhs, window);
                break;
            }
            nextPos = cur.get(lhs.mOrder);
            if (nextPos > rhs.mPos) {
                accDist(lhs, rhs, wi, wj, ret, prevRhs, window);
                tmp = new TermPos(lhs.mOrder, nextPos);
                lhs = rhs;
                rhs = tmp;
            } else {
                lhs.mPos = nextPos;
            }
        }
        return ret;
    }

    /**
     * Bigram interval score.
     */

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        DocumentFieldContext context = documentContext.fieldContexts.get(field);
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        float score = 0.0f;
        int window = 100;
        long Wd = context.docSize;
        List<Float> curScore;
        List<List<Integer>> accPositions = new ArrayList<>();
        List<String> accTerms = new ArrayList<>();
        int s = 0;
        for (String queryToken : queryFieldContext.queryTokens) {
            if (context.termFreqs.containsKey(queryToken)) {
                ++s;
                List<Integer> termPos = context.termPositions.get(queryToken);
                // Vector insert ordered by term frequency.
                if (accPositions.size() == 0) {
                    accPositions.add(termPos);
                    accTerms.add(queryToken);
                }
                for (int i = 0; i<(accPositions).size(); ++i){
                    if (accPositions.get(i).size() > termPos.size()){
                        accPositions.add(i, termPos);
                        accTerms.add(i,queryToken);
                        break;
                    }
                }
            }
            for (int i = 0; i<accPositions.size()-1; ++i){
                String termi = accTerms.get(i);
                List<Integer> posi = accPositions.get(i);
                for (int j = (i+1); j<accPositions.size(); ++j){
                    // swap when bigrams are formed only
                    String termj = accTerms.get(j);
                    int deltaOrder = queryFieldContext.queryFreqs.get(termj)-queryFieldContext.queryFreqs.get(termi);
                    if (Math.abs(deltaOrder) == 1) {
                        List<Integer> posj = accPositions.get(j);
                        float termiWi = (float) Math.log(context.numDocs/context.getDocFreq(termi));
                        float termjWi = (float) Math.log(context.numDocs/context.getDocFreq(termj));
                        if(deltaOrder > 0){
                            curScore = tpDist(posi,posj,termiWi,termjWi,window);
                        } else {
                            curScore = tpDist(posj,posi,termjWi,termiWi,window);
                        }
                        //calculate tf score
                        int k1 = 90;
                        int b = 40;
                        float lambda_o = 0.4f;
                        float lambda_u = 0.4f;
                        float avgDocLen = context.totalTermFreq / context.numDocs;
                        float Kd = k1 * ((1-b) + (b*Wd/avgDocLen));
                        float tfi = ((k1+1) * curScore.get(0)) / (Kd+curScore.get(0)) ;
                        curScore.set(0,lambda_o * tfi);
                        float tfj = ((k1+1) * curScore.get(1)) / (Kd+curScore.get(1)) ;
                        curScore.set(0,lambda_u * tfj);
                        score += curScore.get(0);
                        score += curScore.get(1);
                    }
                }
            }
        }
        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        return queryFieldContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        return String.format("%s_%s_tpDistWindow100",field, qfield);
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public String getQField() {
        return qfield;
    }

    @Override
    public FeatureExtractor clone() {
        return new tpDist(field, qfield);
    }
}
