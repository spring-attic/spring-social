<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Twitter Sample</title>
</head>
<body>
<h1>Twitter Sample</h1>

<c:if test="${empty accountConnectionList}">
<p>You don't have any Twitter connections (<a href="connect/twitter">Create one</a>).</p>
</c:if>
<c:if test="${not empty accountConnectionList}">
<ul>
<p>Here are your Twitter connections (by screen name):</p>
<c:forEach var="connection" items="${accountConnectionList}">
	<li><c:out value="${connection.providerAccountId}" /> (<a href="disconnect?connection=${connection.providerAccountId}">disconnect</a>)</li>
</c:forEach>
</ul>
<a href="connect/twitter">Create another one</a> | <a href="tweet">Send a Tweet</a>
</c:if>


</body>
</html>
