package Web;

import Db.DBHandler;
import Cryptography.ImgCryptoReader;
import Utils.Utilities;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowPhoto extends HttpServlet {
       
       private Lock lock = new ReentrantLock();
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
            response.getOutputStream().close();
            //lock.unlock();
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
        } 
        catch (Exception e)
        {
            
        }
        finally {            
            //out.close();
        }
    }    
    
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        
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
                
                 if (!db_session.isConnected()) {
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
                ResultSet result = db_session.getImage(picId);
                
                Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "encpic","Data");
                Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "publickey","pubkey");
                Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "a.keystore","privkey");
                
                String ckey = result.getString("CryptoKey");                  
                String title = result.getString("Title");                                                 
                
                //UPLOAD_DIRECTORY
                imageName = title + String.valueOf(Utilities.randInt(1,10)) + ".jpg";
                ImgCryptoReader.LoadImageFromDb(UPLOAD_DIRECTORY + "encpic",ckey,imageName);                               		
                                               
                processRequest(request, response);  
                                                            
        }
        catch(Exception exception) {
          //  out.println("Error --> " + exception.getMessage());
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {            
        UPLOAD_DIRECTORY = "D:/temp/";        
        //compiled code        
        Object o_picNumber = req.getParameter("pic_id");    
        String picId = (String)o_picNumber;
        
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
             //   out.println("Error --> " + exception.getMessage());
                db_session = null;          
                return;
            }
        }   
        
//        try {
         //   if (lock.tryLock(5,TimeUnit.SECONDS))
            {
                try {             
                        ServletContext ctx = getServletContext();
                        String path = ctx.getRealPath("/");   

                        Utilities.DeleteTempImages(path,".jpg");
                        // servletContext().getRealPath("/")
                        // String path = req.getRealPath(picId);
                        // File file = new File(".");
                        // FileUtils.cleanDirectory(file);                    
                        // imageName
                        
                        ResultSet result = db_session.getImage(picId);

                        Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "encpic","Data");
                        Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "publickey","pubkey");
                        Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "a.keystore","privkey");

                        String ckey = result.getString("CryptoKey");                  
                        String title = result.getString("Title");                                                 

                        //UPLOAD_DIRECTORY
                        imageName = path + title + String.valueOf(Utilities.randInt(1,10)) + ".jpg";
                                //title + "abc.jpg";
                        ImgCryptoReader.LoadImageFromDb(UPLOAD_DIRECTORY + "encpic",ckey,imageName);                               		
                        
                        processRequest(req, resp);  

                }
                catch(Exception exception) {
                //  out.println("Error --> " + exception.getMessage());
                }        
            }
//        } catch (InterruptedException ex) 
//        {
//            Logger.getLogger(ShowPhoto.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        finally
//        {
//                    lock.unlock();
//         }
//            
                                       
        
                
        //throw new RuntimeException("Compiled Code");
    }    
    
    @Override
    public String getServletInfo() {
        return "";
    }
    

}
