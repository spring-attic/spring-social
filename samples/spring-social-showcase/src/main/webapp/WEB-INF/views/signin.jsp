<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>Spring Social Showcase: Sign In</title>
</head>
<body>
<h1>Spring Social Showcase: Sign In</h1>

<form id="signin" action="<c:url value="/signin/authenticate" />" method="post">
	<div class="formInfo">
  		<h2>Spring Social Showcase Sign In</h2>
  		<c:if test="${signinError}">
  		<div class="error">
  			Your sign in information was incorrect.
  			Please try again or <a href="<c:url value="/signup" />">sign up</a>.
  		</div>
 	 	</c:if>
	</div>
	<fieldset>
		<label for="login">Username</label>
		<input id="login" name="j_username" type="text" size="25" <c:if test="${not empty signinErrorMessage}">value="${SPRING_SECURITY_LAST_USERNAME}"</c:if> />
		<label for="password">Password</label>
		<input id="password" name="j_password" type="password" size="25" />	
	</fieldset>
	<p><button type="submit">Sign In</button></p>
	
	<p>Some test user/password pairs you may use are:</p>
	<ul>
		<li>habuma/habuma</li>
		<li>kdonald/kdonald</li>
		<li>rclarkson/rclarkson</li>
	</ul>
</form>

<form action="<c:url value="/connect/twitter/signin" />" method="post">
	<button type="submit">Sign In With Twitter</button>
</form>

<form action="<c:url value="/connect/facebook/signin" />" method="post">
	<button type="submit">Sign In With Facebook</button>
</form>
</body>
</html>
