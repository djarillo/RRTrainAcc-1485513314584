package example.nosql.model;

import java.io.Serializable;

public class RankerForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ranker;

	public String getRanker() {
		return ranker;
	}

	public void setRanker(String ranker) {
		this.ranker = ranker;
	}
}
