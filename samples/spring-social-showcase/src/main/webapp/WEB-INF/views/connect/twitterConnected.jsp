<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>
<html>
<head>
	<title>Spring Social Showcase: Connected to Twitter</title>
</head>
<body>
<h1>Spring Social Showcase: Connected to Twitter</h1>

<form id="disconnect" method="post">
	<div class="formInfo">
		<p>The Spring Social Showcase sample application is already connected to your Twitter account.
			Click the button if you wish to disconnect.			
	</div>

	<button type="submit">Disconnect</button>	
	<input type="hidden" name="_method" value="delete" />
</form>

<p><a href="<s:url value="/" />">Return to home page</a></p>

</body>
</html>
