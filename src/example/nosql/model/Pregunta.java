package example.nosql.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

public class Pregunta implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	private String pregunta;

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}
}
