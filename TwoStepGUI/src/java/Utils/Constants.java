package Utils;

public class Constants 
{
    public static String UPLOAD_DIRECTORY = "/home/developer/temp";
    public static int NUMBER_OF_PICTURES_TO_SELECT = 3;        
    
    public static int blockSize = 16; 
    public static int rsa_key_size = 512;    
    public static String upload_direcotry = Constants.UPLOAD_DIRECTORY;    
    public static String keystore_file_name = upload_direcotry + "a.keystore";
    public static String keystore_password = "password";
    public static String signature_algo = "MD5withRSA";    
    public static String save_enc_file_name = upload_direcotry + "file_enc";
    public static String save_dec_file_name = upload_direcotry + "file_dec";    
    public static String pk_file_name = upload_direcotry + "publickey";     
    public static String secure_random_value = "SHA1PRNG";
    public static String key_gen_algorithem = "RSA";
    public static String key_store_type = "JCEKS";
    public static String aes_type = "AES/CBC/PKCS5Padding";    
}
