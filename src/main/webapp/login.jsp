<%@ include file="include.jsp" %>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="<c:url value="/style.css"/>"/>
    <style type="text/css">
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
        <tr><td>presidentskroob</td> <td>12345</td></tr>
        <tr><td>darkhelmet</td>	<td>ludicrousspeed</td></tr>
        <tr><td>lonestarr</td>	<td>vespa</td> </tr>
        </tbody>
    </table>
    <br/><br/>

<form name="loginform" action="" method="post">
    <table align="left" border="0" cellspacing="0" cellpadding="3">
        <tr>
            <td>Username:</td>
            <td><input type="text" name="username" maxlength="30"></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type="password" name="password" maxlength="30"></td>
        </tr>
        <tr>
            <td colspan="2" align="right"><input type="submit" name="submit" value="Login"></td>
        </tr>
    </table>
</form>

</body>
</html>
