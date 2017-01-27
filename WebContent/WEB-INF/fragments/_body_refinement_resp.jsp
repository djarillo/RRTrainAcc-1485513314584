<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
		<td style='width: 30%;'></td>
		<td>
			<form action="/Retrieve" method="get">
				<!--<input type=\"text\" name=\"pregunta\" style=\"height: 17px; width: 401px;\">-->
					<textarea name="pregunta" rows="3" cols="48"></textarea>
					<input type="submit" value="Submit">
				</form>
			</td>
		</tr>
		<tr>
			<td style='width: 30%;'>
			<img class = "newappIcon" src='/resources/images/newapp-icon.png'>
		</td>
		<td>
			<p class='description'></p><span class="blue">${rrResp.rrPregunta.pregunta}</span>
		</td>
	</tr>
</table>
<table border="1">
	<tr>
		<td>
			<form id="questionrank" method="get">
				<c:forEach var="respId" items="${rrResp.rrResponse}" varStatus="counter">
					<tr>
						<td style='width: 70%;'>
							<b><c:out value='${respId.title}'/>: </b>${respId.body}
						</td>
						<td>
							<table>
								<tr>
									<td colspan="5">
										<p>How accurate is the answer?</p>
									</td>
								</tr>
								<tr>
									<td>
										<input type="radio" name="rank_${counter.index}" value="1">1
									</td>
									<td>
										<input type="radio" name="rank_${counter.index}" value="2">2
									</td>
									<td>
										<input type="radio" name="rank_${counter.index}" value="3">3
									</td>
									<td>
										<input type="radio" name="rank_${counter.index}" value="4">4
									</td>
									<td>
										<input type="radio" name="rank_${counter.index}" value="5">5
									</td>
									<td>
										<input type="hidden" name="idResposta_${counter.index}" id="idResposta_${counter.index}" value="${respId.id}">
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</c:forEach>
				<input type="hidden" name="pregunta" id="pregunta" value="${rrResp.rrPregunta.pregunta}">
				<input type="button" id="submit" value="Submit">
			</form>
		</td>