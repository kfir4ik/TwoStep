<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%        
        HttpSession _session = request.getSession(true);
        
        Object gameSessionState = session.getAttribute("user_session_state");     
        
        if (gameSessionState != null)
        {
           response.sendRedirect("WebLogic");
        }        
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="initial-scale = 0.5, user-scalable=yes"  />
        <title>On Wheels of History</title>
        <link rel="stylesheet" type="text/css" href="css/special.css">
        <script type="text/javascript" src="scripts/jlogic.js"></script>
    </head>
    <body>
<div class="container">
	<section id="content">
		<form name="myform" action="WebLogic" method="post" onsubmit="return false;">                      
			<h1>Login Page</h1>
			<div>
				<input type="text" placeholder="UserID" required="" name="uname" id="uname" />
			</div>
			<div>
				<input type="password" placeholder="Password" required="" name="password" id="password"/>
			</div>
			<div>                            
                            <input type="submit" value="Log in" onclick="login_validation()"/>
				<a href="mailto: support@onwheelsofhistory.com">Forgot your password?</a>
				<a href="register.jsp">Register</a>
			</div>
		</form>
	</section>
</div>
</body>
</html>
