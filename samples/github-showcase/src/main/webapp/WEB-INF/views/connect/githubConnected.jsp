<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>
<html>
<head>
	<title>GitHub Showcase: Connected to GitHub</title>
</head>
<body>
<h1>GitHub Showcase: Connected to GitHub</h1>

<c:if test="${not empty message}">
<div class="${message.type.cssClass}">${message.text}</div>
</c:if>

<form id="disconnect" method="post">
	<div class="formInfo">
		<p>
			GitHub Showcase is connected to your GitHub account.
			Click the button if you wish to disconnect.
		</p>
	</div>
	
	<!-- TODO: FIX THE DISCONNECT BUTTON -->
	<button type="submit" onclick="FB.logout(function(response) { return true; } );">Disconnect</button>	
	<input type="hidden" name="_method" value="delete" />
</form>

<a href="<c:url value="/"/>">Return to home page</a>

</body>
</html>
