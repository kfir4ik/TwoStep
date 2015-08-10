package Web;

import Db.DBHandler;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Cryptography.BCrypt;
import Utils.PicMosaic;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UserLoginLogic", urlPatterns = {"/UserLoginLogic"})
public class UserLoginLogic extends HttpServlet 
{
    private static String UPLOAD_DIRECTORY = "/home/developer/temp";    
    private static DBHandler db_session;
    private static ArrayList<PicMosaic> pictures;    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
       
        Utils.Utilities.printPictureMosaic(pictures,response.getWriter(),"UserPicPasswordLogin",10,3);           
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession _session = request.getSession(true);     
           
        if (db_session == null)
        {
            try
            {
                db_session = DBHandler.getInstance();  
                
                 if (!db_session.isConnected()) {
                    db_session.connect();
                 }
            }
            catch (Exception e)
            {             
                db_session = null;          
                Utils.Utilities.printErrorReport(response,e);
                return;
            }
        }             
                        
        Object o_userName = request.getParameter("uname");                
        Object o_password = request.getParameter("password");        
        
        String userName = (String)o_userName;
        String password = (String)o_password;
        
        _session.setAttribute("username", userName);    
        
        try 
        {
            if (!db_session.doesUserExist(userName))
            {
               return;
            }
        }
        catch (SQLException e) 
        {
           Utils.Utilities.printErrorReport(response,e);
           return;
        }
        
        String hashedPassword ;
        byte[] salt;
        ResultSet result;
        
        try
        {
            result = db_session.getUser(userName);
            hashedPassword = result.getString("password");            
            salt = result.getBytes("salt");                           
            result.close();
        }
        catch (SQLException e) 
        { 
            Utils.Utilities.printErrorReport(response,e);
            return;
        }       
        
        String saltString1 = new String(salt);
           
        if (!BCrypt.checkpw(password + saltString1, hashedPassword))
        {
            Utils.Utilities.printErrorReport(response,new Exception("invalid password"));
            return;
        }          
        
        try
        {
            pictures = new ArrayList<PicMosaic>();    
            Utils.Utilities.GeneratePicturesMosaic(db_session, UPLOAD_DIRECTORY, pictures);
        }
        catch (Exception e)
        {  
            Utils.Utilities.printErrorReport(response,e);
            return;
        }
        
        _session.setAttribute("matrix", pictures);    
        
        processRequest(request, response);
    }

}
