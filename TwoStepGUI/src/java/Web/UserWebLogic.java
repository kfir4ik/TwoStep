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
public class UserWebLogic extends HttpServlet {

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
        try {
            /* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UserWebLogic</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UserWebLogic at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             */
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
             //   out.println("Error --> " + exception.getMessage());
                db_session = null;          
                return;
            }
        }             
                        
        Object o_userName = request.getParameter("uname");                
        Object o_password = request.getParameter("password");        
        String password = (String)o_password;
        String salt;
        
        try 
        {
            salt = generateNonce();
        } 
        catch (AuthenticationException ex) 
        {
            salt = "";
        }
                
        String hashed = BCrypt.hashpw(password + salt, BCrypt.gensalt());        
                         
       // hashed1 = BCrypt.hashpw(password + salt, BCrypt.gensalt(12));        
        
        if (BCrypt.checkpw(password + "a" + salt, hashed))
        {
            int i=1;
        }        
        
        processRequest(request, response);
    }
    
    private String generateNonce() throws AuthenticationException
    {
        try {
            SecureRandom sr=SecureRandom.getInstance("SHA1PRNG");
            byte[] temp=new byte[55];
            sr.nextBytes(temp);
            String n=new String(temp);
            return n;
        }
        catch (Exception ex) 
        {
            //throw new AuthenticationException(e.getMessage(),e);
        }
  
        return "";
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
