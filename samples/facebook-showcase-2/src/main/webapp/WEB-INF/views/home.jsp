<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Facebook Showcase</title>
</head>
<body>
	<h1>Facebook Showcase</h1>

	<p>Hello, <c:out value="${fbUser.firstName}"/>!  (<a href="<c:url value="/connect/facebook"/>">Disconnect from Facebook</a>)</p>
	
	<h3>Post to your Facebook wall</h3>	

	<p>Note that this currently won't work in this version of the showcase (you'll get a '403 Forbidden' error) 
	because right now there's no way to specify scope when authorizing with via Spring Social's OAuth 2 web server
	flow in ConnectController. Consequently, after authorization, the showcase is only granted read access to the
	user's public profile information. Consider this a TODO item.</p>

	<form method="POST" action="<c:url value="/wall"/>">
		<textarea id="message" name="message" rows="5" cols="40"></textarea><br/>
		<input type="submit" value="Post" />
	</form>
</body>
</html>
