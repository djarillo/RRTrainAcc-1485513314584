package example.nosql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.Rankers;

import example.nosql.constants.Constants;
import example.nosql.model.SolrClusterForm;
import example.nosql.service.RetrieveAndRankService;
import example.nosql.service.model.RetrieveRankCredentials;

@Controller
public class AccuracyController {
	
	private List<RetrieveRankCredentials> rrCredAccuracy;
	RetrieveAndRankService rank1;
	RetrieveAndRankService rank2;

	@RequestMapping(value = "/Accuracy", method = RequestMethod.GET)
	public String accuracyRedirect(ModelMap model) {
		rrCredAccuracy = new ArrayList<RetrieveRankCredentials>(2);
		RetrieveAndRank service = new RetrieveAndRank();
		SolrClusterForm cluster = new SolrClusterForm();
		Rankers rankers;
		RetrieveRankCredentials rrCred = new RetrieveRankCredentials();
		
		service.setUsernameAndPassword("2ff18c19-7484-4676-8208-cbb971f15dc4", "z3UIk7jAHC6D");
		rankers = service.getRankers();
		
		rrCred.setLabel("retrieve_and_rank");
		rrCred.setName("SDC-bot_es-RR-DC");
		rrCred.setPassword("z3UIk7jAHC6D");
		rrCred.setPlan("standard");
		rrCred.setRankers(rankers.getRankers());
		cluster.setClusterId("sc4a9f43f7_6080_4f5b_8d28_7e5dcb2be982");
		cluster.setCollectionName(Arrays.asList("sdc_bot_es_collection"));
		cluster.setConfigName(Arrays.asList(""));
		cluster.setName("sdc_bot_es_cluster");
		cluster.setSize("0.05GB");
		cluster.setStatus("AVAILABLE");
		rrCred.setSolrClusts(Arrays.asList(cluster));
		rrCred.setUrl("https://gateway.watsonplatform.net/retrieve-and-rank/api");
		rrCred.setUsername("2ff18c19-7484-4676-8208-cbb971f15dc4");
		rrCredAccuracy.add(rrCred);
		
		service = new RetrieveAndRank();
		service.setUsernameAndPassword("5757de10-cf4f-4637-bd0d-6fc9072f2abb", "ZDjFr5UczthY");
		rankers = service.getRankers();
		cluster = new SolrClusterForm();
		rrCred = new RetrieveRankCredentials();
		
		rrCred.setLabel("retrieve_and_rank");
		rrCred.setName("Bot-SDC_es");
		rrCred.setPassword("ZDjFr5UczthY");
		rrCred.setPlan("standard");
		rrCred.setRankers(rankers.getRankers());
		cluster.setClusterId("scf6ec6514_835b_4603_8d7e_726ecd35d811");
		cluster.setCollectionName(Arrays.asList("Bot-SDC_es-colection"));
		cluster.setConfigName(Arrays.asList(""));
		cluster.setName("Bot-SDC_es-cluster");
		cluster.setSize("0.05GB");
		cluster.setStatus("AVAILABLE");
		rrCred.setSolrClusts(Arrays.asList(cluster));
		rrCred.setUrl("https://gateway.watsonplatform.net/retrieve-and-rank/api");
		rrCred.setUsername("5757de10-cf4f-4637-bd0d-6fc9072f2abb");
		rrCredAccuracy.add(rrCred);

		model.addAttribute("rrCredAccuracy", rrCredAccuracy);
		
		return "accuracy";
	}
	
	@RequestMapping(value = "/accuracyTest", method = RequestMethod.POST)
	public String accuracyTest(ModelMap model) throws IOException, SolrServerException {
		File accuracyFile;
		XSSFWorkbook workBook;
		XSSFSheet sheet;
		FileInputStream fis = null;
		String pregunta = null;
		String resposta[] = new String[2];
		String preguntaEncoded = URLEncoder.encode("¿Cuáles son las condiciones generales de uso de TRAM?", "ISO-8859-1");
		
		accuracyFile = new File("C://Users//josep.marco.selma//Documents//IBM//Bluemix//Watson//SDC-bot//respostes_correctes.xlsx");
		
		rank1 = new RetrieveAndRankService();
		rank2 = new RetrieveAndRankService();
		
		try {
			fis = new FileInputStream(accuracyFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			workBook = new XSSFWorkbook(accuracyFile);
			sheet = workBook.getSheetAt(0);
			sheet.setColumnWidth(2, 25746);
			sheet.setColumnWidth(4, 25746);

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				
				Cell cell = row.getCell(0);
				
				switch (cell.getCellTypeEnum()) {
				case STRING:
					pregunta = cell.getStringCellValue();
					break;
				case NUMERIC:
					break;
				case BLANK:
					break;
				}
									
				resposta[0] = rank1.rank(pregunta, rrCredAccuracy.get(0));
				resposta[1] = rank2.rank(pregunta, rrCredAccuracy.get(1));
				
				CellStyle style = workBook.createCellStyle();
				style.setWrapText(true);
				
				cell = row.createCell(2, CellType.STRING);
				
				cell.setCellStyle(style);
				cell.setCellValue(resposta[0]);
				
				cell = row.createCell(3, CellType.FORMULA);
				cell.setCellFormula("IF(B"+ (i + 1) + "=C" + (i + 1) + ",1,0)");
				
				cell = row.createCell(4, CellType.STRING);
				
				cell.setCellStyle(style);
				cell.setCellValue(resposta[1]);
				
				cell = row.createCell(5, CellType.FORMULA);
				cell.setCellFormula("IF(B"+ (i + 1) + "=E" + (i + 1) + ",1,0)");
			}
			Row fila = sheet.getRow(1);
			Cell cela = fila.getCell(6);
			
			cela = fila.createCell(6, CellType.FORMULA);
			cela.setCellFormula("SUM(D1:D" + sheet.getLastRowNum() + ")/COUNT(D1:D" + sheet.getLastRowNum() + ")");
			
			cela = fila.getCell(7);
			cela = fila.createCell(7, CellType.FORMULA);
			cela.setCellFormula("SUM(F1:F" + sheet.getLastRowNum() + ")/COUNT(F1:F" + sheet.getLastRowNum() + ")");
			
			fis.close();
			
			FileOutputStream os = new FileOutputStream(new File("C://Users//josep.marco.selma//Documents//IBM//Bluemix//Watson//SDC-bot//respostes_correctes_resp.xlsx"));
			workBook.write(os);
			
			workBook.close();
			
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException ife) {
			ife.printStackTrace();
		}
		
		return "accuracy";
	}
	
	private String retrieveAndRank(String question, final RetrieveRankCredentials rr) throws IOException{
		String output = null;
		String answer = null;
		String preguntaEncoded = URLEncoder.encode(question, "ISO-8859-1");

		//String requestURL = "https://\"" + rr.getUsername() + "\":\"" + rr.getPassword() + "\"@gateway.watsonplatform.net/retrieve-and-rank/api/v1/solr_clusters/" + rr.getSolrClusts().get(0).getClusterId() + "/solr/" + rr.getSolrClusts().get(0).getCollectionName().get(0) + "/fcselect?ranker_id=" + rr.getRankers().get(0).getId() + "&q=" + preguntaEncoded + "&wt=json&fl=id,title,body";
		String requestURL = "https://gateway.watsonplatform.net/retrieve-and-rank/api/v1/solr_clusters/" + rr.getSolrClusts().get(0).getClusterId() + "/solr/" + rr.getSolrClusts().get(0).getCollectionName().get(0) + "/fcselect?ranker_id=" + rr.getRankers().get(0).getId() + "&q=" + preguntaEncoded + "&wt=json&fl=id,title,body";
		System.out.println("La URL es: " + requestURL);
		
		try {
			URL url = new URL(requestURL);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			
			Authenticator au = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(rr.getUsername(), rr.getPassword().toCharArray());
				}
			};
			Authenticator.setDefault(au);
			conn.connect();
			
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code :" + conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			output = br.readLine();
			
			if (br != null) {
				br.close();
			}
			conn.disconnect();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject json = (JsonObject) jsonParser.parse(output);
		
		JsonElement responseElement = json.get(Constants.RESP_STR);
		JsonObject responseObject = responseElement.getAsJsonObject();
		JsonArray docsArray = responseObject.getAsJsonArray(Constants.DOCS_STR);
		
		if (docsArray.size() != 0) {
			JsonElement doc = docsArray.get(0);
			JsonObject docObjetc = doc.getAsJsonObject();
			JsonElement bodyElement = docObjetc.get(Constants.BODY_STR);
			byte[] bBody = bodyElement.getAsString().getBytes("ISO-8859-1");
			answer = new String(bBody, "UTF-8").trim();
			//answer = answer.trim();
		} else {
			answer = "";
		}
		
		return answer;
	}
	
	private String[] retrieveAndRank2(String question, final List<RetrieveRankCredentials> rr) throws IOException{
		String output = null;
		String output2 = null;
		String answer[] = new String[2];
		String preguntaEncoded = URLEncoder.encode(question, "ISO-8859-1");

		//String requestURL = "https://\"" + rr.getUsername() + "\":\"" + rr.getPassword() + "\"@gateway.watsonplatform.net/retrieve-and-rank/api/v1/solr_clusters/" + rr.getSolrClusts().get(0).getClusterId() + "/solr/" + rr.getSolrClusts().get(0).getCollectionName().get(0) + "/fcselect?ranker_id=" + rr.getRankers().get(0).getId() + "&q=" + preguntaEncoded + "&wt=json&fl=id,title,body";
		String requestURL = "https://gateway.watsonplatform.net/retrieve-and-rank/api/v1/solr_clusters/";
		System.out.println("La URL es: " + requestURL);
		
		try {
			URL url = new URL(requestURL);
			URL rr1 = new URL(url, rr.get(0).getSolrClusts().get(0).getClusterId() + "/solr/" + rr.get(0).getSolrClusts().get(0).getCollectionName().get(0) + "/fcselect?ranker_id=" + rr.get(0).getRankers().get(0).getId() + "&q=" + preguntaEncoded + "&wt=json&fl=id,title,body");
			URL rr2 = new URL(url, rr.get(1).getSolrClusts().get(0).getClusterId() + "/solr/" + rr.get(1).getSolrClusts().get(0).getCollectionName().get(0) + "/fcselect?ranker_id=" + rr.get(1).getRankers().get(0).getId() + "&q=" + preguntaEncoded + "&wt=json&fl=id,title,body");
			HttpsURLConnection conn = (HttpsURLConnection) rr1.openConnection();
			HttpsURLConnection conn2 = (HttpsURLConnection) rr2.openConnection();
			
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			
			conn2.setRequestMethod("GET");
			conn2.setRequestProperty("Accept", "application/json");
			
			Authenticator au = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(rr.get(0).getUsername(), rr.get(0).getPassword().toCharArray());
				}
			};
			Authenticator.setDefault(au);
			conn.connect();
			
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code :" + conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			output = br.readLine();
			
			if (br != null) {
				br.close();
			}
			conn.disconnect();
			
			Authenticator au2 = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(rr.get(1).getUsername(), rr.get(1).getPassword().toCharArray());
				}
			};
			Authenticator.setDefault(au2);
			conn2.connect();
			
			/*if (conn2.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code :" + conn2.getResponseCode());
			}*/
			
			BufferedReader br2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
			output2 = br2.readLine();
			
			if (br2 != null) {
				br2.close();
			}
			conn2.disconnect();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject json = (JsonObject) jsonParser.parse(output);
		
		JsonElement responseElement = json.get(Constants.RESP_STR);
		JsonObject responseObject = responseElement.getAsJsonObject();
		JsonArray docsArray = responseObject.getAsJsonArray(Constants.DOCS_STR);
		
		if (docsArray.size() != 0) {
			JsonElement doc = docsArray.get(0);
			JsonObject docObjetc = doc.getAsJsonObject();
			JsonElement bodyElement = docObjetc.get(Constants.BODY_STR);
			byte[] bBody = bodyElement.getAsString().getBytes("ISO-8859-1");
			answer[0] = new String(bBody, "UTF-8").trim();
			//answer = answer.trim();
		} else {
			answer[0] = "";
		}
		
		JsonParser jsonParser2 = new JsonParser();
		JsonObject json2 = (JsonObject) jsonParser2.parse(output2);
		
		JsonElement responseElement2 = json2.get(Constants.RESP_STR);
		JsonObject responseObject2 = responseElement2.getAsJsonObject();
		JsonArray docsArray2 = responseObject2.getAsJsonArray(Constants.DOCS_STR);
		
		if (docsArray2.size() != 0) {
			JsonElement doc2 = docsArray2.get(0);
			JsonObject docObjetc2 = doc2.getAsJsonObject();
			JsonElement bodyElement2 = docObjetc2.get(Constants.BODY_STR);
			byte[] bBody2 = bodyElement2.getAsString().getBytes("ISO-8859-1");
			answer[1] = new String(bBody2, "UTF-8").trim();
			//answer = answer.trim();
		} else {
			answer[1] = "";
		}
		
		return answer;
	}
}
