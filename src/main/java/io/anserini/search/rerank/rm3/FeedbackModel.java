package io.anserini.search.rerank.rm3;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class FeedbackModel {
//	protected List<TResult> relDocs;
//	protected GQuery originalQuery;
	protected int fbDocCount  = 20;
	protected int fbTermCount = 20;
	protected List<KeyValuePair> features;		// these will be KeyValuePair objects
	protected Stopper stopper;
	
	
	
	public void build(Stopper stopper) {
		this.stopper = stopper;
	}
	

	
//	public GQuery asGquery() {
//		GQuery newQuery = new GQuery();
//		newQuery.setTitle(originalQuery.getTitle());
//		newQuery.setText(originalQuery.getText());
//		
//		FeatureVector finalVector = new FeatureVector(stopper);
//		
//		ScorableComparator comparator = new ScorableComparator(true);
//		Collections.sort(features, comparator);
//		Iterator<KeyValuePair> it = features.iterator();
//		
//		int i=0;
//		while(it.hasNext() && i++ < fbTermCount) {			
//			KeyValuePair tuple = it.next();
//			finalVector.addTerm(tuple.getKey(), tuple.getScore());
//		}
//		
//		newQuery.setFeatureVector(finalVector);
//		
//		return newQuery;
//	}

	public FeatureVector asFeatureVector() {
		FeatureVector f = new FeatureVector();
		Iterator<KeyValuePair> it = features.iterator();
		
		while(it.hasNext()) {			
			KeyValuePair tuple = it.next();
			f.addTerm(tuple.getKey(), tuple.getScore());
		}	
		
		return f;
	}
	
	public Map<String,Double> asMap() {
		Map<String,Double> map = new HashMap<String,Double>(features.size());
		Iterator<KeyValuePair> it = features.iterator();
		while(it.hasNext()) {
			KeyValuePair tuple = it.next();
			map.put(tuple.getKey(), tuple.getScore());
		}

		return map;
	}
	
	@Override 
	public String toString() {
		return toString(features.size());
	}
	
	public String toString(int k) {
		DecimalFormat format = new DecimalFormat("#.#####################");


		
		ScorableComparator comparator = new ScorableComparator(true);
		Collections.sort(features, comparator);
		
		double sum = 0.0;
		Iterator<KeyValuePair> it = features.iterator();
		int i=0;
		while(it.hasNext() && i++ < k) {			
			sum += it.next().getScore();
		}
		
		StringBuilder b = new StringBuilder();
		it = features.iterator();
		i=0;
		while(it.hasNext() && i++ < k) {			
			KeyValuePair tuple = it.next();
			b.append(format.format(tuple.getScore()/sum) + " " + tuple.getKey() + "\n");
		}
		
		return b.toString();
	}
	

//	public void setRes(List<TResult> relDocs) {
//		this.relDocs = relDocs;
//	}
//	public void setOriginalQuery(GQuery originalQuery) {
//		this.originalQuery = originalQuery;
//	}
//	public void setFbTermCount(int fbTermCount) {
//		this.fbTermCount = fbTermCount;
//	}
	
	
	
	
}
