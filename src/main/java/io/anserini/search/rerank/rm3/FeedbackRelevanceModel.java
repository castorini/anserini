package io.anserini.search.rerank.rm3;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.search.rerank.ScoredDocuments;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.IndexReader;

public class FeedbackRelevanceModel extends FeedbackModel {
	private boolean stripNumbers = false;
	private double[] docWeights = null;
	
	public void build(ScoredDocuments docs, Stopper stopper, IndexReader reader) {
		this.stopper = stopper;
		try {
			Set<String> vocab = new HashSet<String>();
			List<FeatureVector> fbDocVectors = new LinkedList<FeatureVector>();

			

			double[] rsvs = new double[docs.documents.length];
			for (int k=0; k<docs.documents.length; k++) {
			  rsvs[k] = docs.scores[k];
			}

			for (int k=0; k<50; k++) {
				FeatureVector docVector = new FeatureVector(reader.getTermVector(docs.ids[k], StatusField.TEXT.name), stopper);
				vocab.addAll(docVector.getFeatures());
				fbDocVectors.add(docVector);
				//System.out.println(docVector);
			}

			features = new LinkedList<KeyValuePair>();

			
			Iterator<String> it = vocab.iterator();
			while(it.hasNext()) {
				String term = it.next();				
				double fbWeight = 0.0;

				Iterator<FeatureVector> docIT = fbDocVectors.iterator();
				int k=0;
				while(docIT.hasNext()) {
					double docWeight = 1.0;
					if(docWeights != null)
						docWeight = docWeights[k];
					FeatureVector docVector = docIT.next();
					double docProb = docVector.getFeaturetWeight(term) / docVector.getLength();
					docProb *= rsvs[k++] * docWeight;

					fbWeight += docProb;
				}
				
				fbWeight /= (double)fbDocVectors.size();
				
				KeyValuePair tuple = new KeyValuePair(term, fbWeight);
				features.add(tuple);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setDocWeights(double[] docWeights) {
		this.docWeights = docWeights;
	}


}
