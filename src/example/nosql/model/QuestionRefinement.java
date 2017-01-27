package example.nosql.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

public class QuestionRefinement implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	private String preg;
	
	@NotEmpty
	private String[] rr;
	
	@NotEmpty
	private String[] id;

	public String getPreg() {
		return preg;
	}

	public void setPreg(String preg) {
		this.preg = preg;
	}

	public String[] getRr() {
		return rr;
	}

	public void setRr(String[] rr) {
		this.rr = rr;
	}

	public String[] getId() {
		return id;
	}

	public void setId(String[] id) {
		this.id = id;
	}
}
