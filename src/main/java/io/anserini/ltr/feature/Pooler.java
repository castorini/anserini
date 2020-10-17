package io.anserini.ltr.feature;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.util.Collections;
import java.util.List;

public class Pooler {
    static float avg(List<Float> array){
        float sum = 0;
        for(float v:array){
            sum += v;
        }
        return sum/array.size();
    }
    static float sum(List<Float> array){
        float sum = 0;
        for(float v:array){
            sum += v;
        }
        return sum;
    }
    static float median(List<Float> array){
        Collections.sort(array);
        int mid = array.size()/2;
        if(array.size()%2==0){
            return (array.get(mid-1)+array.get(mid))/2;
        } else {
            return array.get(mid)/2;
        }

    }
    static float min(List<Float> array){
        float min = 0;
        for(float v:array){
            if(v<min)
                min = v;
        }
        return min;
    }
    static float max(List<Float> array){
        float max = 0;
        for(float v:array){
            if(v>max)
                max = v;
        }
        return max;
    }
    static float var(List<Float> array){
        float sum = 0;
        float squareSum = 0;
        for(float v:array){
            sum += v;
            squareSum += v*v;
        }
        return (squareSum-sum*sum)/array.size();
    }
}
