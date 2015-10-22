package io.anserini.search.rerank.rm3;

import java.util.Comparator;


public class ScorableComparator implements Comparator<Scorable>{
	private boolean decreasing = true;
	
	public ScorableComparator(boolean decreasing) {
		this.decreasing = decreasing;
	}
	public int compare(Scorable x, Scorable y) {
		double xVal = x.getScore();
		double yVal = y.getScore();
		
		if(decreasing) {
			return (xVal > yVal  ? -1 : (xVal == yVal ? 0 : 1));
		} else {
			return (xVal < yVal  ? -1 : (xVal == yVal ? 0 : 1));
		}

	}

}
