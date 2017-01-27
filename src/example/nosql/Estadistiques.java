package example.nosql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.Charsets;

import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.opencsv.CSVReader;

@WebServlet("/Estadistiques")
public class Estadistiques extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String vcap_str = "VCAP_SERVICES";
	private String jdbcUrlStr;
	private String userStr;
	private String pwdStr;
	private String userStrRR;
	private String pwdStrRR;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String selector = "\"selector\" : {\"name\": \"Train CSV\"}";
		String docID = null;
		String fileName = null;
		String output = null;
		double iteration = 0;
		String sqlStmt2 = "SELECT TRUNCATE((DEC(count(*))/DEC((select count(*) from DASH7707.RR_STATISTIC where ITERATION_ID = ";
		String sqlStmt3 = "INSERT INTO DASH7707.RR_ACCURACY (ITERATION_ID, ACCURACY) values (";
		ResultSet rs;
		double accuracy = 0;
		double rowCount = 0;
		
		String envVars = System.getenv(vcap_str);
		if (envVars != null) {
			JsonElement jElement = new JsonParser().parse(envVars);
			JsonObject vcapObject = jElement.getAsJsonObject();
			JsonArray vcapArray = vcapObject.getAsJsonArray("dashDB");
			JsonObject dashObject = (JsonObject) vcapArray.get(0);
			JsonElement credentials = dashObject.get("credentials");
			JsonObject credentialsObject = credentials.getAsJsonObject();
			JsonElement jdbcUrl = credentialsObject.get("jdbcurl");
			JsonElement user = credentialsObject.get("username");
			JsonElement pwd = credentialsObject.get("password");
			jdbcUrlStr = jdbcUrl.getAsString();
			userStr = user.getAsString();
			pwdStr = pwd.getAsString();
			
			JsonArray vcapArrayRR = vcapObject.getAsJsonArray("retrieve_and_rank");
			JsonObject rrObject = (JsonObject) vcapArrayRR.get(0);
			JsonElement credentialsRR = rrObject.get("credentials");
			JsonObject credentialsObjectRR = credentialsRR.getAsJsonObject();
			JsonElement userRR = credentialsObjectRR.get("username");
			JsonElement pwdRR = credentialsObjectRR.get("password");
			userStrRR = userRR.getAsString();
			pwdStrRR = pwdRR.getAsString();
		}
		
		Database db = null;
		Statement stmt = null;
		
		try {
			db = getDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Connection conn = null;
		try{
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			conn = DriverManager.getConnection(jdbcUrlStr, userStr, pwdStr);
			conn.setAutoCommit(false);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		
		List<HashMap> doc = db.findByIndex(selector, HashMap.class);
		
		Iterator<HashMap> iter = doc.iterator();
		while (iter.hasNext()) {
			HashMap<String, Object> obj = (HashMap<String, Object>) iter.next();
			LinkedTreeMap<String, Object> attachments = (LinkedTreeMap<String, Object>) obj.get("_attachments");
			
			if (attachments != null && attachments.size() > 0) {
				docID = obj.get("_id") + "";
				iteration = (double) obj.get("last_iteration") + 1;
				obj.put("last_iteration", iteration);
				sqlStmt2 = sqlStmt2 + iteration + "))) * 100, 2) from DASH7707.RR_STATISTIC where RESP_ESP_ID = RESP_OBT_ID and ITERATION_ID = " + iteration;
				rowCount = (double) obj.get("row_count");
				
				for (Object key : attachments.keySet()) {
					fileName = key + "";
				}
			}
			
			db.update(obj);
		}
		
		InputStream dbResponse = db.find(docID + "/" + fileName);
		CSVReader reader = new CSVReader(new InputStreamReader(dbResponse, Charsets.UTF_8), ',');
		List<String[]> csvBody = reader.readAll();
		@SuppressWarnings("unused")
		int result = 0;
		Integer redirections = csvBody.size();
		System.setProperty("http.maxRedirects", redirections.toString());
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		
		for (int i = 0; i < rowCount/*csvBody.size()*/; i++) {
			int idPregunta = i + 1;
			int idRespEsp = new Integer(csvBody.get(i)[1]).intValue();
			int idRespObt;
			String pregunta = csvBody.get(i)[0];
			String preguntaEncoded = URLEncoder.encode(pregunta, "UTF-8");
			String requestRR = "https://\"" + userStrRR + "\":\"" + pwdStrRR + "\"@gateway.watsonplatform.net/retrieve-and-rank/api/v1/solr_clusters/scd22c5dcd_d1bd_4f2c_ae10_e3f76b99479b/solr/TravelCollection/fcselect?ranker_id=3b140ax15-rank-2601&q=" + preguntaEncoded + "&wt=json&fl=id";
			System.out.println(requestRR);
			
			try {
				URL url = new URL(requestRR);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				
				con.setRequestMethod("GET");
				con.setRequestProperty("Accept", "application/json");
				
				Authenticator au = new Authenticator() {
					@Override
					protected PasswordAuthentication
		               getPasswordAuthentication() {
		               return new PasswordAuthentication
		                  (userStr, pwdStr.toCharArray());
		         }
				};
				Authenticator.setDefault(au);
				
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				output = br.readLine();
				con.disconnect();
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}
			
			JsonParser jsonParser = new JsonParser();
			JsonObject json = (JsonObject) jsonParser.parse(output);
			
			JsonElement responseElement = json.get("response");
			JsonObject responseObject = responseElement.getAsJsonObject();
			JsonArray docsArray = responseObject.getAsJsonArray("docs");
			
			JsonElement docRR = docsArray.get(0);
			JsonObject docObject = docRR.getAsJsonObject();
			JsonElement idElement = docObject.get("id");
			idRespObt = idElement.getAsInt();
			
			String sqlStmt = "INSERT INTO DASH7707.RR_STATISTIC (ITERATION_ID, PREGUNTA_ID, RESP_ESP_ID, RESP_OBT_ID) VALUES (" + iteration + "," + idPregunta + "," + idRespEsp + "," + idRespObt + ")";
			
			try {
				stmt = conn.createStatement();
				result = stmt.executeUpdate(sqlStmt);
			} catch (SQLException sql) {
				sql.printStackTrace();
			}
		}
		
		try {
			conn.commit();
			System.out.println(sqlStmt2);
			rs = stmt.executeQuery(sqlStmt2);
			while (rs.next()) {
				accuracy = rs.getDouble(1);
			}
			
			sqlStmt3 = sqlStmt3 + iteration +", " + accuracy + ")";
			System.out.println(sqlStmt3);
			result = stmt.executeUpdate(sqlStmt3);
			conn.commit();
		} catch (SQLException sqlee) {
			sqlee.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		} finally {
			reader.close();
			try {
				conn.close();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
		
		response.getWriter().println("Tot ok!");
	}

	private Database getDB() {
		return CloudantClientMgr.getDB();
	}
}
