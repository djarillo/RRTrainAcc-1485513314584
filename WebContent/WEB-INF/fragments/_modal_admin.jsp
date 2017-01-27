<div id="adminModal" class="modal">
	<div class="modal-content">
		<div class="modal-header">
			<span class="close" onclick="$('#adminModal').hide()">x</span>
			<h2>Cluster Information:</h2>
		</div>
		<div class="modal-body">
			<form action="/CreateCluster" method="get">
				<input type="text" name="clusterName">
				<input type="submit" id="clustData" value="Create Cluster">
			</form>
		</div>
	</div>
</div>
<div id="configUpLoad" class="modal">
	<div class="modal-content">
		<div class="modal-header">
			<span class="close" onclick="$('#configUpLoad').hide()">x</span>
			<h2>Configuration Information:</h2>
		</div>
		<div class="modal-body">
			<form action="/uploadConfig" method="post" enctype="multipart/form-data">
				<table>
					<tr>
						<td>
							Name: <input type="text" name="configName">
						</td>
						<td>
							File: <input type="file" name="file">
						</td>
					</tr>
				</table>
				<input type="hidden" name="clusterId" id="clusterId" value="${vcapRR.solrClusts[0].clusterId}">
				<input type="submit" id="submitConfig" value="Upload Configuration">
			</form>
		</div>
	</div>
</div>
<div id="collectionModal" class="modal">
	<div class="modal-content">
		<div class="modal-header">
			<span class="close" onclick="$('#collectionModal').hide()">x</span>
			<h2>Collection Informatrion:</h2>
		</div>
		<div class="modal-body">
			<form action="/createColl" method="get">
				<table>
					<tr>
						<td>
							Collection Name: <input type="text" name="collectionName">
						</td>
					</tr>
				</table>
				<input type="hidden" name="clusterId" id="clusterId" value="${vcapRR.solrClusts[0].clusterId}">
				<input type="hidden" name="configName" id="configName" value="${vcapRR.solrClusts[0].configName[0]}">
				<input type="submit" id="submitColl" value="Create Collection">
			</form>
		</div>
	</div>
</div>
<div id="indexDocuments" class="modal">
	<div class="modal-content">
		<div class="modal-header">
			<span class="close" onclick="$('#indexDocuments').hide()">x</span>
			<h2>Select Documents:</h2>
		</div>
		<div class="modal-body">
			<form action="/indexDoc" method="post" enctype="multipart/form-data">
				<table>
					<tr>
						<td>
							File: <input type="file" name="docFile">
						</td>
					</tr>
				</table>
				<input type="hidden" name="clusterId" id="clusterId" value="${vcapRR.solrClusts[0].clusterId}">
				<input type="hidden" name="collectionName" id="collectionName" value="${vcapRR.solrClusts[0].collectionName[0]}">
				<input type="submit" id="submitIndex" value="Index Document">
			</form>
		</div>
	</div>
</div>
<div id="creationRanker" class="modal">
	<div class="modal-content">
		<div class="modal-header">
			<span class="close" onclick="$('#creationRanker').hide()">x</span>
			<h2>Ranker Information:</h2>
		</div>
		<div class="modal-body">
			<form action="/createRank" method="post" enctype="multipart/form-data">
				<table>
					<tr>
						<td>
							Name: <input type="text" name="rankerName">
						</td>
						<td>
							File: <input type="file" name="fileRank">
						</td>
					</tr>
				</table>
				<input type="submit" id="submitRanker" value="Create Ranker">
			</form>
		</div>
	</div>
</div>