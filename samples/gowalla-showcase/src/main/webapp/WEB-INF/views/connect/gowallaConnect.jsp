<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Gowalla Showcase: Connect to Gowalla</title>
</head>
<body>
<h1>Gowalla Showcase: Connect to Gowalla</h1>

<form action="<c:url value="/connect/gowalla" />" method="POST">
	<div class="formInfo">
		<p>Click the button to connect Gowalla Showcase with your Gowalla account.</p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/social/gowalla/button-gowalla_connect-156ool.png" />"/></button></p>
</form>


</body>
</html>
