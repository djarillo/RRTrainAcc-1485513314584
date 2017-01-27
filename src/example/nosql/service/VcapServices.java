package example.nosql.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.common.util.NamedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.Ranker;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.Rankers;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusterList;

import example.nosql.constants.Constants;
import example.nosql.model.SolrClusterForm;
import example.nosql.service.model.CloudantNoSQLCredentials;
import example.nosql.service.model.DashDBCredentials;
import example.nosql.service.model.RetrieveRankCredentials;
import example.nosql.utils.HttpSolrClientUtils;

public class VcapServices {

	/**
	 * Private objects that hold the environment variables
	 */
	private String envVars;
	private RetrieveRankCredentials rrCred;
	private CloudantNoSQLCredentials cnsCred;
	private DashDBCredentials ddbCred;
	
	/**
	 * The singleton object
	 */
	private static VcapServices vcapServices;
	
	public VcapServices() {
		
	}
	
	/**
	 * Method to get the singleton object - initialize if it has not yet created
	 */
	static public VcapServices getVcapServices() {
		if (vcapServices == null) {
			vcapServices = new VcapServices();
			vcapServices.initialize();
		}
		
		return vcapServices;
	}
	
	/**
	 * Initialize the singleton object, and initialize environment variables
	 */
	public void initialize() {
		
		rrCred = new RetrieveRankCredentials();
		cnsCred = new CloudantNoSQLCredentials();
		ddbCred = new DashDBCredentials();
		
		rrCred = parseRR(true);
		cnsCred = parseCNS();
		ddbCred = parseDDB();
	}
	
	public void modifyVcapServices(String operation) {
			parseRR(false);
	}
	
	private RetrieveRankCredentials parseRR(boolean isJson) {
		RetrieveRankCredentials cred = new RetrieveRankCredentials();
		RetrieveAndRank serviceRR;
		List<Ranker> rankerList;
		@SuppressWarnings("unchecked")
		List<SolrClusterForm> clusters = LazyList.decorate(new ArrayList<SolrClusterForm>(), FactoryUtils.instantiateFactory(SolrClusterForm.class));
		
		Map<String, String> mapCred;
		
		if (isJson) {
			mapCred = parse(Constants.RR_STR);
			
			cred.setName(mapCred.get(Constants.NAME_STR));
			cred.setLabel(mapCred.get(Constants.LABEL_STR));
			cred.setPlan(mapCred.get(Constants.PLAN_STR));
			cred.setUsername(mapCred.get(Constants.USR_STR));
			cred.setPassword(mapCred.get(Constants.PWD_STR));
			cred.setUrl(mapCred.get(Constants.URL_STR));
		} else {
			cred = rrCred;
		}
				
		serviceRR = new RetrieveAndRank();
		serviceRR.setUsernameAndPassword(cred.getUsername(), cred.getPassword());
		
		clusters = getClusters(serviceRR, cred);
		rankerList = getRankers(serviceRR, cred);
		
		cred.setRankers(rankerList);
		cred.setSolrClusts(clusters);

		return cred;
	}
	
	private CloudantNoSQLCredentials parseCNS() {
		CloudantNoSQLCredentials cred = new CloudantNoSQLCredentials();
		Map<String, String> mapCred = parse(Constants.CNS_STR);
		
		cred.setName(mapCred.get(Constants.NAME_STR));
		cred.setLabel(mapCred.get(Constants.LABEL_STR));
		cred.setPlan(mapCred.get(Constants.PLAN_STR));
		cred.setUsername(mapCred.get(Constants.USR_STR));
		cred.setPassword(mapCred.get(Constants.PWD_STR));
		cred.setHost(mapCred.get(Constants.HOST_STR));
		cred.setPort(mapCred.get(Constants.PORT_STR));
		cred.setUrl(mapCred.get(Constants.URL_STR));
		
		return cred;		
	}
	
	private DashDBCredentials parseDDB() {
		DashDBCredentials cred = new DashDBCredentials();
		Map<String, String> mapCred = parse(Constants.DASH_STR);
		
		cred.setName(mapCred.get(Constants.NAME_STR));
		cred.setLabel(mapCred.get(Constants.LABEL_STR));
		cred.setPlan(mapCred.get(Constants.PLAN_STR));
		cred.setUsername(mapCred.get(Constants.USR_STR));
		cred.setPassword(mapCred.get(Constants.PWD_STR));
		cred.setPort(mapCred.get(Constants.PORT_STR));
		cred.setDb(mapCred.get(Constants.DB_STR));
		cred.setSslJdbcUrl(mapCred.get(Constants.SSLJDBCURL_STR));
		cred.setHost(mapCred.get(Constants.HOST_STR));
		cred.setHttpsUrl(mapCred.get(Constants.HTTPSURL_STR));
		cred.setDsn(mapCred.get(Constants.DSN_STR));
		cred.setHostname(mapCred.get(Constants.HOSTNAME_STR));
		cred.setJdbcUrl(mapCred.get(Constants.JDBC_STR));
		cred.setSslDsn(mapCred.get(Constants.SSLDSN_STR));
		cred.setUri(mapCred.get(Constants.URI_STR));
		
		return cred;
	}
	
	private Map<String, String> parse(String service) {
		Map<String, String> credMap = new HashMap<String, String>();
		envVars = System.getenv(Constants.VCAP_STR);
		
		if (envVars != null) {
			JsonElement jElement = new JsonParser().parse(envVars);
			JsonObject vcapObject = jElement.getAsJsonObject();
			JsonArray vcapArray = vcapObject.getAsJsonArray(service);
			
			JsonObject jsonObject = (JsonObject) vcapArray.get(0);
			JsonElement name = jsonObject.get(Constants.NAME_STR);
			JsonElement label = jsonObject.get(Constants.LABEL_STR);
			JsonElement plan = jsonObject.get(Constants.PLAN_STR);
			JsonElement credentials = jsonObject.get(Constants.CRED_STR);
			
			JsonObject credentialsObject = credentials.getAsJsonObject();
			JsonElement user = credentialsObject.get(Constants.USR_STR);
			JsonElement pwd = credentialsObject.get(Constants.PWD_STR);
			
			credMap.put(Constants.NAME_STR, name.getAsString());
			credMap.put(Constants.LABEL_STR, label.getAsString());
			credMap.put(Constants.PLAN_STR, plan.getAsString());
			credMap.put(Constants.USR_STR, user.getAsString());
			credMap.put(Constants.PWD_STR, pwd.getAsString());
			
			if (service.equals(Constants.RR_STR)) {
				JsonElement url = credentialsObject.get(Constants.URL_STR);
				
				credMap.put(Constants.URL_STR, url.getAsString());
			} else if (service.equals(Constants.CNS_STR)) {
				JsonElement host = credentialsObject.get(Constants.HOST_STR);
				JsonElement port = credentialsObject.get(Constants.PORT_STR);
				JsonElement url = credentialsObject.get(Constants.URL_STR);
				
				credMap.put(Constants.HOST_STR, host.getAsString());
				credMap.put(Constants.PORT_STR, port.getAsString());
				credMap.put(Constants.URL_STR, url.getAsString());
			} else if (service.equals(Constants.DASH_STR)) {
				JsonElement port = credentialsObject.get(Constants.PORT_STR);
				JsonElement db = credentialsObject.get(Constants.DB_STR);
				JsonElement sslJdbcUrl = credentialsObject.get(Constants.SSLJDBCURL_STR);
				JsonElement host = credentialsObject.get(Constants.HOST_STR);
				JsonElement httpsURL = credentialsObject.get(Constants.HTTPSURL_STR);
				JsonElement dsn = credentialsObject.get(Constants.DSN_STR);
				JsonElement hostname = credentialsObject.get(Constants.HOSTNAME_STR);
				JsonElement jdbcUrl = credentialsObject.get(Constants.JDBC_STR);
				JsonElement sslDsn = credentialsObject.get(Constants.SSLDSN_STR);
				JsonElement uri = credentialsObject.get(Constants.URI_STR);
				
				credMap.put(Constants.PORT_STR, port.getAsString());
				credMap.put(Constants.DB_STR, db.getAsString());
				credMap.put(Constants.SSLJDBCURL_STR, sslJdbcUrl.getAsString());
				credMap.put(Constants.HOST_STR, host.getAsString());
				credMap.put(Constants.HTTPSURL_STR, httpsURL.getAsString());
				credMap.put(Constants.DSN_STR, dsn.getAsString());
				credMap.put(Constants.HOSTNAME_STR, hostname.getAsString());
				credMap.put(Constants.JDBC_STR, jdbcUrl.getAsString());
				credMap.put(Constants.SSLDSN_STR, sslDsn.getAsString());
				credMap.put(Constants.URI_STR, uri.getAsString());
			}
		} else {
			//throw new RuntimeException("VCAP_SERVICES not found");
			if (service.equals(Constants.RR_STR)) {
				credMap.put(Constants.NAME_STR, "Retrieve and Rank-ys");
				credMap.put(Constants.LABEL_STR, "retrieve_and_rank");
				credMap.put(Constants.PLAN_STR, "standard");
				credMap.put(Constants.USR_STR, "6fe698ba-9ed4-43e9-bbd5-b3ec4eb600b7");
				credMap.put(Constants.PWD_STR, "1IzOu6yWwntZ");
				credMap.put(Constants.URL_STR, "https://gateway.watsonplatform.net/retrieve-and-rank/api");
			} else if (service.equals(Constants.CNS_STR)) {
				credMap.put(Constants.NAME_STR, "chatWatsonApi-cloudantNoSQLDB");
				credMap.put(Constants.LABEL_STR, "cloudantNoSQLDB");
				credMap.put(Constants.PLAN_STR, "Shared");
				credMap.put(Constants.USR_STR, "62d6f4b0-e0e4-4300-802f-10d39460b417-bluemix");
				credMap.put(Constants.PWD_STR, "f193ef02ea03432c2ea98aa6effc90b0ced4c6b4d9872ae77da6faf5fe84f3f3");
				credMap.put(Constants.HOST_STR, "62d6f4b0-e0e4-4300-802f-10d39460b417-bluemix.cloudant.com");
				credMap.put(Constants.PORT_STR, "443");
				credMap.put(Constants.URL_STR, "https://62d6f4b0-e0e4-4300-802f-10d39460b417-bluemix:f193ef02ea03432c2ea98aa6effc90b0ced4c6b4d9872ae77da6faf5fe84f3f3@62d6f4b0-e0e4-4300-802f-10d39460b417-bluemix.cloudant.com");
			} else if (service.equals(Constants.DASH_STR)) {
				credMap.put(Constants.NAME_STR, "dashDB-nk");
				credMap.put(Constants.LABEL_STR, "dashDB");
				credMap.put(Constants.PLAN_STR, "Entry");
				credMap.put(Constants.USR_STR, "dash7707");
				credMap.put(Constants.PWD_STR, "HA0udXNv9swR");
				credMap.put(Constants.PORT_STR, "50000");
				credMap.put(Constants.DB_STR, "BLUDB");
				credMap.put(Constants.SSLJDBCURL_STR, "jdbc:db2://dashdb-entry-yp-dal09-07.services.dal.bluemix.net:50001/BLUDB:sslConnection=true;");
				credMap.put(Constants.HOST_STR, "dashdb-entry-yp-dal09-07.services.dal.bluemix.net");
				credMap.put(Constants.HTTPSURL_STR, "https://dashdb-entry-yp-dal09-07.services.dal.bluemix.net:8443");
				credMap.put(Constants.DSN_STR, "DATABASE=BLUDB;HOSTNAME=dashdb-entry-yp-dal09-07.services.dal.bluemix.net;PORT=50000;PROTOCOL=TCPIP;UID=dash7707;PWD=HA0udXNv9swR;");
				credMap.put(Constants.HOSTNAME_STR, "dashdb-entry-yp-dal09-07.services.dal.bluemix.net");
				credMap.put(Constants.JDBC_STR, "jdbc:db2://dashdb-entry-yp-dal09-07.services.dal.bluemix.net:50000/BLUDB");
				credMap.put(Constants.SSLDSN_STR, "DATABASE=BLUDB;HOSTNAME=dashdb-entry-yp-dal09-07.services.dal.bluemix.net;PORT=50001;PROTOCOL=TCPIP;UID=dash7707;PWD=HA0udXNv9swR;Security=SSL;");
				credMap.put(Constants.URI_STR, "db2://dash7707:HA0udXNv9swR@dashdb-entry-yp-dal09-07.services.dal.bluemix.net:50000/BLUDB");
			}
		}
		
		return credMap;
	}
	
	private List<Ranker> getRankers(RetrieveAndRank service, RetrieveRankCredentials credentials) {
		Rankers rankers;
		List<Ranker> rankList;
		
		service.setUsernameAndPassword(credentials.getUsername(), credentials.getPassword());
		
		rankers = service.getRankers();
		rankList =  rankers.getRankers();
		
		for (int i = 0; i < rankList.size(); i++) {
			Ranker status = service.getRankerStatus(rankList.get(i).getId());
			rankList.get(i).setStatus(status.getStatus());
			rankList.get(i).setStatusDescription(status.getStatusDescription());
		}
		
		return rankList;
	}
	
	@SuppressWarnings("unchecked")
	private List<SolrClusterForm> getClusters(RetrieveAndRank service, RetrieveRankCredentials credentials) {
		SolrClusterList clustList;

		List<String> configurations = LazyList.decorate(new ArrayList<String>(), FactoryUtils.instantiateFactory(String.class));
		List<SolrCluster> listClusters;

		List<SolrClusterForm> clusters = LazyList.decorate(new ArrayList<SolrClusterForm>(), FactoryUtils.instantiateFactory(SolrClusterForm.class));

		List<String> collections = LazyList.decorate(new ArrayList<String>(), FactoryUtils.instantiateFactory(String.class));
		CollectionAdminRequest.List listCollectionRequest = new CollectionAdminRequest.List();
		CollectionAdminResponse response;
		HttpSolrClient client;
		NamedList<Object> collectionList = new NamedList<Object>();
		ArrayList<String> colls = new ArrayList<String>();
		
		clustList = service.getSolrClusters();
		listClusters = clustList.getSolrClusters();
		
		for (int j = 0; j < listClusters.size(); j++) {
			SolrCluster cluster = listClusters.get(j);
			SolrClusterForm clusterForm = new SolrClusterForm();
			clusterForm.setClusterId(cluster.getId());
			clusterForm.setName(cluster.getName());
			clusterForm.setSize(cluster.getSize());
			clusterForm.setStatus(cluster.getStatus().toString());
			
			List<String> configList = service.getSolrClusterConfigurations(clusterForm.getClusterId());
			
			for (int k = 0; k < configList.size(); k++) {
				configurations.add(configList.get(k));
			}
			
			client = new HttpSolrClient(service.getSolrUrl(clusterForm.getClusterId()), HttpSolrClientUtils.createHttpClient(credentials.getUrl(), credentials.getUsername(), credentials.getPassword()));
			
			try {
				response = listCollectionRequest.process(client);
				collectionList = response.getResponse();
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			colls = (ArrayList<String>) collectionList.get("collections");
			
			for (int i = 0; i < colls.size(); i++) {
				String collection = colls.get(i);
				collections.add(collection);				
			}
			
			clusterForm.setConfigName(configurations);
			clusterForm.setCollectionName(collections);
			clusters.add(clusterForm);
		}
		
		return clusters;
	}
	
	public RetrieveRankCredentials getRrCred() {
		return rrCred;
	}

	public void setRrCred(RetrieveRankCredentials rrCred) {
		this.rrCred = rrCred;
	}

	public CloudantNoSQLCredentials getCnsCred() {
		return cnsCred;
	}

	public void setCnsCred(CloudantNoSQLCredentials cnsCred) {
		this.cnsCred = cnsCred;
	}

	public DashDBCredentials getDdbCred() {
		return ddbCred;
	}

	public void setDdbCred(DashDBCredentials ddbCred) {
		this.ddbCred = ddbCred;
	}
}
