package Cryptography;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class ImgCrypto {
        
    // The defualt properites, usualy obtained from config file
    // but in any case of failure the values is taken as they are written here...
    public static int blockSize = 16;     
    public static String upload_direcotry = "/home/developer/temp";
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
    
    //function writes encrypted file into the Disk file
    //the file includes the signature at the end of it
    public static void Write_encrypted_file(String _input_file_name,String _output_file_name,Cipher encryptCipher,byte[] signature) throws FileNotFoundException, IOException, Exception
    {
        //needs to save the encrypted data        
        FileInputStream fis = new FileInputStream(_input_file_name);        
        FileOutputStream fos = new FileOutputStream(_output_file_name);
        
        if (fis == null || fos == null) //validation check
        {
            throw new Exception("error reading files!");
        }
                
        //encrypt the to io stream
        try 
        {
            CipherOutputStream cos = new CipherOutputStream(fos, encryptCipher);
                    
            byte[] block = new byte[8];     //block size can be changed        
            int i = 0;

            //read all file
            while ((i = fis.read(block)) != -1) 
            {
                cos.write(block, 0, i); //writes block to file stream
            }
            
            cos.write(signature, 0, signature.length); //write signature to saved file (included inside the file)
            cos.close(); //close the resource, remove handler
            fos.close();            
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            throw ex; 
        }                
    }
    
     //function for calculating the signature (digist) of given dat file
     private static byte[] Sign(String datafile, PrivateKey prvKey,String sigAlg) throws Exception 
     {     
        Signature sig = Signature.getInstance(sigAlg);  //initiation of signature process
        sig.initSign(prvKey); //initiation of signature object with private key
        FileInputStream fis = new FileInputStream(datafile); //gets the input stream

        byte[] dataBytes = new byte[1024]; //size can be changed 

        int nread = fis.read(dataBytes); //gets the first chunck of data
        while (nread > 0)  //read the whole file until end of it
        {
          sig.update(dataBytes, 0, nread);
          nread = fis.read(dataBytes);
        }

        return sig.sign(); //return signature diggest value
      }   
    
     //function for loading config file properites
     public static String load_property(Properties configFile,String key)
     {
          String value; //init

          try
          {
            value = configFile.getProperty(key); //get the value by key
          }
          catch(Exception ex)
          {
              System.out.println(ex.toString());
              return ""; ///return empty string
          }

          return value; //return the value of given key
      }
 
     
    public static String  EncryptFileData(String load_file_name) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, 
            InvalidAlgorithmParameterException, FileNotFoundException, IOException, Exception 
    {
        
        //gets user configuration file
        Properties configFile = new Properties();   //init prop object
        //String config_file_path_location = "C:\\Users\\kfirsa\\Documents\\NetBeansProjects\\TwoStepGUI\\config_file.properties"; //get config file
                                        
        //part 1
        //generate secret key for program process
        //----------------------------------------------------------------------------
        SecureRandom secRandom = SecureRandom.getInstance(secure_random_value);
        secRandom.setSeed(0); //startng random file
        
        byte[] generatedKey = new byte[blockSize];
        
        secRandom.nextBytes(generatedKey);  //generate random value key
        //----------------------------------------------------------------------------
        //stores the Generated key & IV into config file
        //----------------------------------------------------------------------------
        
        //must encode generatedKey and IV to base64 to save them to config file !
        //----------------------------------------------------------------------------
        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();  //must be converted to base64             
        configFile.setProperty("GEN_KEY", encoder.encode(generatedKey)); //save to config file

        //init the Cipher algorithem (symetric key for data encryption)
        //----------------------------------------------------------------------------
        Cipher encryptCipher = Cipher.getInstance(aes_type);
       
        // take empty IV for simplicity ....
        byte[] iv_encoded= { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        
        //generatedKey.                
        SecretKey keyValue = new SecretKeySpec(generatedKey,"AES");   //create the real key  
        AlgorithmParameterSpec IVspec = new IvParameterSpec(iv_encoded);      //create the IV object     
        encryptCipher.init(Cipher.ENCRYPT_MODE, keyValue, IVspec);    //init the cipher for encrypting of whole file
        
        //creates public / private keys using key generating algoritm
        //==============================================================
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(key_gen_algorithem);                        
        kpg.initialize(rsa_key_size);  //init the object with key-size
        KeyPair kp = kpg.genKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();         

        //stores the public key into file (to be used in reader program)
        //==============================================================        
        FileOutputStream fos = new FileOutputStream(pk_file_name);
        ObjectOutputStream oos = new ObjectOutputStream(fos);             
        oos.writeObject(publicKey);
        oos.close();
        
        //key store logic
        //=================================================================
        //get keystore filename
        String keystoreFilename = keystore_file_name;
        //get keystore password
        char[] password = keystore_password.toCharArray(); //sets keystore password

        FileOutputStream fOut = new FileOutputStream(keystoreFilename); //output stream object
        KeyStore keystore = KeyStore.getInstance(key_store_type); //gets instance
        
        keystore.load(null, password); //creates new, blank keystore file 
        
        //store private key in KeyStore::::
        //================================================================
        byte[] key_data = privateKey.getEncoded(); //encode private key for saving in store
        SecretKey sk = new SecretKeySpec(key_data,0,key_data.length,key_gen_algorithem);        
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sk);              
        keystore.setEntry("secretKeyAlias", skEntry, new KeyStore.PasswordProtection(password));
                         
        keystore.store(fOut, password); //store private key enveloped with SecretKey !                          
        
        //======================================================================
        byte[] signature = Sign(load_file_name, privateKey,signature_algo); //addes signature/hash to given file        
        
        //save the encrypted file on the disk
        Write_encrypted_file(load_file_name,save_enc_file_name,encryptCipher,signature);        

        // configFile.store(new FileOutputStream(config_file_path_location),null); //saves the changes to config file        
        
        return encoder.encode(generatedKey);
    }     
}
