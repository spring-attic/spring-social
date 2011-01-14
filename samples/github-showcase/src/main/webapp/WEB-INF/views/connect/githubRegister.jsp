<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>
<html>
<head>
	<title>GitHub Showcase: Registration</title>
	<link rel="stylesheet" href="<c:url value="/resources/form.css" />" type="text/css" media="all" />
</head>
<body>
<h1>GitHub Showcase: Registration</h1>

<c:url value="/signup" var="signupUrl"/>
<sf:form action="${signupUrl}" method="POST" modelAttribute="githubUserProfile">

	<sf:label path="username">Username <sf:errors path="username" cssClass="error" /></sf:label>
	<sf:input path="username" />

	<sf:label path="name">Name <sf:errors path="name" cssClass="error" /></sf:label>
	<sf:input path="name" />

	<sf:label path="email">Email <sf:errors path="email" cssClass="error" /></sf:label>
	<sf:input path="email" />
	
	<sf:label path="company">Company <sf:errors path="company" cssClass="error" /></sf:label>
	<sf:input path="company" />

	<sf:label path="blog">Blog <sf:errors path="blog" cssClass="error" /></sf:label>
	<sf:input path="blog" />

	<p><input type="submit" value="Signup with GitHub Showcase"/></p>
</sf:form>

</body>
</html>
