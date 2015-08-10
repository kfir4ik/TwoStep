<%@page contentType="text/html" pageEncoding="UTF-8"%>
 <html>
    <head>
        <title>Add Photo</title>                              
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
        <form name="form2" action="ShowPhotoServlet?pic_id=1" method="get">           
            <p>Show picture by title:</p>
                    <input id="pic_id" type="text" name="pic_id" maxlength="12" pattern="[A-Za-z0-9]{1,15}">
                    <input type="submit" id="send" value="Get Photo"/>
        </form>
        <p/>        
        <div style="display:none" id="dvloader"><img src="misc/359.gif" /></div>
    <div id="results">        
    </div>
        <div><hr><a href="#" onclick="javascript: window.close();window.opener.location.reload();">Back.</a></div>
    </body>
</html>
