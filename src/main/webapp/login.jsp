<%@ include file="include.jsp" %>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="<c:url value="/style.css"/>"/>
    <link rel="icon" type="image/png" href="/steps.png">
    <style>
        table.sample {
            border-width: 1px;
            border-style: outset;
            border-color: blue;
            border-collapse: separate;
            background-color: rgb(255, 255, 240);
        }

        table.sample th {
            border-width: 1px;
            padding: 1px;
            border-style: none;
            border-color: blue;
            background-color: rgb(255, 255, 240);
        }

        table.sample td {
            border-width: 1px;
            padding: 1px;
            border-style: none;
            border-color: blue;
            background-color: rgb(255, 255, 240);
        }
    </style>
    <title>Login</title>
</head>
<body>

<h2>Please Log in</h2>

    <p>Here are a few sample accounts to play with</p>
    <table class="sample">
        <thead>
        <tr><th>Username</th>	<th>Password</th></tr>
        </thead>
        <tbody>
        <tr><td>root</td>	<td>secret</td></tr>
        <tr><td>president</td> <td>12345</td></tr>
        <tr><td>darkhelmet</td>	<td>ludicrous speed</td></tr>
        <tr><td>lonestarr</td>	<td>vespa</td> </tr>
        </tbody>
    </table>
    <br/><br/>

<form name="login-form" action="/login" method="POST">
    <label>Username: <input type="text" name="username" maxlength="30"></label><br>

    <label>Password: <input type="password" name="password" maxlength="30"></label><br>

    <input type="submit" name="login-button" value="Login">
</form>

</body>
</html>
