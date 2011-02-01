<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Spring Social Showcase: Send a Tweet</title>
</head>
<body>
<h1>Spring Social Showcase: Send a Tweet</h1>

<p>Your Spring Social Showcase account is connected to the following Twitter profiles:</p>
<ul>
	<c:forEach items="${connectedProfiles}" var="profile">
		<li><img src="${profile.profileImageUrl}"/> <c:out value="${profile.name}"/> (<c:out value="${profile.screenName}"/>)</li>
	</c:forEach>
</ul>

<c:url value="/connect/twitter" var="disconnectUrl"/>
<form id="disconnect" action="${disconnectUrl}" method="post">
	<button type="submit">Disconnect from all</button>	
	<input type="hidden" name="_method" value="delete" />
</form>

<c:url var="tweetUrl" value="/twitter/tweet" />
<sf:form action="${tweetUrl}" method="post" modelAttribute="tweetForm">
	Tweet as
	<c:if test="${fn:length(connectedProfiles) eq 1}">
		<b><c:out value="${connectedProfiles[0].screenName}" /></b>
		<input type="hidden" name="screenName" value="<c:out value="${connectedProfiles[0].screenName}" />" />
	</c:if>
	<c:if test="${fn:length(connections) gt 1}">
		<sf:select path="screenName">
			<sf:options items="${connectedProfiles[0]}" itemLabel="screenName" itemValue="screenName" />
		</sf:select>
		<sf:checkbox path="tweetToAll" label="Tweet to all"/>
	</c:if>
	<br/>
	<a href="<c:url value="/connect/twitter"/>">Add a new connection</a>
	<br/>
	<sf:textarea path="message" rows="5" cols="80"/><br/>
	<input type="submit" value="Send Tweet"/>
</sf:form>

</body>
</html>
