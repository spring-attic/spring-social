<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Facebook Showcase: Connect to Facebook</title>
</head>
<body>
<h1>Facebook Showcase: Connect to Facebook</h1>

<form action="<c:url value="/connect/facebook" />" method="POST">
	<div class="formInfo">
		<p>Click the button to connect Facebook Showcase with your Facebook account.</p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/social/facebook/connect_light_medium_short.gif" />"/></button></p>
</form>


</body>
</html>
