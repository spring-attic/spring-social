<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="apiKey" required="false" %>

<c:if test="${empty apiKey}">
	<s:eval expression="@facebookProvider.apiKey" var="apiKey" />
</c:if>

<div id='fb-root'></div>
<c:choose>
	<c:when test="${pageContext.request.secure}">
		<script src='https://connect.facebook.net/en_US/all.js'></script>
	</c:when>
	<c:when test="${not pageContext.request.secure}">
		<script src='http://connect.facebook.net/en_US/all.js'></script>
	</c:when>
</c:choose>
<script>
if(FB) {
	FB.requireSessionThenGoTo = function(url) {
		FB.getLoginStatus(function(response) {
			if (response.session) { 
				window.location = url; 
			} else {
				FB.login(function(response) {
					if (response.session) {
						window.location = url;
					}
				});
			}
		});
	};
	
	FB.logoutThenGoTo = function(url) {
		FB.logout(function(response) { 
			window.location = url; 
		});
	};
}
</script>
<script type='text/javascript'>	
	FB.init({appId: '${apiKey}', status: true, cookie: true, xfbml: true});
	FB.Event.subscribe('auth.sessionChange', function(response) { if (response.session) {} else {} });
</script>
