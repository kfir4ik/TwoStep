package Db;

import Cryptography.ImgCrypto;
import com.sun.jersey.core.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.fileupload.FileItem;

/**
 * This class handles interaction with the DB and enforce thread safe access to it's methods by using a Singletoned pattern.
 */
public class DBHandler
{
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_BASE_URL = "jdbc:mysql://192.168.56.101:3306/";
	private static final String DB_NAME = "TwoStep";
	private static final String DB_URL = DB_BASE_URL + DB_NAME;
	private static final String DB_USER = "developer";
	private static final String DB_PASS = "1";
        	  
	private static DBHandler s_Instance = null;
	private Connection m_Conn = null;
        private boolean is_active;
        private static int user_counter=0;
	
	/**
	 * Get Singleton instance.
	 * @return
	 */
	synchronized public static DBHandler getInstance()
	{
		if (s_Instance == null)
		{
			try
			{
				s_Instance = new DBHandler();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
                user_counter++;
		return s_Instance;
                
	}
	
	/**
	 * C'tor
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private DBHandler() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
                is_active=false;
		Class.forName(JDBC_DRIVER).newInstance();
                
	}
	
	/**
	 * Connect to DB.
	 * @throws SQLException
	 */
	public void connect() throws SQLException
	{
		try
		{
			m_Conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                        is_active = true;
		}
		catch (SQLException e)
		{                    
                   e.getMessage();
                   s_Instance = null;
                   user_counter=0;
			// can't connect to the database :\
		}
	}
        
        public boolean isConnected()
        {
            return is_active;
        }
	
	public void disconnect() throws SQLException
	{       
            user_counter--;
            if (user_counter < 1)
            {
		m_Conn.close();
                is_active=false;
            }
	}
	          
        public ResultSet getImage(String id) throws SQLException
        {
             String query = "select Title,Data,CryptoKey,pubkey,privkey from Pictures where pic_Id = " + id;
             PreparedStatement stmt = m_Conn.prepareStatement(query);            
             ResultSet result = stmt.executeQuery();
             result.next();
                
             return result;
        }
        
        public ResultSet getGetImagesNumbers(int userId) throws SQLException
        {
             String query = "select pic_id from Pictures";
             PreparedStatement stmt = m_Conn.prepareStatement(query);            
             ResultSet result = stmt.executeQuery();
            // result.next();
                
             return result;            
        }
        
        public int getGetNumberOfImages() throws SQLException
        {
                String query = "select count(*) as 'cnt' from Pictures";
                PreparedStatement stmt = m_Conn.prepareStatement(query);            
                ResultSet result = stmt.executeQuery();                
                result.next();                
                
                return result.getInt("cnt");                            
        }        
        
        public ResultSet getUser(String name) throws SQLException
        {
                String query = "select * from Users where username = '" + name + "'";
                PreparedStatement stmt = m_Conn.prepareStatement(query);            
                ResultSet result = stmt.executeQuery();
                result.next();
                
                return result;
        }        
        
        public boolean doesUserExist(String name) throws SQLException
        {
                String query = "select count(*) as 'cnt' from Users where username = '" + name + "'";
                PreparedStatement stmt = m_Conn.prepareStatement(query);            
                ResultSet result = stmt.executeQuery();                
                result.next();                
                
                int count = result.getInt("cnt");                            
                
                if (count > 0)
                {
                    return true;
                }
                
                return false;
        }        
        
        public void addNewUserToDb(String userName,String password,byte[] salt) throws SQLException
        {
            PreparedStatement pst = m_Conn.prepareStatement("INSERT INTO Users(username,password,salt) VALUES ((?),(?),(?))");                       
            
            pst.setString(1, userName);   
            pst.setString(2, password);   
            pst.setBytes(3, salt);   
                  
            pst.executeUpdate();  
            pst.close();
       }        
       
//        public ResultSet getImagesByQuery(String query)  throws SQLException
//        {                        
//                PreparedStatement stmt = m_Conn.prepareStatement(query);            
//                ResultSet result = stmt.executeQuery();
//                
//                return result;
//       }
              
        public void addImage(FileItem file,String title,String uploadDirectory) throws Exception        
        {                                    
            PreparedStatement pst = m_Conn.prepareStatement("INSERT INTO Pictures(Data,Title,CryptoKey,pubkey,privkey) VALUES ((?),(?),(?),(?),(?))");                       
                                 
            String encFileName = ImgCrypto.EncryptFileData(uploadDirectory + "tmpfile");            
            
            File encFile = new File(ImgCrypto.save_enc_file_name);
            FileInputStream fin = new FileInputStream(encFile);
            
            File pubkeyFile = new File(uploadDirectory + "publickey");
            FileInputStream finpk = new FileInputStream(pubkeyFile);
            
            File privkeyFile = new File(uploadDirectory + "a.keystore");
            FileInputStream finpkv = new FileInputStream(privkeyFile);
                                           
            pst.setBlob(1,fin);                                                                                             
            pst.setString(2, title);     
            pst.setString(3, encFileName);     
            pst.setBlob(4,finpk);                                                                                             
            pst.setBlob(5,finpkv); 
                       
            pst.executeUpdate();            
            
            fin.close();
            pst.close();
            finpkv.close();
            finpk.close();
        }       

//	private String encodeFileToBase64Binary(String fileName)
//			throws IOException {
//
//		File file = new File(fileName);
//		byte[] bytes    = loadFile(file);
//		byte[] encoded  = Base64.encode(bytes);
//		String encodedString = new String(encoded);
//
//		return encodedString;
//	}

	private static byte[] loadFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }
	    byte[] bytes = new byte[(int)length];
	    
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    is.close();
	    return bytes;
	}       
		
	public int isUserAuthenticated(String i_UserName, String i_Password) throws SQLException
	{
		int userId = 0;
		Statement statement = m_Conn.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE username = '" + i_UserName + "' AND password = '" + i_Password + "'");
		
		if (resultSet.next())
		{
			userId = resultSet.getInt("user_id");
			if (userId != 0)
			{				
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
				Date date = new Date();
                                try {
                                //    updateUserStartTime(userId,dateFormat.format(date));
                                }
                                catch(Exception ex)
                                {
                                    
                                }
			}
		}
		
		return userId;
	}
}