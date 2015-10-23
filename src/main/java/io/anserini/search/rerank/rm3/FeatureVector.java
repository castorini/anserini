package io.anserini.search.rerank.rm3;


import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public class FeatureVector  {
	private static StandardAnalyzer analyzer;	
	private Map<String, Float> features = new HashMap<String, Float>();
	private RmStopper stopper;
	private double length = 0.0;

	// CONSTRUCTORS  
	public FeatureVector() {}
	public FeatureVector(Terms terms, RmStopper stopper) {
		this.stopper = stopper;


    try {
      TermsEnum termsEnum = terms.iterator();

      BytesRef text = null;
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();
        if (term.length() < 2) continue;
        if (stopper.isStopWord(term)) continue;
        if (!term.matches("[a-z0-9#@]+")) continue;
        int freq = (int) termsEnum.totalTermFreq();
        length += freq;
        features.put(term, (float) freq);
      }
    } catch (Exception e) {

    }
    //		List<String> terms = this.analyze(text);
//		Iterator<String> termsIt = terms.iterator();
//		while(termsIt.hasNext()) {
//			String term = termsIt.next();
//			length += 1.0;
//			Double val = (Double)features.get(term);
//			if(val == null) {
//				features.put(term, new Double(1.0));
//			} else {
//				double v = val.doubleValue() + 1.0;
//				features.put(term, new Double(v));
//			}
//		}
	}

//	public FeatureVector(Stopper stopper) {
//		this.stopper = stopper;
//		if(stopper==null || stopper.asSet().size()==0) {
//			analyzer = new StandardAnalyzer(Version.LUCENE_41, CharArraySet.EMPTY_SET);
//		} else {
//			CharArraySet charArraySet = new CharArraySet(Version.LUCENE_41, stopper.asSet(), true);
//			analyzer = new StandardAnalyzer(Version.LUCENE_41, charArraySet);
//		}
//		features = new HashMap<String,Double>();
//	}






	// MUTATORS

	/**
	 * Add all the terms in a string to this vector
	 * @param text a space-delimited string where we want to add each word.
	 */
	public void addText(String text) {
		List<String> terms = this.analyze(text);
		Iterator<String> termsIt = terms.iterator();
		while(termsIt.hasNext()) {
			String term = termsIt.next();		
			addTerm(term);
		}
	}

	/**
	 * Add a term to this vector.  if it's already here, increment its count.
	 * @param term
	 */
	public void addTerm(String term) {
		if(stopper != null && stopper.isStopWord(term))
			return;

		Float freq = ((Float)features.get(term));
		if(freq == null) {
			features.put(term, new Float(1.0));
		} else {
			double f = freq.doubleValue();
			features.put(term, new Float(f+1.0));
		}
		length += 1.0;
	}


	/**
	 * Add a term to this vector with this weight.  if it's already here, supplement its weight.
	 * @param term
	 * @param weight
	 */
	public void addTerm(String term, double weight) {
		Float w = ((Float)features.get(term));
		if(w == null) {
			features.put(term, new Float(weight));
		} else {
			double f = w.doubleValue();
			features.put(term, new Float(f+weight));
		}
		length += weight;
	}

	/**
	 * in case we want to override the derived length.
	 * @param length
	 */
	public void setLength(double length) {
		this.length = length;
	}

	public void pruneToSize(int k) {
		List<KeyValuePair> kvpList = getOrderedFeatures();

		Iterator<KeyValuePair> it = kvpList.iterator();

		Map<String,Float> newMap = new HashMap<String,Float>(k);
		int i=0;
		while(it.hasNext()) {
			KeyValuePair kvp = it.next();
			newMap.put((String)kvp.getKey(), kvp.getValue());
			if(i++ > k)
				break;
		}

		features = (HashMap<String, Float>) newMap;

	}

	public void normalizeToOne() {
		Map<String,Float> f = new HashMap<String,Float>(features.size());

		Iterator<String> it = features.keySet().iterator();
		while(it.hasNext()) {
			String feature = it.next();
			float obs = features.get(feature);
			f.put(feature, (float) (obs/length));
		}

		features = f;
	}


	// ACCESSORS

	public Set<String> getFeatures() {
		return features.keySet();
	}

	public double getLength() {
		return length;
	}

	public int getDimensions() {
		return features.size();
	}

	public float getFeaturetWeight(String feature) {
		Float w = (Float)features.get(feature);
		return (w==null) ? 0.0f : w.floatValue();
	}

	public Iterator<String> iterator() {
		return features.keySet().iterator();
	}

	public boolean contains(Object key) {
		return features.containsKey(key);
	}

	public double getVectorNorm() {
		double norm = 0.0;
		Iterator<String> it = features.keySet().iterator();
		while(it.hasNext()) {
			norm += Math.pow(features.get(it.next()), 2.0);
		}
		return Math.sqrt(norm);
	}



	// VIEWING

	@Override
	public String toString() {
		return this.toString(features.size());
	}

	private List<KeyValuePair> getOrderedFeatures() {
		List<KeyValuePair> kvpList = new ArrayList<KeyValuePair>(features.size());
		Iterator<String> featureIterator = features.keySet().iterator();
		while(featureIterator.hasNext()) {
			String feature = featureIterator.next();
			float value   = features.get(feature);
			KeyValuePair keyValuePair = new KeyValuePair(feature, value);
			kvpList.add(keyValuePair);
		}

		Collections.sort(kvpList, new Comparator<KeyValuePair>() {
      public int compare(KeyValuePair x, KeyValuePair y) {
        double xVal = x.getValue();
        double yVal = y.getValue();

        return (xVal > yVal ? -1 : (xVal == yVal ? 0 : 1));
      }
    });

		return kvpList;
	}

	public String toString(int k) {
		DecimalFormat format = new DecimalFormat("#.#########");
		StringBuilder b = new StringBuilder();
		List<KeyValuePair> kvpList = getOrderedFeatures();
		Iterator<KeyValuePair> it = kvpList.iterator();
		int i=0;
		while(it.hasNext() && i++ < k) {
			KeyValuePair pair = it.next();
			b.append(format.format(pair.getValue()) + " " + pair.getKey() + "\n");
		}
		return b.toString();

	}


	// UTILS
	public List<String> analyze(String text) {
		List<String> result = new LinkedList<String>();
		try {
			TokenStream stream = null;
			stream = analyzer.tokenStream("text", new StringReader(text));

			CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
			while(stream.incrementToken()) {
				String term = charTermAttribute.toString();
				result.add(term);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static FeatureVector interpolate(FeatureVector x, FeatureVector y, double xWeight) {
		FeatureVector z = new FeatureVector();
		Set<String> vocab = new HashSet<String>();
		vocab.addAll(x.getFeatures());
		vocab.addAll(y.getFeatures());
		Iterator<String> features = vocab.iterator();
		while(features.hasNext()) {
			String feature = features.next();
			double weight  = xWeight*x.getFeaturetWeight(feature) + (1.0-xWeight)*y.getFeaturetWeight(feature);
			z.addTerm(feature, weight);
		}
		return z;
	}


	public static void main(String[] args) {
		String text = "This. This is NOT a test, nor is it better than 666!";

		RmStopper stopper = new RmStopper();
		stopper.addStopword("this");
		stopper.addStopword("is");
		stopper.addStopword("better");
		
		FeatureVector featureVector = new FeatureVector();
		List<String> terms = featureVector.analyze(text);
		Iterator<String> termIterator = terms.iterator();
		while(termIterator.hasNext()) {
			System.out.println(termIterator.next());
		}
	}

	
	private class KeyValuePair {
	  private String key;
	  private float value;
	  
	  public KeyValuePair(String key, float value)  {
	    this.key = key;
	    this.value = value;
	  }

	  public String getKey() {
	    return key;
	  }
	  
	  @Override
	  public String toString() {
	    StringBuilder b = new StringBuilder(value + "\t" + key);
	    return b.toString();
	  }

	  public float getValue() {
	    return value;
	  }
	}

}
