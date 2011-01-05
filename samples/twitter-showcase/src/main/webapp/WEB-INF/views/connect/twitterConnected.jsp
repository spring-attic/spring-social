<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>
<html>
<head>
	<title>Twitter Showcase: Connected to Twitter</title>
</head>
<body>
<h1>Twitter Showcase: Connected to Twitter</h1>

<c:if test="${not empty message}">
<div class="${message.type.cssClass}">${message.text}</div>
</c:if>

<form action="<c:url value="/connect/twitter" />" method="POST">
	<div class="formInfo">
		<p>The Twitter Showcase sample application is already connected to your Twitter account.
			Click the button to create another connection with Twitter.</p>
			
		<p><a href="<s:url value="/" />">Return to home page</a></p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/social/twitter/signin.png" />"/></button></p>
</form>

</body>
</html>
