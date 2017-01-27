package example.nosql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import example.nosql.constants.Constants;
import example.nosql.model.Pregunta;
import example.nosql.model.RetrieveRank;
import example.nosql.model.RetrieveRankResponse;
import example.nosql.service.VcapServices;

@Controller
public class RankAndRetrieveController {

	private VcapServices vcap;
	
	@RequestMapping(value = "/Retrieve", method = RequestMethod.GET)
	public String retrieve(@Valid Pregunta pregunta, BindingResult result, ModelMap model) throws IOException {
		String output = null;
		String preguntaEncoded = URLEncoder.encode(pregunta.getPregunta(), "ISO-8859-1");
		RetrieveRankResponse rrResp = new RetrieveRankResponse();
		Pregunta preguntaPant = new Pregunta();
		preguntaPant.setPregunta(new String(pregunta.getPregunta().getBytes("ISO-8859-1"), "UTF-8"));
		rrResp.setRrPregunta(preguntaPant);
		
		vcap = VcapServices.getVcapServices();
		
		String requestURL = "https://\"" + vcap.getRrCred().getUsername() + "\":\"" + vcap.getRrCred().getPassword() + "\"@gateway.watsonplatform.net/retrieve-and-rank/api/v1/solr_clusters/scd22c5dcd_d1bd_4f2c_ae10_e3f76b99479b/solr/TravelCollection/fcselect?ranker_id=3b140ax15-rank-2601&q=" + preguntaEncoded + "&wt=json&fl=id,title,body";
		
		try {
			URL url = new URL(requestURL);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			
			Authenticator au = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword().toCharArray());
				}
			};
			Authenticator.setDefault(au);
			
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code :" + conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			output = br.readLine();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject json = (JsonObject) jsonParser.parse(output);
		
		JsonElement responseElement = json.get(Constants.RESP_STR);
		JsonObject responseObject = responseElement.getAsJsonObject();
		JsonArray docsArray = responseObject.getAsJsonArray(Constants.DOCS_STR);
		
		for (int i = 0; i < docsArray.size(); i++) {
			RetrieveRank rr = new RetrieveRank();
			JsonElement doc = docsArray.get(i);
			JsonObject docObject = doc.getAsJsonObject();
			JsonElement idElement = docObject.get(Constants.ID_STR);
			JsonElement bodyElement = docObject.get(Constants.BODY_STR);
			JsonElement titleElement = docObject.get(Constants.TITLE_STR);
			rr.setId(idElement.getAsString());
			byte[] bBody = bodyElement.getAsString().getBytes("ISO-8859-1");
			rr.setBody(new String(bBody, "UTF-8"));
			byte[] bTitle = titleElement.getAsString().getBytes("ISO-8859-1");
			rr.setTitle(new String(bTitle, "UTF-8"));
			rrResp.getRrResponse().add(rr);
		}
		
		model.addAttribute("rrResp", rrResp);
		
		return "refinementResp";
	}
}
