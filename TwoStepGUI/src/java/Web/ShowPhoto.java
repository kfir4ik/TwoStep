package Web;

import Db.DBHandler;
import Cryptography.ImgCryptoReader;
import Utils.Utilities;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowPhoto extends HttpServlet {
              
    private static String UPLOAD_DIRECTORY;
    private static String imageName;
    private static DBHandler db_session;    
       
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException 
    {        
        try 
        { 
            Path path = Paths.get(imageName);
            byte[] imageBytes  = Files.readAllBytes(path);

            response.setContentType("image/jpeg");
            response.setContentLength(imageBytes.length);
            response.getOutputStream().write(imageBytes);              
            response.getOutputStream().close();                        
        } 
        catch (Exception e)
        {
            Utils.Utilities.printErrorReport(response,e);                        
        }
        request.logout();
    }            

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        Object o_picNumber = request.getParameter("pic_id");        

        String title = (String)o_picNumber;
                
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
                return;
            }
        }           
        
        try 
        {             
                ResultSet result = db_session.getImageByTitle(title);
                
                Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "encpic","Data");
                Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "publickey","pubkey");
                Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "a.keystore","privkey");
                
                String ckey = result.getString("CryptoKey");                                  
                
                imageName = title + String.valueOf(Utilities.randInt(1,100)) + ".jpg";
                ImgCryptoReader.LoadImageFromDb(UPLOAD_DIRECTORY + "encpic",ckey,imageName);                               		
                                               
                processRequest(request, response);  
                                                            
        }
        catch(Exception e) 
        {
            Utils.Utilities.printErrorReport(response,e);
            request.logout();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {            
        UPLOAD_DIRECTORY = "D:/temp/";        
        //compiled code        
        Object o_picNumber = request.getParameter("pic_id");    
        String title = (String)o_picNumber;
        
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

        try
        {             
            ServletContext ctx = getServletContext();
            String path = ctx.getRealPath("/");   

            Utilities.DeleteTempImages(path,".jpg");
                        
            ResultSet result = db_session.getImageByTitle(title);

            Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "encpic","Data");
            Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "publickey","pubkey");
            Utilities.ExtractDataFromDb(result,UPLOAD_DIRECTORY + "a.keystore","privkey");

            String ckey = result.getString("CryptoKey");                              
                        
            imageName = path + title + String.valueOf(Utilities.randInt(1,100)) + ".jpg";
                                
            ImgCryptoReader.LoadImageFromDb(UPLOAD_DIRECTORY + "encpic",ckey,imageName);                               		
                        
            processRequest(request, response);           
         }
         catch(Exception e)
         {
               Utils.Utilities.printErrorReport(response,e);  
               request.logout();
         }        
    }            
}
