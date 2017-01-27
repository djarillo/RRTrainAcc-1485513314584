package example.nosql;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;

import example.nosql.service.VcapServices;

public class CloudantClientMgr {

	private static CloudantClient cloudant = null;
	private static Database db = null;

	private static String databaseName = "training";

	private static String user = null;
	private static String password = null;
	
	private static VcapServices vcap = null;

	private static void initClient() {
		if (cloudant == null) {
			synchronized (CloudantClientMgr.class) {
				if (cloudant != null) {
					return;
				}
				cloudant = createClient();

			} // end synchronized
		}
	}

	private static CloudantClient createClient() {
		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the NoSQL DB connection info
		String serviceName = null;
		vcap = VcapServices.getVcapServices();

		if (vcap != null) {
			serviceName = vcap.getCnsCred().getName();
			System.out.println("Service Name - " + serviceName);

			user = vcap.getCnsCred().getUsername();
			password = vcap.getCnsCred().getPassword();

		} else {
			throw new RuntimeException("VCAP_SERVICES not found");
		}

		try {
			CloudantClient client = ClientBuilder.account(user)
					.username(user)
					.password(password)
					.build();
			return client;
		} catch (CouchDbException e) {
			throw new RuntimeException("Unable to connect to repository", e);
		}
	}

	public static Database getDB() {
		if (cloudant == null) {
			initClient();
		}

		if (db == null) {
			try {
				db = cloudant.database(databaseName, true);
			} catch (Exception e) {
				throw new RuntimeException("DB Not found", e);
			}
		}
		return db;
	}

	private CloudantClientMgr() {
	}
}
