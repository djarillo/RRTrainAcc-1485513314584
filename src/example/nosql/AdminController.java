package example.nosql;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.RetrieveAndRank;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.Ranker;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrCluster;
import com.ibm.watson.developer_cloud.retrieve_and_rank.v1.model.SolrClusterOptions;

import example.nosql.constants.Constants;
import example.nosql.model.ClusterForm;
import example.nosql.model.RankerForm;
import example.nosql.service.VcapServices;
import example.nosql.utils.HttpSolrClientUtils;

@Controller
public class AdminController {

	private VcapServices vcap;
	
	@RequestMapping(value = "/Admin", method = RequestMethod.GET)
	public String initAdmin(ModelMap model) {
		vcap = VcapServices.getVcapServices();
		model.addAttribute("vcapRR", vcap.getRrCred());
		
		return "admin";
	}
	
	@ResponseBody
	@RequestMapping(value="/RankStat", method = RequestMethod.GET)
	public String getRankerStatus(@Valid RankerForm ranker, BindingResult result, ModelMap model){
		RetrieveAndRank service = new RetrieveAndRank();
		Ranker rank;
		
		vcap = VcapServices.getVcapServices();
		
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		
		rank = service.getRankerStatus(ranker.getRanker());
		
		return rank.getStatus().toString() + "," + rank.getStatusDescription().toString();
	}
	
	@RequestMapping(value="/createRank", method = RequestMethod.POST)
	private String createRanker(@RequestParam("rankerName") String rankerName, @RequestParam("fileRank") MultipartFile fileRank, ModelMap model) {
		RetrieveAndRank service = new RetrieveAndRank();
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		
		//File trainFile = new File("C:\\Users\\josep.marco.selma\\Documents\\IBM\\Bluemix\\Watson\\rr_test.csv");
		
		File trainFile = new File(fileRank.getOriginalFilename());
		
		if (!fileRank.isEmpty()) {
			try {
				fileRank.transferTo(trainFile);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@SuppressWarnings("unused")
		Ranker rank = service.createRanker(rankerName, trainFile);
		
		vcap.modifyVcapServices(Constants.RANKER_OP_STR);
		
		model.addAttribute("vcapRR", vcap.getRrCred());
		
		return "admin";
	}
	
	@ResponseBody
	@RequestMapping(value="/ClustStat", method = RequestMethod.GET)
	public String getClusterStatus(@Valid ClusterForm cluster, BindingResult result, ModelMap model) {
		RetrieveAndRank service = new RetrieveAndRank();
		SolrCluster clust;
						
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		
		clust = service.getSolrCluster(cluster.getCluster());
		
		return clust.getStatus().toString();
	}
	
	@RequestMapping(value="/CreateCluster", method = RequestMethod.GET)
	public String createCluster(@RequestParam("clusterName") String clusterName, ModelMap model) {
		RetrieveAndRank service = new RetrieveAndRank();
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		
		SolrClusterOptions options = new SolrClusterOptions(clusterName, "1");
		service.createSolrCluster(options);
		
		vcap.modifyVcapServices(Constants.CLUSTER_OP_STR);
		
		model.addAttribute("vcapRR", vcap.getRrCred());

		return "admin";
	}
	
	@RequestMapping(value="/DelCluster", method = RequestMethod.GET)
	public String deleteCluster(@RequestParam("clusterId") String clusterId, ModelMap model) {
		RetrieveAndRank service = new RetrieveAndRank();
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		
		service.deleteSolrCluster(clusterId);
		
		vcap.modifyVcapServices(Constants.CLUSTER_OP_STR);
		
		model.addAttribute("vcapRR", vcap.getRrCred());
		
		return "admin";
	}
	
	@RequestMapping(value="/delRanker", method = RequestMethod.GET)
	public String deleteRanker(@RequestParam("rankerId") String rankerId, ModelMap model) {
		RetrieveAndRank service = new RetrieveAndRank();
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		
		service.deleteRanker(rankerId);
		
		vcap.modifyVcapServices(Constants.RANKER_OP_STR);
		
		model.addAttribute("vcapRR", vcap.getRrCred());
		
		return "admin";
	}
	
	@RequestMapping(value="/uploadConfig", method = RequestMethod.POST)
	public String uploadConfig(@RequestParam("clusterId") String clusterId, @RequestParam("configName") String configName, @RequestParam("file") MultipartFile file, ModelMap model) {
		RetrieveAndRank service = new RetrieveAndRank();
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		
		File configFile = new File(file.getOriginalFilename());
		
		if (!file.isEmpty()) {
			try {
				file.transferTo(configFile);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		service.uploadSolrClusterConfigurationZip(clusterId, configName, configFile);
		
		vcap.modifyVcapServices(Constants.CLUSTER_OP_STR);
		
		model.addAttribute("vcapRR", vcap.getRrCred());
		
		return "admin";
	}
	
	@RequestMapping(value="/createColl", method = RequestMethod.GET)
	public String createCollection(@RequestParam("clusterId") String clusterId, @RequestParam("configName") String configName, @RequestParam("collectionName") String collectionName, ModelMap model) {
		RetrieveAndRank service = new RetrieveAndRank();
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		//HttpSolrClient client = getSolrClient(service, clusterId, service.getSolrUrl(clusterId),vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());
		HttpSolrClient client = new HttpSolrClient(service.getSolrUrl(clusterId), HttpSolrClientUtils.createHttpClient(vcap.getRrCred().getUrl(), vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword()));
		
		CollectionAdminRequest.Create createCollectionRequest = new CollectionAdminRequest.Create();
		
		createCollectionRequest.setCollectionName(collectionName);
		createCollectionRequest.setConfigName(configName);

		try {
			@SuppressWarnings("unused")
			CollectionAdminResponse response = createCollectionRequest.process(client);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		vcap.modifyVcapServices(Constants.CLUSTER_OP_STR);
		
		model.addAttribute("vcapRR", vcap.getRrCred());
		
		return "admin";
	}
	
	@RequestMapping(value="/indexDoc", method = RequestMethod.POST)
	public String addDocuments(@RequestParam("clusterId") String clusterId, @RequestParam("docFile") MultipartFile docFile, @RequestParam("collectionName") String collectionName, ModelMap model) {
		@SuppressWarnings("unused")
		UpdateResponse addResponse;
		ByteArrayInputStream stream;
		String myString = "";
		RetrieveAndRank service = new RetrieveAndRank();
		service.setUsernameAndPassword(vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword());

		try {
			stream = new ByteArrayInputStream(docFile.getBytes());
			myString = IOUtils.toString(stream, "UTF-8");
			myString.length();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HttpSolrClient client = new HttpSolrClient(service.getSolrUrl(clusterId), HttpSolrClientUtils.createHttpClient(vcap.getRrCred().getUrl(), vcap.getRrCred().getUsername(), vcap.getRrCred().getPassword()));
		Collection<SolrInputDocument> docList = parseDocument(myString);
		
		try {
			addResponse = client.add(collectionName, docList);
			client.commit(collectionName);
			client.close();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		model.addAttribute("vcapRR", vcap.getRrCred());
		
		return "admin";
	}
	
	public Collection<SolrInputDocument> parseDocument(String jsonString) {
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		JsonParser parser  = new JsonParser();
		SolrInputDocument newDoc;
		
		try {
			JsonElement docsElement = parser.parse(jsonString);
			
			JsonObject docsObject = docsElement.getAsJsonObject();
			JsonArray docsArray = docsObject.getAsJsonArray("add");			
			
			for (int i = 0; i < docsArray.size(); i++) {
				newDoc = new SolrInputDocument();
				
				JsonObject docObject = (JsonObject) docsArray.get(i);
				JsonElement docElement = docObject.get("doc");
				JsonObject docuObject = docElement.getAsJsonObject();
				JsonElement id = docuObject.get(Constants.ID_STR);
				JsonElement body = docuObject.get(Constants.BODY_STR);
				JsonElement title = docuObject.get(Constants.TITLE_STR);
				
				newDoc.addField(Constants.ID_STR, id.getAsInt());
				newDoc.addField(Constants.BODY_STR, body.getAsString());
				newDoc.addField(Constants.TITLE_STR, title.getAsString());
				docs.add(newDoc);
			}
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return docs;
	}
}
