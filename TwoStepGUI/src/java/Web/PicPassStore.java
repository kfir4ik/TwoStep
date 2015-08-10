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

@WebServlet(name = "PicPassStore", urlPatterns = {"/PicPassStore"})
public class PicPassStore extends HttpServlet
{    
    private static DBHandler db_session;    
    private static ArrayList<PicMosaic> pictures;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try 
        {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Registration page</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>congratulations! user was created.</h1>");
            out.println("<a href=\"Index.jsp\">Login.</a>");
            out.println("</body>");
            out.println("</html>");
        } 
        finally
        {            
            out.close();
        }
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
        
        HttpSession _session = request.getSession(true);      
        
        Object picsObject = _session.getAttribute("matrix");                
        
        String username = (String)_session.getAttribute("username");
        
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
                                                             
            byte[] salt;            

        try
        {                            
            ResultSet result = db_session.getUser(username);
            salt = result.getBytes("salt");                           
            result.close();
        }
        catch (SQLException e) 
        { 
            Utils.Utilities.printErrorReport(response,e);
            return;
        }       

        String saltString = new String(salt);                   
        
        //adding hashing
        String hashedPicPassword = BCrypt.hashpw(selectedPictures + saltString, BCrypt.gensalt());        
                
        try 
        {
            db_session.updateUserWithPicPassword(username,hashedPicPassword);
        } 
        catch (SQLException e) 
        {
            db_session = null;  
            Utils.Utilities.printErrorReport(response,e);          
            return;
        }
        
        processRequest(request, response);
    }    
}
