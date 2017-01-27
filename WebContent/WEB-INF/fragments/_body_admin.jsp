<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<td style='width: 30%;'><img class = "newappIcon" src='/resources/images/newapp-icon.png'>
</td>
<td>
	<!-- <form action="/CreateCluster" method="get"> -->
		<input type="button" id="createCluster" value="Create Cluster" onclick="$('#adminModal').modal();">
	<!-- </form> -->
</td>
<td>
	<input type="button" id="createRanker" value="Create Ranker" onclick="$('#creationRanker').modal();">
</td>
<div>
	<table>
		<c:forEach var="cluster" items="${vcapRR.solrClusts}">
			<tr>
				<td>
					
					SolrClusterId: ${cluster.clusterId}
				</td>
				<td>
					<form id="clusterStat" method="get">
						
						<input type="hidden" name="clusterId" id="clusterId" value="${cluster.clusterId}">
						<input type="button" id="submitCluStat" value="Refresh Status">
					</form>
				</td>
				<td>
					<input type="button" id="openConfig" value="Upload Configuration" onclick="$('#configUpLoad').modal()">
				</td>
				<td>
					<input type="button" id="submitCollection" value="Create Collection" onclick="$('#collectionModal').modal()">
				</td>
				<td>
					<input type="button" id="submitIndex" value="Index Document" onclick="$('#indexDocuments').modal()">
				</td>
				<td>
					<form action="/DelCluster" method="get">
						
						<input type="hidden" name="clusterId" id="clusterId" value="${cluster.clusterId}">
						<input type="submit" id="submitDelClu" value="Delete Cluster">
					</form>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					Name: ${cluster.name}
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					Size: ${cluster.size}
				</td>
			</tr>
			<tr>
				<td></td>
				<td id="statusClust">
					Status: ${cluster.status}
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					Configuration Name: 
					<c:forEach var="configuration" items="${cluster.configName}">
						${configuration}
					</c:forEach>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					Collection Name:
					<c:forEach var="collection" items="${cluster.collectionName}">
						${collection}
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
		<c:forEach var="rank" items="${vcapRR.rankers}">
			<tr>
				<td>
					RankerId: ${rank.id}
				</td>
				<td>
					<form id="rankStat" method="get">
						<input type="hidden" name="rankerId" id="rankerId" value="${rank.id}">
						<input type="button" id="submitStat" value="Refresh Status">
					</form>
				</td>
				<td>
					<form action="/delRanker" method="get">
						<input type="hidden" name="rankerId" id="rankerId" value="${rank.id}">
						<input type="submit" id="submitDel" value="Delete Ranker">
					</form>
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					Name: ${rank.name}
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					Created: ${rank.created}
				</td>
			</tr>
			<tr>
				<td></td>
				<td id="statusRank">
					Status: ${rank.status}
				</td>
			</tr>
			<tr>
				<td></td>
				<td id="stausDesc">
					Status Description: ${rank.statusDescription}
				</td>
			</tr>
			<tr>
				<td></td>
				<td>
					URL: ${rank.url}
				</td>
			</tr>
		</c:forEach>
	</table>
</div>