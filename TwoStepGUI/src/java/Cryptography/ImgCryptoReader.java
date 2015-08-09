/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Cryptography;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author kfirsa
 */
public class ImgCryptoReader {
       // The defualt properites, usualy obtained from config file
    // but in any case of failure the values is taken as they are written here...
    public static int blockSize = 16; 
    public static String upload_direcotry = "D:\\temp\\";
    public static String keystore_file_name = upload_direcotry + "a.keystore";
    public static String keystore_password = "password";
    public static String signature_algo = "MD5withRSA";    
    public static String save_enc_file_name = upload_direcotry + "file_enc";
    public static String save_dec_file_name = upload_direcotry + "file_dec";    
    public static String pk_file_name = upload_direcotry + "publickey"; 
    public static int rsa_key_size = 512;    
    public static String secure_random_value = "SHA1PRNG";
    public static String key_gen_algorithem = "RSA";
    public static String key_store_type = "JCEKS";
    public static String aes_type = "AES/CBC/PKCS5Padding";
    //public static String config_file_name = "C:\\Users\\kfirsa\\Documents\\NetBeansProjects\\TwoStepGUI\\config_file.properties";  
    
    //function for writing decoded file with digiest validation checking
    public static boolean Write_decrypted_file(String _input_file_name, String _output_file_name,Cipher decryptCipher,PublicKey publicKey)
            throws FileNotFoundException, IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, Exception
    {
        FileInputStream fis = new FileInputStream(_input_file_name); 
        CipherInputStream cis = new CipherInputStream(fis, decryptCipher);
        FileOutputStream fos = new FileOutputStream(_output_file_name);
        
        if (fis == null || fos == null || cis == null) //validation check
        {
            throw new Exception("error reading files!");
        }
                                
        //get encrypted file length
        //-------------------------------------------------
        File file = new File(_input_file_name);
        long input_file_length = file.length(); //with garbidge inside!       
        
        //-------------------------------------------------
        byte[] block = new byte[1]; //init block size for reading data
        
        //used to allocate memory stream array
        ByteArrayOutputStream _mem = new ByteArrayOutputStream((int)input_file_length);
        
        //calcuate the real length of encrypted file
        int i = 0;
        long file_length=0; 
        while ((i = cis.read(block)) != -1) {   //load all file data
             _mem.write(block); //store binary block into memory stream
             file_length++;
        }                
         
        _mem.close(); //close stream writing
        
        cis.close();
        fis.close();
        
        //copy byte array from the decrypted file
        //the idea is to seperate the data and the siganture
        byte[] sigbytes = CopyMemory((int)file_length-64,64,_mem.toByteArray()); //extract signature part from the file
        byte[] data = CopyMemory(0,(int)file_length-64,_mem.toByteArray()); //extract data part from the file                  
        
        if (Verify(publicKey,signature_algo, sigbytes, data)) //verifies the file signature
        {
            fos.write(data); //dump the data part to hdd file
            fos.close();     //close writer
            return true;     //done okay
        }
        
        fos.close();
        return false;  //had error, no file was created!
     }
    
  //helper function for copying memory between two arrays
  private static byte[] CopyMemory(int start_pos, int length, byte[] src) throws IOException
  {

        byte[] result = new byte[length];      //allocate memory
        int k=0;
      
        for (int j = start_pos; j < (start_pos+length); j++)  //copy selected part only
        {
              result[k++] = src[j]; //only selected part!
        }                
      
       return result; //return the reuslt
   }    

  //function for loading config file properites
  public static String load_property(Properties configFile,String key)
  {
      String value;
      
      try
      {
        value = configFile.getProperty(key);
      }
      catch(Exception ex)
      {
          System.out.println(ex.toString());
          return "";
      }
     
      return value;
  }
 //function for verifing the data signature
  private static boolean Verify(PublicKey pubKey,String sigAlg, byte[] sigbytes,byte[] input) throws Exception
  {
    Signature sig = Signature.getInstance(sigAlg); //initiation of signature process

    sig.initVerify(pubKey);        
    sig.update(input); //the data
    
    return sig.verify(sigbytes);
  }    
    
    public static void LoadImageFromDb(String fileName, String cipherKey_base64,String decFileName) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, 
            InvalidAlgorithmParameterException, FileNotFoundException, IOException, Exception 
    {
        save_dec_file_name = decFileName;
        //gets user configuration file
        Properties configFile = new Properties();   //init prop object
        
        //String config_file_path_location = config_file_name;                        
        //String config_file_path_location = "C:\\Users\\kfirsa\\Documents\\NetBeansProjects\\TwoStepGUI\\config_file.properties";
        
        save_enc_file_name = upload_direcotry + "encpic";
                //fileName;
                
        try
        {
//            configFile.load(new FileInputStream(config_file_path_location));  //load the file
//        
//            //load properites from file
//            blockSize = Integer.parseInt(load_property(configFile,"BLOCK_SIZE")); //get the prop. from file
//            rsa_key_size = Integer.parseInt(load_property(configFile,"RSA_KEY_SIZE")); //get the prop. from file
//            keystore_file_name = load_property(configFile,"KEYSTORE_FILE_NAME");     
//            keystore_password = load_property(configFile,"KEYSTORE_PASSWORD");
//            signature_algo = load_property(configFile,"SIGNATURE_ALGO");
//            load_file_name = load_property(configFile,"LOAD_FILE_NAME");
//            save_enc_file_name = "c://temp//encrypted_file2";//load_property(configFile,"SAVE_ENC_FILE_NAME");
//            save_dec_file_name = load_property(configFile,"SAVE_DEC_FILE_NAME");                
//            pk_file_name = load_property(configFile,"PK");                
//            secure_random_value = load_property(configFile,"SECURE_RANDOM_VALUE");
//            key_gen_algorithem = load_property(configFile,"KEY_GEN_ALGORITHEM");
//            key_store_type = load_property(configFile,"KEY_STORE_TYPE");       
//            aes_type = load_property(configFile,"AES_TYPE"); 
        }
        catch (Exception ex) //in case of exception gets the deafult proprerties
        {
            System.out.println(ex.toString());
        }
                     
        //gets public key from file
        //===============================================================
        FileInputStream fis = new FileInputStream(pk_file_name);
	ObjectInputStream ois = new ObjectInputStream(fis);       
        PublicKey publicKey = (PublicKey)ois.readObject();                                
                                         
        //key store logic
        //=================================================================
        //get keystore filename
        KeyStore keystore = KeyStore.getInstance(key_store_type); //gets instance        
                
        //gets keystore password from config file
        char[] password = keystore_password.toCharArray(); 
        
        //loads the keystore file
        //=================================================================
        fis = null;
        try {
            fis = new java.io.FileInputStream(keystore_file_name);
            keystore.load(fis, password);
        } finally {
            if (fis != null) {
                fis.close();
            }
            else
            {              
                throw new Exception("Cannot obtain keystore password!");
            }
        }        
                        
        //restore private key from KeyStore::::
        SecretKey skEntry = (SecretKey)keystore.getKey("secretKeyAlias", password);
                        
        //gets the private key from keystore 
        byte[] key_bytes = skEntry.getEncoded();
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(key_bytes); //decode teh private key
        KeyFactory kf = KeyFactory.getInstance(key_gen_algorithem);
        PrivateKey privateKey = kf.generatePrivate(privateKeySpec);   //regenerate the private key                                                                                     

        //get AES algorithem properties from config file
        //byte[] b1 = new byte[blockSize];
        //String cipherKey_base64 = "BxpON9VPzeULml12J9lSkQ==";//load_property(configFile,"GEN_KEY");   
        //String aesIV_base64 = "AAAAAAAAAAAAAAAAAAAAAA==";//load_property(configFile,"IV");   
                
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder(); //gets the decoder intance
                
        //decode base64 string to byte array
        byte[] aes_encoded = decoder.decodeBuffer(cipherKey_base64);                                                                      

        byte[] iv_encoded= { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                //decoder.decodeBuffer(aesIV_base64);     
        
        //gets the key and iv of AES algorithem
        SecretKey keyValue = new SecretKeySpec(aes_encoded,"AES");   //create the real key  
        AlgorithmParameterSpec IVspec = new IvParameterSpec(iv_encoded);      //create the IV object     
                                
        Cipher encryptCipher = Cipher.getInstance(aes_type);
        Cipher cipher = Cipher.getInstance(key_gen_algorithem);                   
        cipher.init(Cipher.DECRYPT_MODE, privateKey); //opens enceyption decrypt private key
        encryptCipher.init(Cipher.DECRYPT_MODE, keyValue, IVspec);
                    
        //create decoded file !
        boolean status = Write_decrypted_file(save_enc_file_name,save_dec_file_name,encryptCipher,publicKey);       
        
        //boolean status = Write_decrypted_file(save_enc_file_name,save_dec_file_name,p,publicKey);       
         
         if (status) {
            System.out.printf("The file was decoded !\n");
         }
         else {
             System.out.printf("Invalid digiest! file failed!\n");
         }    
    }
}
