package Web;

import Cryptography.BCrypt;
import Db.DBHandler;
import Utils.PicMosaic;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UserPicPasswordLogin", urlPatterns = {"/UserPicPasswordLogin"})
public class UserPicPasswordLogin extends HttpServlet
{    
    private static DBHandler db_session;    
    private static ArrayList<PicMosaic> pictures;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try 
        {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Registration page</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Welcome !!!</h1>");
            out.println("</body>");
            out.println("</html>");            
        } 
        finally
        {            
            out.close();
        }
        request.logout();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {        
        if (db_session == null)
        {
            try
            {
                db_session = DBHandler.getInstance();  
                
                 if (!db_session.isConnected()) 
                 {
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
            request.logout();
       }             
        
        HttpSession _session = request.getSession(true);      
        Object picsObject = _session.getAttribute("matrix");                
        pictures = (ArrayList<PicMosaic>)picsObject;     
        
        String selectedPictures = "";
        
        int max = 3;
        for (int i = 0; i < max; i++) 
        {
            selectedPictures += Utils.Utilities.GetImageNumberFromRequest(request,"passpic" + i,pictures);    
            
            if (i+1 != max)
            {
                selectedPictures += ",";
            }            
        }                       
        
        Object o_username = _session.getAttribute("username");                                  
        String username = (String)o_username;                                  
        
        try 
        {
            ResultSet result = db_session.getUser(username);
            
            //String storedPassword = result.getString("picpassword");                    
            
            String hashedPassword ;
            byte[] salt;            

            try
            {                
                hashedPassword = result.getString("picpassword");            
                salt = result.getBytes("salt");                           
                result.close();
            }
            catch (SQLException e) 
            { 
                Utils.Utilities.printErrorReport(response,e);
                request.logout();
                return;
            }       

            String saltString1 = new String(salt);
                        
            if (!BCrypt.checkpw(selectedPictures + saltString1, hashedPassword))                                    
            //if (!BCrypt.checkpw(hashedPicPassword, storedPassword))
            //if (!storedPassword.equals(selectedPictures))
            {
                Utils.Utilities.printErrorReport(response,new Exception("Invalid login data."));
                return;
            }
        } 
        catch (SQLException e) 
        {
            db_session = null;  
            Utils.Utilities.printErrorReport(response,e);
            request.logout();
            return;
        }
        
        processRequest(request, response);
    } 
}
