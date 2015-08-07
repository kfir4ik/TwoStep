<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="initial-scale = 0.5, user-scalable=yes"  />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registration page</title>
        <link rel="stylesheet" type="text/css" href="css/special2.css">
        <script type="text/javascript" src="scripts/jlogic.js"></script>
    </head>
<div class="container">
	
	<h1>Registration Page.</h1>
	<!--<form name="myform" action="UserWebLogic" method="post" onsubmit="return login_validation2();">-->
	<form name="myform" action="UserWebLogic" method="post">
            
		<div>
                    <p><u>Registration page:</u></p><hr>
		<!--
			<label for="fname">First name</label>
			<input id="fname" type="text" name="fname" maxlength="12" pattern="[A-Za-z]{2,15}">
			<label for="lname">Last name</label>
			<input id="lname" type="text" name="lname" maxlength="12" pattern="[A-Za-z]{2,15}">
                 -->
			<label for="lname">User name</label>
			<input id="uname" type="text" name="uname" maxlength="12" pattern="[A-Za-z0-9]{3,15}">

		</div>
		
		<div>
			<label for="first_name">Password</label>
			<input id="pass" type="password" name="password" required>
                        <p class="help-block">alphabets, numbers and underscore allowed. max-length:6.</p>
		</div>
		
		
		<div>
                    <hr><input type="submit" name="submit" value="Sign-in">
		</div>
	
	</form>
	
</div>	
</body>
</html>
