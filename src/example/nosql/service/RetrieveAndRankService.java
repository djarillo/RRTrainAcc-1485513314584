package example.nosql.service;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;

import example.nosql.constants.Constants;
import example.nosql.service.model.RetrieveRankCredentials;
import example.nosql.utils.StringUtils;

public class RetrieveAndRankService {

	private static HttpSolrClient solrClient;
	private static RetrieveAndRank service;
	
	public RetrieveAndRankService() {
		
	}
	
	private static HttpSolrClient getSolrClient(String uri, String username, String password, String clusterId) {
		return new HttpSolrClient(service.getSolrUrl(clusterId), createHttpClient(uri, username, password));
	}
	
	private static HttpClient createHttpClient(String uri, String username, String password) {
		final URI scopeUri = URI.create(uri);
		
		final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		
		credentialsProvider.setCredentials(new AuthScope(scopeUri.getHost(), scopeUri.getPort()), new UsernamePasswordCredentials(username, password));
		
		final HttpClientBuilder builder = HttpClientBuilder.create()
				.setMaxConnTotal(128)
				.setMaxConnPerRoute(32)
				.setDefaultRequestConfig(RequestConfig.copy(RequestConfig.DEFAULT).setRedirectsEnabled(true).build())
				.setDefaultCredentialsProvider(credentialsProvider)
				.addInterceptorFirst(new PreemptiveAuthInterceptor());
		
		return builder.build();
	}
	
	public String rank(String pregunta, RetrieveRankCredentials rr) throws SolrServerException, IOException{
		String resposta = null;
		
		service = new RetrieveAndRank();
		service.setUsernameAndPassword(rr.getUsername(), rr.getPassword());
		
		solrClient = getSolrClient(service.getSolrUrl(rr.getSolrClusts().get(0).getClusterId()), rr.getUsername(), rr.getPassword(), rr.getSolrClusts().get(0).getClusterId());
		SolrQuery query = new SolrQuery(pregunta);
		
		query.setParam("ranker_id", rr.getRankers().get(0).getId());
		query.setRequestHandler("/fcselect");
		
		QueryResponse response = solrClient.query(rr.getSolrClusts().get(0).getCollectionName().get(0), query);
		
		if (response.getResults().size() != 0) {
			resposta = StringUtils.reformatString(response.getResults().get(0).getFieldValue(Constants.BODY_STR).toString());
		} else {
			resposta = "";
		}
		
		return resposta;
	}
	
	private static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
		
		public void process (HttpRequest request, HttpContext context) throws HttpException {
			AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
			
			if (authState.getAuthScheme() == null) {
				CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
				Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
				
				if (creds == null) {
					throw new HttpException("No creds provided for preemptive auth.");
				}
				
				authState.update(new BasicScheme(), creds);
			}
		}
	}
	
}
