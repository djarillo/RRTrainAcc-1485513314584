<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title><tiles:getAsString name="title"/></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script src="http://code.jquery.com/jquery-latest.js"></script>
		<link rel="stylesheet" href="/resources/css/style.css">
		<tiles:insertAttribute name="head"/>
	</head>
	<body>
		<table>
			<tr>
				<td colspan="2"><tiles:insertAttribute name="header"/></td>
			</tr>
			<tr>
				<tiles:insertAttribute name="body"/>
			</tr>
		</table>
		<tiles:insertAttribute name="modal"/>
	</body>
</html>