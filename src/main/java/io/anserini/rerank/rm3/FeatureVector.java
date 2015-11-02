package io.anserini.rerank.rm3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public class FeatureVector  {
	private Map<String, Float> features = new HashMap<String, Float>();

	public FeatureVector() {}

	/**
	 * Add a term to this vector.  if it's already here, increment its count.
	 * @param term
	 */
	public void addTerm(String term) {
		Float freq = ((Float)features.get(term));
		if(freq == null) {
			features.put(term, new Float(1.0));
		} else {
			double f = freq.doubleValue();
			features.put(term, new Float(f+1.0));
		}
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

  public FeatureVector normalizeToOne() {
    double norm = getVectorNorm();

    for (String f : features.keySet()) {
      features.put(f, (float) (features.get(f) / norm));
    }

    return this;
  }

	// ACCESSORS

	public Set<String> getFeatures() {
		return features.keySet();
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

  public static FeatureVector fromTerms(List<String> terms) {
    FeatureVector f = new FeatureVector();
    for (String t : terms) {
      f.addTerm(t);
    }
    return f;
  }

  public static FeatureVector fromLuceneTermVector(Terms terms, RmStopper stopper) {
    FeatureVector f = new FeatureVector();

    try {
      TermsEnum termsEnum = terms.iterator();

      BytesRef text = null;
      while ((text = termsEnum.next()) != null) {
        String term = text.utf8ToString();
        if (term.length() < 2) continue;
        if (stopper.isStopWord(term)) continue;
        if (!term.matches("[a-z0-9#@]+")) continue;
        int freq = (int) termsEnum.totalTermFreq();
        f.addTerm(term, (float) freq);
      }
    } catch (Exception e) {
      e.printStackTrace();
      // Return empty feature vector
      return f;
    }

    return f;
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
