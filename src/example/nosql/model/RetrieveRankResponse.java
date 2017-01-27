package example.nosql.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

public class RetrieveRankResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Pregunta rrPregunta;
	
	@SuppressWarnings("unchecked")
	private List<RetrieveRank> rrResponse = LazyList.decorate(new ArrayList<RetrieveRank>(), FactoryUtils.instantiateFactory(RetrieveRank.class));

	public Pregunta getRrPregunta() {
		return rrPregunta;
	}

	public void setRrPregunta(Pregunta rrPregunta) {
		this.rrPregunta = rrPregunta;
	}

	public List<RetrieveRank> getRrResponse() {
		return rrResponse;
	}

	public void setRrResponse(List<RetrieveRank> rrResponse) {
		this.rrResponse = rrResponse;
	}
}
