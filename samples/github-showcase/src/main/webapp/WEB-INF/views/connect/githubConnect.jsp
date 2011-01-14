<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>GitHub Showcase: Connect to GitHub</title>
</head>
<body>
<h1>GitHub Showcase: Connect to GitHub</h1>

<form action="<c:url value="/connect/github" />" method="POST">
	<div class="formInfo">
		<p>Click the button to connect GitHub Showcase with your GitHub account.</p>
	</div>
	<p><input type="submit" value="Connect to GitHub"/></p>
</form>


</body>
</html>
