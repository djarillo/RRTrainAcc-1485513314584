package example.nosql.model;

import java.io.Serializable;

public class Coordenada implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float xCoord;
	private float yCoord;
	
	public float getxCoord() {
		return xCoord;
	}
	public void setxCoord(float xCoord) {
		this.xCoord = xCoord;
	}
	public float getyCoord() {
		return yCoord;
	}
	public void setyCoord(float yCoord) {
		this.yCoord = yCoord;
	}
}
