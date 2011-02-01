<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Spring Social Showcase: Connect to Twitter</title>
</head>
<body>
<h1>Spring Social Showcase: Connect to Twitter</h1>

<form action="<c:url value="/connect/twitter" />" method="POST">
	<div class="formInfo">
		<p>You haven't created any connections with Twitter yet. Click the button to connect Spring Social Showcase with your Twitter account. 
		(You'll be redirected to Twitter where you'll be asked to authorize the connection.)</p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/social/twitter/signin.png" />"/></button></p>
	<label for="postTweet"><input id="postTweet" type="checkbox" name="postTweet" /> Post a tweet about connecting with Spring Social Showcase</label>
</form>


</body>
</html>
