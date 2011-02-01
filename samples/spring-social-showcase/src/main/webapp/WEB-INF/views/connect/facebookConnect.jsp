<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>
<html>
<head>
	<title>Spring Social Showcase: Connect to Facebook</title>
	<script type="text/javascript" src="<c:url value="/resources/jquery/1.4/jquery.js" />"></script>	
	<script>
		function signInWithFacebook() {
			FB.getLoginStatus(function(response) {
		        if (response.session) {
		    		$('#fb_signin').submit();
		        }
		      });
	
		}
	</script>
</head>
<body>
	<h1>Spring Social Showcase: Connect to Facebook</h1>
	
	<form action="<c:url value="/connect/facebook" />" method="POST">
		<input type="hidden" name="scope" value="publish_stream,offline_access" />
		<div class="formInfo">
			<p>Click the button to connect Spring Social Showcase with your Facebook account.</p>
		</div>
		<p><button type="submit"><img src="<c:url value="/resources/social/facebook/connect_light_medium_short.gif" />"/></button></p>
		<label for="postToWall"><input id="postToWall" type="checkbox" name="postToWall" /> Tell your friends about Spring Social Showcase on your Facebook wall</label>
	</form>
	
	<p>...or...</p>
	
	<form id="fb_signin" action="<c:url value="/connect/facebook" />" method="POST">
		<div id="fb-root"></div>	
		<p><fb:login-button perms="email,publish_stream,offline_access" onlogin="signInWithFacebook();" v="2" length="long">Connect to Facebook</fb:login-button></p>
	</form>
	
	<facebook:init apiKey="0b754d95f9c9899b0d6c4454b6f2dde7" />
	
</body>
</html>
