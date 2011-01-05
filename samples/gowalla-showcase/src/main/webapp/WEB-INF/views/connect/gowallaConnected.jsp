<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>
<html>
<head>
	<title>Gowalla Showcase: Connected to Gowalla</title>
</head>
<body>
<h1>Gowalla Showcase: Connected to Gowalla</h1>

<c:if test="${not empty message}">
<div class="${message.type.cssClass}">${message.text}</div>
</c:if>

<form id="disconnect" method="post">
	<div class="formInfo">
		<p>
			Gowalla Showcase is connected to your Gowalla account.
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
