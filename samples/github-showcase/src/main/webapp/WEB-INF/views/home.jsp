<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>GitHub Showcase</title>
</head>
<body>
	<h1>GitHub Showcase</h1>

	<p>Hello, <c:out value="${gitHubId}"/>!  (<a href="<c:url value="/connect/github"/>">Disconnect from GitHub</a>)</p>
	
	<h3>GitHub User Profile:</h3>
	<ul>
		<li>ID: <c:out value="${gitHubUser.id}"/></li>
		<li>Username: <c:out value="${gitHubUser.username}"/></li>
		<li>Name: <c:out value="${gitHubUser.name}"/></li>
		<li>Company: <c:out value="${gitHubUser.company}"/></li>
		<li>Email: <c:out value="${gitHubUser.email}"/></li>
		<li>Blog: <c:out value="${gitHubUser.blog}"/></li>
	</ul>
</body>
</html>
