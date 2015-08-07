package Web;

import Db.DBHandler;
import Db.ImgCryptoReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ShowPhotoServlet extends HttpServlet {

       private static String UPLOAD_DIRECTORY;
       private static String imageName;
       private static DBHandler db_session;
       private static int load_state = 0;
       
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        
            throws ServletException, IOException {
        //get encrypted file length
       
                
        
//        ServletContext ctx = getServletContext();
  //      String path = ctx.getRealPath("/");
        //String place = ServletContext.getRealPath("//");
        
        try { 
            Path path = Paths.get(imageName);
            byte[] imageBytes  = Files.readAllBytes(path);

            response.setContentType("image/jpeg");
            response.setContentLength(imageBytes.length);
            response.getOutputStream().write(imageBytes);              
            /*
            out.println("<html>");
            out.println("<head>");
            out.println("<title>ttt</title>");            
            out.println("</head>");
            out.println("<body align=\"left\" style=\"background-color: green;\">");
            out.println("<img src=\"" + path + "\\" + imageName + "\" width=\"40px\" height=\"40px\"></br>");
            out.println("<a href=\"javascript:history.back();\">Back</a>");
            out.println("</body>");
            out.println("</html>");
            * 
            */
        } finally {            
            //out.close();
        }
    }       

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UPLOAD_DIRECTORY = "D:/temp/";
        
        //PrintWriter out = response.getWriter();
        
        //HttpSession _session = request.getSession(true);        
                
        Object o_picNumber = request.getParameter("pic_id");        
       //Object pFnameFromContextObj = request.getParameter("fname");
        String picId = (String)o_picNumber;
                
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
        
        try {             
                ResultSet result = db_session.getImageFromDb(picId);
                
                ExtractDataFromDb(result,UPLOAD_DIRECTORY + "encpic","Data");
                ExtractDataFromDb(result,UPLOAD_DIRECTORY + "publickey","pubkey");
                ExtractDataFromDb(result,UPLOAD_DIRECTORY + "a.keystore","privkey");
                
                String ckey = result.getString("CryptoKey");                  
                String title = result.getString("Title");                                                 
                
                //UPLOAD_DIRECTORY
                imageName = title + String.valueOf(randInt(1,10)) + ".jpg";
                ImgCryptoReader.LoadImageFromDb(UPLOAD_DIRECTORY + "encpic",ckey,imageName);                               		
                                               
                processRequest(request, response);  
                                                            
        }
        catch(Exception exception) {
          //  out.println("Error --> " + exception.getMessage());
        }
    }
    
    @Override
    public String getServletInfo() {
        return "";
    }
    
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
    
public static int randInt(int min, int max) {

    // NOTE: Usually this should be a field rather than a method
    // variable so that it is not re-seeded every call.
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;

    return randomNum;
}    
}
