<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
 <html>
    <head>
        <title>Add Photo</title>        
        <link rel="stylesheet" type="text/css" href="css/editor.css">
        <script type="text/javascript" src="scripts/jquery-1.6.1.min.js"></script>        
        <script>
            $(document).ready(function() {                        
                $('#linker').click(function(event) {                                          
                    $("#dvloader").show();                    
                    event.preventDefault();                     
                //    var username=$('#user').val();
                    $.get('ShowPhotoServlet',{id:115},function(responseText) { 
                        $('#results').replaceWith(responseText);                                 
                         $("#dvloader").hide();                                             
                        });
                });                    
                
               
            });
            function show_my_pic(e,id)
            {
                e.preventDefault();
                var edit_save = document.getElementById("pic_loader");
                
                edit_save.src = "image?id="+id; 
                //edit_save.src = "misc/bg2.gif";
                
                //image?id="+rs.getString("id") +"\
                //alert(edit_save.src);                                
            }               
            
        </script>        
    </head>    
     <body align="left" style="background-attachment: fixed; background-size: 100%; background-repeat: no-repeat; background-color: white;">
        <h2>Add picture to DB</h2>       
        <form id="form1" enctype="multipart/form-data" action="AddPhotoServlet" method="post">        
            <table>
                <tr>
                    <td>Write a title for picture:</td>
                    <td><input type="text" name="title"/></td>
                </tr>
                <tr>
                    <td>Select picture  </td>
                    <td><input type="file"  name="photo" />
                </tr>
            </table>
            <p/>
            <input type="submit" value="Add Photo"/>
        </form>
        <hr>
        <form name="form2" action="ShowPhotoServlet" method="post">           
            <p>Show picture by title:</p>
                    <input id="pic_id" type="text" name="pic_id" maxlength="12" pattern="[A-Za-z0-9]{1,15}">
                    <input type="submit" id="send" value="Get Photo"/>
        </form>
        <p/>
        <a id="linker" href="">List Photos.</a>
        <div style="display:none" id="dvloader"><img src="misc/359.gif" /></div>
    <div id="results">        
    </div>
        <div><hr><a href="#" onclick="javascript: window.close();window.opener.location.reload();">Back.</a></div>
    </body>
</html>
