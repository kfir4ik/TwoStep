/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import Db.DBHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kfirsa
 */
@WebServlet(name = "PicPasswordRegistration", urlPatterns = {"/PicPasswordRegistration"})
public class PicPasswordRegistration extends HttpServlet {
    
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
            out.println("<title>Servlet PicPasswordRegistration</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PicPasswordRegistration at " + request.getContextPath () + "</h1>");
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
//        try {
//            ResultSet imagesNumbers = db_session.getGetImagesNumbers(0);
//            int numberOfImages = db_session.getGetNumberOfImages();
//            
//            int[] shuffleArray = new int[numberOfImages];
//            
//            for (int i=0;i<numberOfImages;i++)
//            {
//                shuffleArray[i] = i+1;
//            }
//            
//           // shuffleArray(shuffleArray);
//            
//            //PicMosaic[] pictures = new PicMosaic[numberOfImages];
//            ArrayList<PicMosaic> pictures = new ArrayList<PicMosaic>();
//            int i=0;    
//            
//            while (imagesNumbers.next())
//            {                
//                PicMosaic picture = new PicMosaic(imagesNumbers.getInt("pic_id"));                
//                picture.shuffle_pos = shuffleArray[i++];
//                picture.generatedName = UUID.randomUUID().toString();
//                pictures.add(picture);
//            }
//            
//            HttpSession _session = request.getSession(true);        
//                
//            _session.setAttribute("matrix", pictures);
//            
//        } catch (SQLException ex) {
//            
//        }
        
        
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


