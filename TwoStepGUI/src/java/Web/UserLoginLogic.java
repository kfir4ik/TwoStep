/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import Db.DBHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Cryptography.BCrypt;

/**
 *
 * @author kfirsa
 */
@WebServlet(name = "UserLoginLogic", urlPatterns = {"/UserLoginLogic"})
public class UserLoginLogic extends HttpServlet {

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
            out.println("<title>Servlet UserLoginLogic</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UserLoginLogic at " + request.getContextPath () + "</h1>");
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
            throws ServletException, IOException {
        
        if (db_session == null)
        {
            try
            {
                db_session = DBHandler.getInstance();  
                
                 if (!db_session.isConnected()) {
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
            if (!db_session.doesUserExist(userName))
            {
               return;
            }
        }
        catch (SQLException ex) 
        {
           return;
        }
        String hashedPassword = "";
        byte[] salt = null;
        ResultSet result;
        try {
            result = db_session.getUser(userName);
            hashedPassword = result.getString("password");
            salt = result.getBytes("salt");                        
        }
        catch (SQLException ex) {
            
        }                
        String saltString1 = new String(salt);
           
        if (BCrypt.checkpw(password + saltString1, hashedPassword))
        {
            boolean passed = true;
        }            
        
        processRequest(request, response);
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
