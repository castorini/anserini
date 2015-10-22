package io.anserini.search.rerank.rm3;

public interface Scorable {

	public void setScore(double score);
	
	public double getScore();
}
