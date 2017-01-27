<script src="/resources/js/jquery.modal.js" type="text/javascript" charset="utf-8"></script>
<script>
	$(document).ready(function(){
		$('#submitStat').click(function(event){
			var rankId = $('#rankerId').val();
			
			$.get('RankStat', {ranker:rankId}, function(responseText){
				var stat = responseText.split(",");
				$('#statusRank').html('Status: ' + stat[0]);
				$('#stausDesc').html('Status Description: ' + stat[1]);
			});
		});
		$('#submitCluStat').click(function(event){
			var clustId = $('#clusterId').val();
			
			$.get('ClustStat', {cluster:clustId}, function(responseText){
				$('#statusClust').html('Status: ' + responseText);
			});
		});
	});
</script>