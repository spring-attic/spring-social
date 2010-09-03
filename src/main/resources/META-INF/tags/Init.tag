<%@ attribute name="apiKey" required="true" %>
<div id='fb-root'></div>
<script src='http://connect.facebook.net/en_US/all.js'></script>
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
