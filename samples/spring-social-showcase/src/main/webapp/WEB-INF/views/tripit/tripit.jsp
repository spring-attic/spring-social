<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Spring Social Showcase: TripIt</title>
</head>
<body>
<h1>Spring Social Showcase: TripIt</h1>

<p>Hello <c:out value="${tripItUser.publicDisplayName}"/>!</p>
<p>Your TripIt profile:</p>
<ul>
	<li>Screen name: <c:out value="${tripItUser.screenName}"/></li>
	<li>Home city: <c:out value="${tripItUser.homeCity}"/></li>
	<li>Company: <c:out value="${tripItUser.company}"/></li>
</ul>

<p>Your upcoming trips:</p>
<ul>
<c:forEach items="${trips}" var="trip">
	<li>"<c:out value="${trip.displayName}"/>" - Destination: <c:out value="${trip.primaryLocation}"/>; 
	    <fmt:formatDate value="${trip.startDate}" dateStyle="full"/> to <fmt:formatDate value="${trip.endDate}" dateStyle="full"/></li>
</c:forEach>
</ul>

<c:url value="/connect/tripit" var="disconnectUrl"/>
<form id="disconnect" action="${disconnectUrl}" method="post">
	<button type="submit">Disconnect from TripIt</button>	
	<input type="hidden" name="_method" value="delete" />
</form>

</body>
</html>
