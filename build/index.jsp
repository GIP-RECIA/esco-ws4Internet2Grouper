<%@page import="org.springframework.util.StringUtils"%>
<html>
<head>
</head>
<body>
<%
String newLocation = "stylesheets/welcome.faces?enter=";
if (StringUtils.hasText(request.getQueryString())) {
	newLocation = newLocation + "&" + request.getQueryString();
}
response.sendRedirect(newLocation);
%>
</body>
</html>
