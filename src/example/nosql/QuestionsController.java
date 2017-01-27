package example.nosql;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.ws.rs.core.Response;

import org.apache.commons.io.Charsets;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloudant.client.api.Database;
import com.google.gson.internal.LinkedTreeMap;
import com.opencsv.CSVReader;

import example.nosql.model.QuestionRefinement;

@Controller
public class QuestionsController {

	@ResponseBody
	@RequestMapping(value="/Questions", method = RequestMethod.GET)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String updateCSV(@Valid QuestionRefinement qRef, BindingResult result, ModelMap model) throws UnsupportedEncodingException {
		
		String returnText = null;
		String selector = "\"selector\" : {\"name\": \"Train CSV\"}";
		String docID = null;
		String fileName = null;
		String rev = null;
		String content = null;
		byte[] bPregunta = qRef.getPreg().getBytes("ISO-8859-1");
		String pregunta = new String(bPregunta, "UTF-8");
		String[] idResposta = qRef.getId();
		String[] relevance = qRef.getRr();
		
		Database db = null;
		
		try{ 
			db = getDB();
		} catch (Exception e) {
			e.printStackTrace();
			returnText = Response.Status.INTERNAL_SERVER_ERROR.toString();
		}
		
		List<HashMap> doc = db.findByIndex(selector, HashMap.class);
		
		Iterator<HashMap> iter = doc.iterator();
		while (iter.hasNext()) {
			HashMap<String, Object> obj = (HashMap) iter.next();
			LinkedTreeMap<String, Object> attachments = (LinkedTreeMap<String, Object>) obj.get("_attachments");
			
			if (attachments != null && attachments.size() > 0) {
				docID = obj.get("_id") + "";
				rev = obj.get("_rev") + "";
				
				for (Object key : attachments.keySet()) {
					fileName = key + "";
					LinkedTreeMap<String, Object> att = (LinkedTreeMap<String, Object>) attachments.get(key);
					content = att.get("content_type").toString();
				}
				
				if (modifyCSV(docID, fileName, pregunta, idResposta, relevance, rev, content)) {
					returnText = "<div class=\"modal-content\"><div class=\"modal-header\"><span class=\"close\" onclick=\"$('#myModal').hide()\">x</span><h2>Update file result:</h2></div><div class=\"modal-body\"><p>The csv has been update correctly</p></div></div>";
				} else {
					returnText = "<div class=\"modal-content\"><div class=\"modal-header\"><span class=\"close\" onclick=\"$('#myModal').hide()\">x</span><h2>Update file result:</h2></div><div class=\"modal-body\"><p>The csv can't be update!</p></div></div>";
				}
				
			}
		}
		
		return returnText;
	}
	
	private Database getDB() {
		return CloudantClientMgr.getDB();
	}
	
	private boolean modifyCSV(String docID, String csvID, String preguntaWeb, String[] idRespWeb, String[] relevanceWeb, String revision, String contentType) {
		
		Database db = null;
		boolean preguntaTrobada = false;
		
		try{ 
			db = getDB();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		InputStream dbResponse = db.find(docID + "/" + csvID);
		CSVReader reader = new CSVReader(new InputStreamReader(dbResponse, Charsets.UTF_8), ',');
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StringBuilder csvLine = new StringBuilder();
		
		try {
			List<String[]> csvBody = reader.readAll();
			int i = 0;
			
			while (i < csvBody.size()) {
				if (csvBody.get(i)[0].equals(preguntaWeb)) {
					preguntaTrobada = true;
					HashMap<String, String> noTorbats = new HashMap<String, String>();
					for (int j = 0; j < idRespWeb.length; j++) {
						boolean respostaTrobada = false;
						for (int z = 1; z < csvBody.get(i).length; z = z+ 2) {
							if (csvBody.get(i)[z].equals(idRespWeb[j])) {
								csvBody.get(i)[z + 1] = relevanceWeb[j];
								respostaTrobada = true;
							}
						}
						
						if (!respostaTrobada) {
							noTorbats.put(idRespWeb[j], relevanceWeb[j]);
						}
					}
					
					if (noTorbats.size() > 0) {
						System.out.println("El tamany de noTrobats es " + noTorbats.size());
						Set<String> keys = noTorbats.keySet();
						Iterator<String> idIterator = keys.iterator();
						String[] line = new String[csvBody.get(i).length + (noTorbats.size() * 2)];
						int pos = csvBody.get(i).length;
						for (int z = 0; z < pos; z++) {
							line[z] = csvBody.get(i)[z];
						}
						
						while (idIterator.hasNext()) {
							String id = idIterator.next();
							line[pos] = id;
							pos++;
							line[pos] = noTorbats.get(id);
							pos++;
						}
						
						csvBody.set(i, line);
					}
				}
				i++;
			}
			
			if (!preguntaTrobada){
				List<String> csvList = new ArrayList<String>();
				csvList.add(preguntaWeb);
				for (int m = 0; m < relevanceWeb.length; m++) {
					csvList.add(idRespWeb[m]);
					csvList.add(relevanceWeb[m]);
				}
				String[] newLine = csvList.toArray(new String[csvList.size()]);
				csvBody.add(newLine);
			}
			
			for (String[] line : csvBody) {
				for (String field : line) {
					csvLine.append("\"" + field + "\",");
				}
				csvLine.delete(csvLine.length() - 1, csvLine.length());
				csvLine.append("\r\n");
			}
			
			baos.write(csvLine.toString().getBytes(Charsets.UTF_8));
			reader.close();
			byte[] bytes = baos.toByteArray();

			InputStream in = new ByteArrayInputStream(bytes);
			db.saveAttachment(in, csvID, contentType, docID, revision);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
		
		return true;
	}
}
