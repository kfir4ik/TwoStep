package Web;

import Db.DBHandler;
import java.io.IOException;
import java.sql.SQLException;
import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Cryptography.BCrypt;
import Utils.PicMosaic;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;


@WebServlet(name = "UserWebLogic", urlPatterns = {"/UserWebLogic"})
public class UserRegistrationWebLogic extends HttpServlet {

    private static String UPLOAD_DIRECTORY = "D:/temp/";    
    private static DBHandler db_session;
    private static ArrayList<PicMosaic> pictures;
    
    private static int load_state = 0;    
        
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        response.setContentType("text/html;charset=UTF-8");
        
        Utils.Utilities.printPictureMosaic(pictures,response.getWriter(),"PicPassStore",10,3);    
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileNotFoundException
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
                request.logout();
                return;
            }
        }             
                        
        Object o_userName = request.getParameter("uname");                
        String userName = (String)o_userName;
        
        Object o_password = request.getParameter("password");                                       
        String password = (String)o_password;
        
        _session.setAttribute("username", userName);
        
        try 
        {
          if (db_session.doesUserExist(userName))
          {
            return;
          }
        }
        catch (SQLException e)
        {
            Utils.Utilities.printErrorReport(response,e);
            request.logout();
            return;
        }
        
        byte[] salt;                        
        try 
        {
            salt = Utils.Utilities.generateNonce();
        } 
        catch (AuthenticationException e) 
        {
            salt = null;
            Utils.Utilities.printErrorReport(response,e);
            request.logout();
            return;
        }
                
        String saltString = new String(salt);
        String hashedPassword = BCrypt.hashpw(password + saltString, BCrypt.gensalt());                      
        
        try 
        {
            db_session.addNewUserToDb(userName, hashedPassword, salt);
        } 
        catch (SQLException e)
        { 
            Utils.Utilities.printErrorReport(response,e);
            request.logout();
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
             request.logout();
             return;
        }
            
           //store pictures table in session cookie
        _session.setAttribute("matrix", pictures);          
         
        processRequest(request, response);
    } 
    
}
