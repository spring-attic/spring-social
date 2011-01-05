<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Twitter Showcase: Send a Tweet</title>
</head>
<body>
<h1>Twitter Showcase: Send a Tweet</h1>

<sf:form action="tweet" method="post" modelAttribute="tweetForm">
	Tweet using
	<c:if test="${fn:length(accountConnectionList) eq 1}">
		<b><c:out value="${accountConnectionList[0].providerAccountId}" /></b>
		<input type="hidden" name="screenName" value="<c:out value="${accountConnectionList[0].providerAccountId}" />" />
	</c:if>
	<c:if test="${fn:length(accountConnectionList) gt 1}">
		<sf:select path="screenName">
			<sf:options items="${accountConnectionList}" itemValue="providerAccountId" itemLabel="providerAccountId"/>
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
