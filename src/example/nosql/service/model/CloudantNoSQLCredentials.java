package example.nosql.service.model;

public class CloudantNoSQLCredentials extends VcapServicesCredentials{
	
	private String host;
	
	private String port;
	
	private String url;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
