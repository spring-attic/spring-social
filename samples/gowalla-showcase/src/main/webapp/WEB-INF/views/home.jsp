<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Gowalla Showcase</title>
</head>
<body>
	<h1>Gowalla Showcase</h1>

	<p>Hello, <c:out value="${gowallaId}"/>!  (<a href="<c:url value="/connect/gowalla"/>">Disconnect from Gowalla</a>)</p>
	
	<h3>Top Checkins</h3>
	<c:forEach items="${topCheckins}" var="checkin">
		<li><c:out value="${checkin.name}"/> (<c:out value="${checkin.count}" />)</li>
	</c:forEach>
	
</body>
</html>
