<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<html>
<head>
	<title>Facebook Showcase: Connect to Facebook</title>
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

<h1>Facebook Showcase: Connect to Facebook</h1>

<form id="fb_signin" action="<c:url value="/connect/facebook" />" method="post">
	<div class="formInfo">
		<p>Click the button to connect Facebook Showcase with your Facebook account.</p>
	</div>
	<div id="fb-root"></div>	
	<p><fb:login-button perms="email,publish_stream,offline_access" onlogin="signInWithFacebook();" v="2" length="long">Connect to Facebook</fb:login-button></p>
</form>

<facebook:init />

</body>
</html>
