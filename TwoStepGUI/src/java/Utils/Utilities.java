package Utils;


import Cryptography.ImgCryptoReader;
import java.io.*;
import java.security.SecureRandom;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import javax.naming.AuthenticationException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author xbox
 */
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
                // Simple swap
                int a = array[index];
                array[index] = array[i];
                array[i] = a;
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
                
}

