package io.anserini.rerank.rm3;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class Rm3Stopper {
	public static final Pattern SPACE_PATTERN = Pattern.compile(" ", Pattern.DOTALL);
	private Set<String> stopwords;


	public Rm3Stopper() {
		stopwords = new HashSet<String>();
	}
	
	public Rm3Stopper(String pathToStoplist) {
		try {
			stopwords = new HashSet<String>();
			
			// assume our stoplist has one stopword per line
			List<String> lines = IOUtils.readLines(new FileInputStream(pathToStoplist));
			Iterator<String> it = lines.iterator();
			while(it.hasNext()) {
				stopwords.add(it.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String apply(String text) {
		StringBuilder b = new StringBuilder();
		String[] toks = SPACE_PATTERN.split(text);
		for(String tok : toks) {
			if(! isStopWord(tok))
				b.append(tok + " ");
		}
		return b.toString().trim();
	}
	public void addStopword(String term) {
		stopwords.add(term);
	}
	public boolean isStopWord(String term) {
		return (stopwords.contains(term)) ? true : false;
	}
	
	public Set<String> asSet() {
		return stopwords;
	}
}
