package example.nosql.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

public class SolrClusterForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String clusterId;
	
	private String name;
	
	private String size;
	
	private String status;
	
	@SuppressWarnings("unchecked")
	private List<String> configName = LazyList.decorate(new ArrayList<String>(), FactoryUtils.instantiateFactory(String.class));
	
	@SuppressWarnings("unchecked")
	private List<String> collectionName = LazyList.decorate(new ArrayList<String>(), FactoryUtils.instantiateFactory(String.class));

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getConfigName() {
		return configName;
	}

	public void setConfigName(List<String> configName) {
		this.configName = configName;
	}

	public List<String> getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(List<String> collectionName) {
		this.collectionName = collectionName;
	}
	
}
