<%@ include file="include.jsp" %>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="<c:url value="/style.css"/>"/>
    <title>Auth Filter Quickstart</title>
</head>
<body>

<h1>Auth Filter Quickstart</h1>

<p>
 <c:choose>
 <c:when test="${empty user.login}">
     Hi Unknown! <a href="<c:url value="/login.jsp"/>">Log in</a> first
 </c:when>
 <c:otherwise>
     Hi ${user.login}!
     <a href="<c:url value="/account"/>">Account page</a> | <a href="<c:url value="/logout"/>">Log out</a>
 </c:otherwise>
 </c:choose>
</p>
<hr/>
</body>
</html>
