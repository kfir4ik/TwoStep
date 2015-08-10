package Utils;


import Cryptography.ImgCryptoReader;
import Db.DBHandler;
import java.io.*;
import java.security.SecureRandom;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utilities
{    
            public static void DeleteTempImages(String path,final String suffix)
            {                              
                File f =  new File(path);
              
                FilenameFilter jpgFilter = new FilenameFilter()
                {
                        @Override
                    public boolean accept(File dir, String name) {
                        String lowercaseName = name.toLowerCase();
                        if (lowercaseName.endsWith(suffix)) {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                };
            
                File[] files = f.listFiles(jpgFilter);
                for (File file : files) 
                {
                    file.delete();
                }
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

            public static void ExtractPicture(ResultSet result,String pictureName,String uploadDirectory)
            {
                 try
                 {                                                                                                            
                    ExtractDataFromDb(result,uploadDirectory + "encpic","Data");
                    ExtractDataFromDb(result,uploadDirectory + "publickey","pubkey");
                    ExtractDataFromDb(result,uploadDirectory + "a.keystore","privkey");

                    String ckey = result.getString("CryptoKey");                  
                            //String title = result.getString("Title");                                                 
                            
                            //String imageName = title + String.valueOf(randInt(1,10)) + ".jpg";
                    ImgCryptoReader.LoadImageFromDb(uploadDirectory + "encpic",ckey,uploadDirectory + pictureName + ".jpg");                                
                 }
                 catch(Exception ex)
                 {

                 }  
            }
            
            public static void shuffleArray(int[] array)
            {
                Random rnd = new Random();
                for (int i = array.length - 1; i > 0; i--)
                {
                int index = rnd.nextInt(i + 1);                                
                int val = array[index];
                array[index] = array[i];
                array[i] = val;
                }
            }        
    
            public static byte[] generateNonce() throws AuthenticationException
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
                
                }
  
                return null;
            }          
            
            public static void GeneratePicturesMosaic(DBHandler db_session,String upload_directory,ArrayList<PicMosaic> pictures) throws Exception
            {
                ResultSet imagesNumbers = null;
                int numberOfImages = 0;

                try 
                {
                    imagesNumbers = db_session.getGetImagesIdNumbers(0);
                    numberOfImages = db_session.getGetNumberOfImages();
                }
                catch (Exception ex)
                { }

                DeleteTempImages(upload_directory,".jpg");

                int[] shuffleArray = new int[numberOfImages];            
                for (int i=0;i<numberOfImages;i++)
                {
                    shuffleArray[i] = i;
                }

                Utils.Utilities.shuffleArray(shuffleArray);    

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

                    ResultSet result1 = db_session.getImageById(String.valueOf(picture.pic_id));                      
                    Utils.Utilities.ExtractPicture(result1, picture.generatedName,upload_directory);
                    }
                }
                catch (Exception ex)
                { 
                    throw new Exception("GeneratePicturesMosaic has failed.",ex);
                }
       
            }        
            
            public static void printPictureMosaic(ArrayList<PicMosaic> pictures,PrintWriter out,String actionPage,int picsInRow,int numOfLists)
            {        
                try 
                {                         
                    int numberOfRows = pictures.size() / picsInRow;            
                    numberOfRows += pictures.size() % picsInRow;

                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Registration page</title>");  
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<table>");

                    int picCount = 0;
                    for (int j = 0; j < numberOfRows; j++) {

                            out.println("<tr>");    

                            for (int i = 0; i < picsInRow && picCount < pictures.size(); i++) 
                            {
                                out.println("<td><p>[" + (picCount+1) +"]</p><img width=\"70px\" height=\"70px\" src=\"" + "PicPic?pic_id=" + (picCount++) + "\" /></td>");    
                            }                                  

                        out.println("</tr>");    
                    }

                    out.println("</tr>");            
                    out.println("</table><hr>");                        
                    //PicPassStore
                    out.println("<form method=\"post\" action=\""+ actionPage + "\">");
                    out.println("<table>");            
                    out.println("<tr>");            

                    for (int i = 0; i < numOfLists; i++)
                    {
                        out.println("<td>");            

                        out.println("<label>"+ (i+1) +".</label><select name=\"passpic" + i + "\">");
                        for (int k=0;k<=pictures.size()-1;k++)
                        {
                            out.println("<option value=\""+ (k+1) +"\">Pic [" + (k+1)+ "]</option>");
                        }
                        out.println("</select>");                

                        out.println("</td>");            
                    }

                    out.println("</tr>");                        
                    out.println("</table>");                        
                    out.println("<hr><input type=\"submit\" name=\"submit\" value=\"Continue.\">");
                    out.println("</form>");        
                    out.println("</body>");
                    out.println("</html>");

                } finally {            
                    out.close();
                }            
            }     
            
            public static int GetImageNumberFromRequest(HttpServletRequest request,String htmlObjectName,ArrayList<PicMosaic> pictures)
            {
                Object o_picSelect = request.getParameter(htmlObjectName);    
                String _picSelect = (String)o_picSelect;             

                int num = Integer.parseInt(_picSelect) - 1;
                return pictures.get(pictures.get(num).shuffle_pos).pic_id;        
            }            
            
            public static void printErrorReport(HttpServletResponse response,Exception e) throws IOException
            {
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<html>");
                out.println("<title>");
                out.println("Exception handle - message");
                out.println("</title>");                
                out.println("<body>");
                out.println("<p>" + e.toString() + "</p>");
                out.println("</body>");
                out.println("</html>");
                out.close();                      
            }            
}

