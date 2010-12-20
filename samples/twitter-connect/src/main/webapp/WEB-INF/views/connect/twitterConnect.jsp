<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<form action="<c:url value="/connect/twitter" />" method="POST">
	<div class="formInfo">
		<h2>Connect to Twitter</h2>
		<p>Click the button to connect the Twitter Sample with your Twitter account.</p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/social/twitter/signin.png" />"/></button></p>
</form>