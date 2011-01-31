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

<c:url var="tweetUrl" value="/twitter/tweet" />
<sf:form action="${tweetUrl}" method="post" modelAttribute="tweetForm">
	Tweet using
	<c:if test="${fn:length(connections) eq 1}">
		<b><c:out value="${connections[0]}" /></b>
		<input type="hidden" name="screenName" value="<c:out value="${connections[0]}" />" />
	</c:if>
	<c:if test="${fn:length(connections) gt 1}">
		<sf:select path="screenName">
			<sf:options items="${connections}"/>
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
