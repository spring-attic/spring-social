<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Twitter Sample: Tweet</title>
</head>
<body>
<h1>Twitter Sample: Tweet</h1>

<c:if test="${empty accountConnectionList}">
<p>You don't have any Twitter connections (<a href="connect/twitter">Create one</a>).</p>
</c:if>

<c:if test="${not empty accountConnectionList}">
<sf:form method="post" modelAttribute="tweetForm">
	Tweet using
	<c:if test="${fn:length(accountConnectionList) eq 1}">
		<b><c:out value="${accountConnectionList[0].providerAccountId}" /></b>
	</c:if>
	<c:if test="${fn:length(accountConnectionList) gt 1}">
		<sf:select path="screenName">
			<sf:options items="${accountConnectionList}" itemValue="providerAccountId" itemLabel="providerAccountId"/>
		</sf:select>
		<sf:checkbox path="tweetToAll" label="Tweet to all"/>
	</c:if>
	<br/>
	<sf:textarea path="message" rows="5" cols="80"/><br/>
	<input type="submit" value="Send Tweet"/>
</sf:form>
</c:if>


</body>
</html>
