package Web;

import Utils.PicMosaic;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "PicPic", urlPatterns = {"/PicPic"})
public class GetPictureObject extends HttpServlet 
{
    private static String UPLOAD_DIRECTORY = "D:\\temp\\";
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession _session = request.getSession(true);      
        Object picsObject = _session.getAttribute("matrix");                
        ArrayList<PicMosaic> pictures = (ArrayList<PicMosaic>)picsObject;                                                                 
            
        try
        {                                                     
            Object o_picNumber = request.getParameter("pic_id");    
            String _picId = (String)o_picNumber;                            
            
            PicMosaic selectedPic = pictures.get(Integer.parseInt(_picId));
            int pos = selectedPic.shuffle_pos;        
            Path path = Paths.get(UPLOAD_DIRECTORY + pictures.get(pos).generatedName + ".jpg");
            byte[] imageBytes  = Files.readAllBytes(path);

            response.setContentType("image/jpeg");
            response.setContentLength(imageBytes.length);
            response.getOutputStream().write(imageBytes);              
        }
        catch (Exception ex)
        {
           
        }
        finally {            
            //out.close();
            response.getOutputStream().close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
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
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
