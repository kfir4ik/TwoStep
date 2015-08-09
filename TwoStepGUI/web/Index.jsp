<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%                
        HttpSession _session = request.getSession(true);        
        
        Object gameSessionState = session.getAttribute("user_session_state");     
        
        if (gameSessionState != null)
        {
           response.sendRedirect("UserWebLogic");
        }        
%>
<html>
    <head>
        <meta name="viewport" content="initial-scale = 0.5, user-scalable=yes"  />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Logging page</title>
        <link rel="stylesheet" type="text/css" href="css/special2.css">
        <script type="text/javascript" src="scripts/helper.js"></script>
    </head>
    <body>    
    <div class="container">	
	<h1>Logging Page.</h1><hr>
		<form name="myform" action="UserLoginLogic" method="post">                      	
			<div>
                            <input type="text" placeholder="UserID" required="" name="uname" id="uname" style="width:500px"/>
			</div>
			<div>
                            <input type="password" placeholder="Password" required="" name="password" id="password" style="width:500px"/>
			</div>
			<div>                            
                            <input type="submit" value="Log in" style="width:100px;height:45px"/>
                            <a href="mailto: unsupported@email.com">Forgot your password?</a>
                            <a href="register.jsp">Register</a>                            
			</div>
		</form>		
    </div>	
</body>
</html>
