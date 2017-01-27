<%@ page import="java.util.List, example.nosql.model.Coordenada" %>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawBackgroundColor);
	
	function drawBackgroundColor() {
		var data = new google.visualization.DataTable();
		var rows = []; 
		var row = [];
		<% 	@SuppressWarnings("unchecked")
			List<Coordenada> coordList = (List<Coordenada>)request.getAttribute("coordenadas");
			for (int i = 0; i < coordList.size(); i++) {%>
				row = new Array(<%=coordList.get(i).getxCoord()%>, <%=coordList.get(i).getyCoord()%>);
				rows[<%=i%>] = row;
			<%}%>

		data.addColumn('number', 'X');
		data.addColumn('number', 'Accuracy');
		
		data.addRows(
		              rows
		);
		
		var options = {
				hAxis: {
					title:	'Iteration'
				},
				vAxis: {
					title:	'%'
				},
				backgroundColor:	'#f1f8e9'
		};
		
		var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
		chart.draw(data, options);
	}
</script>