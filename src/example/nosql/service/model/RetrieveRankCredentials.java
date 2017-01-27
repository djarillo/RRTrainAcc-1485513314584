package example.nosql.service.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.Ranker;

import example.nosql.model.SolrClusterForm;

public class RetrieveRankCredentials extends VcapServicesCredentials{

	private String url;
	
	@SuppressWarnings("unchecked")
	private List<Ranker> rankers = LazyList.decorate(new ArrayList<Ranker>(), FactoryUtils.instantiateFactory(Ranker.class));

	@SuppressWarnings("unchecked")
	private List<SolrClusterForm> solrClusts = LazyList.decorate(new ArrayList<SolrClusterForm>(), FactoryUtils.instantiateFactory(SolrClusterForm.class));

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Ranker> getRankers() {
		return rankers;
	}

	public void setRankers(List<Ranker> rankers) {
		this.rankers = rankers;
	}

	public List<SolrClusterForm> getSolrClusts() {
		return solrClusts;
	}

	public void setSolrClusts(List<SolrClusterForm> solrClusts) {
		this.solrClusts = solrClusts;
	}
}
