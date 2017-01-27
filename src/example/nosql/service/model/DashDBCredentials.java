package example.nosql.service.model;

public class DashDBCredentials extends VcapServicesCredentials{
	
	private String port;
	
	private String db;
	
	private String sslJdbcUrl;
	
	private String host;
	
	private String httpsUrl;
	
	private String dsn;
	
	private String hostname;
	
	private String jdbcUrl;
	
	private String sslDsn;
	
	private String uri;

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getSslJdbcUrl() {
		return sslJdbcUrl;
	}

	public void setSslJdbcUrl(String sslJdbcUrl) {
		this.sslJdbcUrl = sslJdbcUrl;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHttpsUrl() {
		return httpsUrl;
	}

	public void setHttpsUrl(String httpsUrl) {
		this.httpsUrl = httpsUrl;
	}

	public String getDsn() {
		return dsn;
	}

	public void setDsn(String dsn) {
		this.dsn = dsn;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getSslDsn() {
		return sslDsn;
	}

	public void setSslDsn(String sslDsn) {
		this.sslDsn = sslDsn;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
