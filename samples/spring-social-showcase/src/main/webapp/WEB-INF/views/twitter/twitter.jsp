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
<c:forEach items="${connectedProfiles}" var="profile">
	<div>
		<p><a href="${profile.profileUrl}" target="_blank"><img src="${profile.profileImageUrl}" border="0"/></a></p>
		<p><c:out value="${profile.name}"/> (<c:out value="${profile.screenName}"/>)</p>
	</div>
</c:forEach>

<c:url value="/connect/twitter" var="disconnectUrl"/>
<form id="disconnect" action="${disconnectUrl}" method="post">
	<button type="submit">Disconnect from all</button>	
	<input type="hidden" name="_method" value="delete" />
</form>

<form action="<c:url value="/connect/twitter" />" method="POST">
	<p>You may connect multiple Twitter profiles with a Spring Social Showcase account. To connect with another
	Twitter profile, click the button.</p>
	<p>(Note: If you are still logged into Twitter as any one of the profiles that are already connected, you'll need to
	click the "Sign Out" link when Twitter prompts you to allow access to Spring Social Showcase and then login as a
	different Twitter user.)</p>
	<p><button type="submit">Connect another Twitter profile</button></p>
	<label for="postTweet"><input id="postTweet" type="checkbox" name="postTweet" /> Post a tweet about connecting with Spring Social Showcase</label>
</form>

<c:url var="tweetUrl" value="/twitter/tweet" />
<sf:form action="${tweetUrl}" method="post" modelAttribute="tweetForm">
	Tweet as
	<c:if test="${fn:length(connectedProfiles) eq 1}">
		<b><c:out value="${connectedProfiles[0].screenName}" /></b>
		<input type="hidden" name="screenName" value="<c:out value="${connectedProfiles[0].screenName}" />" />
	</c:if>
	<c:if test="${fn:length(connectedProfiles) gt 1}">
		<sf:select path="screenName">
			<sf:options items="${connectedProfiles}" itemLabel="name" itemValue="screenName" />
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
