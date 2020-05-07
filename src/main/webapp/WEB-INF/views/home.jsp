<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hallo world!
</h1>

<P>  The time on the server is ${serverTime}. </P>

<form method="get" action="<%=request.getContextPath()%>/skillUpload">
	<div style="padding: 5px;">
		<button type="submit">Hallo</button>
	</div>
</form>

</body>
</html>
