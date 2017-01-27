<script src="/resources/js/jquery.modal.js" type="text/javascript" charset="utf-8"></script>
<script>
	$(document).ready(function(){
		$('#submit').click(function(event){
			var question = $('#pregunta').val();
			var ranking = [];
			var idPreg = [];
			for(i = 0; i < 10; i++) {
				if ($('input[name=rank_'+i+']').is(':checked')) {
					ranking.push($('input[name=rank_'+i+']:checked').val());
					idPreg.push($('#idResposta_'+i).val());
				}
			}
			$.get('Questions', {preg:question, rr:ranking.toString(), id:idPreg.toString()}, function(responseText){
				$('#myModal').html(responseText).modal();
			});
		});
	});
</script>