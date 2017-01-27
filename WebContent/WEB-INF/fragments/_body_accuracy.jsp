<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<td style='width: 30%;'><img class="newappIcon" src="/resources/images/newapp-icon.png">
</td>
<td>
	<form action="/accuracyTest" method="post" enctype="multipart/form-data">
		<!-- <table>
			<tr>
				<td>
					File: <input type="file" name="fileAccuracy">
				</td>
				<td>
					Ranker: <select>
								<c:forEach var="rank" items="${rrCredAccuracy}">
									<option value=""></option>
								</c:forEach>
							</select>
				</td>
			</tr>
		</table> -->
		<input type="submit" id="submitTest" value="Accuracy Test">
	</form>
</td>