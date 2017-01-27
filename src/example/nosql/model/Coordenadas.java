package example.nosql.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

public class Coordenadas implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unchecked")
	private List<Coordenada> coords = LazyList.decorate(new ArrayList<Coordenada>(), FactoryUtils.instantiateFactory(Coordenada.class));

	public List<Coordenada> getCoords() {
		return coords;
	}

	public void setCoords(List<Coordenada> coords) {
		this.coords = coords;
	}
}
