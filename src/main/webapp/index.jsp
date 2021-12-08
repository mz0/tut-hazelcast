<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="<c:url value="/style.css"/>"/>
    <link rel="icon" type="image/png" href="/steps.png">
    <title>Auth Filter Quickstart</title>
</head>
<body>

<h1>Auth Filter Quickstart</h1>

<p>
 <c:choose>
 <c:when test="${empty sessionScope.name}">
     Hi Unknown! <a href="<c:url value="/login.xhtml"/>">Log in</a> first
 </c:when>
 <c:otherwise>
     Hi ${sessionScope.name}!
     <a href="<c:url value="/account"/>">Account page</a> | <a href="<c:url value="/logout"/>">Log out</a>
 </c:otherwise>
 </c:choose>
</p>
</body>
</html>
