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
import Cryptography.BCrypt;
import Cryptography.ImgCryptoReader;
import Utils.PicMosaic;
import java.io.*;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.UUID;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kfirsa
 */
@WebServlet(name = "UserWebLogic", urlPatterns = {"/UserWebLogic"})
public class UserRegistrationWebLogic extends HttpServlet {

    private static String UPLOAD_DIRECTORY;
    private static String imageName;
    private static DBHandler db_session;
    private static int load_state = 0;    
    private static ArrayList<PicMosaic> pictures;
    
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
            int picsInRow = 4;
            int numberOfRows = pictures.size() / picsInRow;            
            numberOfRows += pictures.size() % picsInRow;
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UserWebLogic</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<table>");
            
            int k =0;
            for (int j = 0; j < numberOfRows; j++) {
                
                    out.println("<tr>");    
                    
                    for (int i = 0; i < 4 && k < pictures.size()-1; i++) 
                    {
                        out.println("<td><p>[" + (k+1) +"]</p><img width=\"70px\" height=\"70px\" src=\"" + "PicPic?pic_id=" + (k++) + "\" /></td>");    
                    }                                  
                    
                out.println("</tr>");    
            }


            //            out.println("<td><img src=\"" + "ShowPhotoServlet?pic_id=1"+ "\" /></td>");
            out.println("</tr>");            
            out.println("</table>");            
            
            out.println("<form method=\"post\" action=\"\">");
            out.println("<table>");            
            out.println("<tr>");            
            
            for (int i = 0; i < 3; i++) {
                out.println("<td>");            
                
                out.println("<select name=\"passpic" + i + "\">");
                for (k=0;k<=pictures.size()-1;k++)
                {
                    out.println("<option value=\""+ (k+1) +"\">Pic [" + (k+1)+ "]</option>");
                }
                out.println("</select>");                
                
                out.println("</td>");            
            }


            out.println("</tr>");                        
            out.println("</table>");                        
            out.println("<hr><input type=\"submit\" name=\"submit\" value=\"Sign-in\">");
            out.println("</form>");
        //    out.println("<h1>Servlet UserWebLogic at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
             
        } finally {            
            out.close();
        }        
    }
//
//    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//    /** 
//     * Handles the HTTP <code>GET</code> method.
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileNotFoundException
    {        
        UPLOAD_DIRECTORY = "D:/temp/";    
        
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
            salt = Utils.Utilities.generateNonce();
        } 
        catch (AuthenticationException ex) 
        {
            salt = null;
        }
                
        String saltString = new String(salt);
        String hashedPassword = BCrypt.hashpw(password + saltString, BCrypt.gensalt());        
        
//        if (BCrypt.checkpw(password + saltString, hashedPassword))
//        {
//            int a=1;
//        }             
        
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
        if (!BCrypt.checkpw(password + saltString1, hashedPassword1))
        {
            return;
        }     
        
        ResultSet imagesNumbers = null;
        int numberOfImages = 0;
        
        try 
        {
            imagesNumbers = db_session.getGetImagesNumbers(0);
            numberOfImages = db_session.getGetNumberOfImages();
        }
        catch (Exception ex)
        {
            
        }
        
        Utils.Utilities.DeleteTempImages(UPLOAD_DIRECTORY,".jpg");
            
        int[] shuffleArray = new int[numberOfImages];            
        for (int i=0;i<numberOfImages;i++)
        {
            shuffleArray[i] = i;
        }
            
        Utils.Utilities.shuffleArray(shuffleArray);
                    
        pictures = new ArrayList<PicMosaic>();        

        int index=0;            
        try
        {            
           while (imagesNumbers.next())
           {                   
              int id = imagesNumbers.getInt("pic_id");            
              
              PicMosaic picture = new PicMosaic(id);                      
              picture.shuffle_pos = shuffleArray[index++];
              picture.generatedName = UUID.randomUUID().toString();
              
              pictures.add(picture);
                        
              ResultSet result1 = db_session.getImage(String.valueOf(picture.pic_id));                      
              Utils.Utilities.ExtractPicture(result1, picture.generatedName,UPLOAD_DIRECTORY);
            }
         }
         catch (Exception ex)
         {
                    
         }
            
        HttpSession _session = request.getSession(true);        
        _session.setAttribute("matrix", pictures);                    
         
        processRequest(request, response);
    }
    
 

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//    
public static void ExtractDataFromDb(ResultSet result,String fileName,String dbField) throws SQLException, FileNotFoundException, IOException
{
                OutputStream outputStream = new FileOutputStream(new File(fileName));
    
                Blob blob = result.getBlob(dbField);
                InputStream img = blob.getBinaryStream();                                                        
                
                int read = 0;
                byte[] bytes = new byte[4096];
                while ((read = img.read(bytes)) != -1)
                {
                    outputStream.write(bytes,0,read);
                    outputStream.flush();
                }
                
                outputStream.close();                
                img.close();                               
}    
}
