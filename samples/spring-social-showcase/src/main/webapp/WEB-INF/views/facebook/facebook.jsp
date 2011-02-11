<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Spring Social Showcase: Facebook</title>
</head>
<body>
	<h1>Spring Social Showcase: Facebook</h1>

	<p>Hello, <c:out value="${fbUser.firstName}"/>!  (<a href="<c:url value="/connect/facebook"/>">Disconnect from Facebook</a>)</p>
	
	<p>Your Facebook profile:</p>
	<dl>
		<dt>Facebook ID:</dt>
		<dd><c:out value="${fbUser.id}"/></dd>
		<dt>Name:</dt>
		<dd><c:out value="${fbUser.name}"/></dd>
		<dt>Email:</dt>
		<dd><c:out value="${email}"/></dd>
	</dl>
	<hr/>
	<form method="POST" action="<c:url value="/facebook/wall" />">
		<p>Post to your Facebook wall:<p>
		<textarea id="message" name="message" rows="5" cols="40"></textarea><br/>
		<input type="submit" value="Post" />
	</form>

</body>
</html>
