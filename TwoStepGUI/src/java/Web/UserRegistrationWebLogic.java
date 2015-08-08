/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import Db.DBHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author kfirsa
 */
@WebServlet(name = "UserWebLogic", urlPatterns = {"/UserWebLogic"})
public class UserRegistrationWebLogic extends HttpServlet {

    private static DBHandler db_session;
    private static int load_state = 0;    
    
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try 
        {            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UserWebLogic</title>");  
            out.println("</head>");
            out.println("<body>");
        //    out.println("<h1>Servlet UserWebLogic at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {        
        if (db_session == null)
        {
            try
            {
                db_session = DBHandler.getInstance();  
                
                 if (!db_session.is_connected()) {
                    db_session.connect();
                 }
            }
            catch (Exception exception)
            {             
                db_session = null;          
                return;
            }
        }             
                        
        Object o_userName = request.getParameter("uname");                
        Object o_password = request.getParameter("password");        
        
        String userName = (String)o_userName;
        String password = (String)o_password;
        
        try 
        {
          if (db_session.doesUserExist(userName))
          {
            return;
          }
        }
        catch (SQLException ex)
        {
            return;
        }
        
        byte[] salt;                        
        try 
        {
            salt = generateNonce();
        } 
        catch (AuthenticationException ex) 
        {
            salt = null;
        }
                
        String saltString = new String(salt);
        String hashedPassword = BCrypt.hashpw(password + saltString, BCrypt.gensalt());        
        
        if (BCrypt.checkpw(password + saltString, hashedPassword))
        {
            int a=1;
        }             
        
        try 
        {
            db_session.addNewUserToDb(userName, hashedPassword, salt);
        } 
        catch (SQLException ex)
        {
            return;
        }
        
        String hashedPassword1 = "";
        byte[] salt1 = null;
        ResultSet result;
        try {
            result = db_session.getUser(userName);
            hashedPassword1 = result.getString("password");
            salt1 = result.getBytes("salt");                        
        }
        catch (SQLException ex) {
            
        }
        String saltString1 = new String(salt1);
        
        if (saltString1.equals(saltString))
        {
            int k=1;
        }
        //salt = salt1;
        if (BCrypt.checkpw(password + saltString1, hashedPassword1))
        {
            int a=1;
        }     
         
        //processRequest(request, response);
    }
    
    private byte[] generateNonce() throws AuthenticationException
    {
        try {
            SecureRandom sr=SecureRandom.getInstance("SHA1PRNG");
            byte[] temp=new byte[55];
            sr.nextBytes(temp);
            //String n=new String(temp);
            return temp;
        }
        catch (Exception ex) 
        {
            //throw new AuthenticationException(e.getMessage(),e);
        }
  
        return null;
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
