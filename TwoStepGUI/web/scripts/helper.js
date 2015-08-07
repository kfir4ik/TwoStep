function login_validation1()
{    
    var user_name = document.getElementById('uname');
    var user_password = document.getElementById('password');

    if (user_name.value != null && user_password.value != null)
    {
   
       if (validate_login(user_name,user_password)) 
       {            
           document.forms["myform"].submit();                
           return true;
       }
 
    }
    
    alert('invalid input');
        
    return false;
}