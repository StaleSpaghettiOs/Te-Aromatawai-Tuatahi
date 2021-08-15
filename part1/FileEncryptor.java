import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.IIOException;


/**
 *
 * @author Erik Costlow, extended by Jakob Coker
 *
 */
public class FileEncryptor {
    private static final Logger LOG = Logger.getLogger(FileEncryptor.class.getSimpleName());

    private static final String ALGORITHM = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5PADDING";
    //Allow the user to specify encryption or decryption operation
    private static String OPERATION;
    //Allow the user to specify IO paths
    private static String INPUT;
    private static Path OUTPUT;
    //Allow the user to specify Secret Key and IV in Base64 (decryption only)
    private static String SKEY;
    private static String INITV;

    /**
     * Method to handle the arguments and spit IllegalArgumentExceptions
     * @author Jakob
     */
    public static void fillParams(String[] args){
        OPERATION = args[0];
        if(OPERATION.compareTo("enc") == 0) {
            if(args.length == 3) {
                INPUT = args[1];
                OUTPUT = Paths.get(args[2]);
            } else {
                System.out.println("Incorrect number of parameters for encryption operation");
            }
        } else if (OPERATION.compareTo("dec") == 0){
            if ( args.length == 5) {
                SKEY = args[1];
                System.out.println("Secret key = " + SKEY);
                INITV = args[2];
                System.out.println("INIT vector = " + INITV);
                INPUT = args[3];
                OUTPUT = Paths.get(args[4]);
            } else {
                throw new IllegalArgumentException("Wrong no. of arguments! Expected 4, got: " + Integer.toString(args.length-1));
            }
        } else {
            throw new IllegalArgumentException("Incorrect operand, please use enc or dec to specify encryption or decryption operation.");
        }

    }

    public static void encrypt() throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        SecureRandom sr = new SecureRandom();
        byte[] key = new byte[16];
        sr.nextBytes(key); // 128 bit key
        byte[] initVector = new byte[16];
        sr.nextBytes(initVector); // 16 bytes IV
        System.out.println("Random key= " + Base64.getEncoder().encodeToString(key));
        System.out.println("initVector= " + Base64.getEncoder().encodeToString(initVector));
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);


        final Path tempDir = Files.createTempDirectory("packt-crypto");
        final Path encryptedPath = OUTPUT;
        try (InputStream fin = FileEncryptor.class.getResourceAsStream(INPUT);
             OutputStream fout = Files.newOutputStream(encryptedPath);
             CipherOutputStream cipherOut = new CipherOutputStream(fout, cipher) {
             }) {
            final byte[] bytes = new byte[1024];
            for(int length=fin.read(bytes); length!=-1; length = fin.read(bytes)){
                cipherOut.write(bytes, 0, length);
            }
        } catch (IOException e) {
            LOG.log(Level.INFO, "Unable to encrypt", e);
        }
        LOG.info("Encryption finished, saved at " + encryptedPath.toAbsolutePath());
    }

    public static void decrypt() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            NoSuchAlgorithmException, IOException, InvalidKeyException {
        //Decode the user inputted base64 encoded IV and key into byte arrays
        byte[] decodedKey = Base64.getDecoder().decode(SKEY);
        byte[] decodedIV = Base64.getDecoder().decode(INITV);
        IvParameterSpec iv = new IvParameterSpec(decodedIV);
        SecretKeySpec skeySpec = new SecretKeySpec(decodedKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER);

        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        final Path decryptedPath = OUTPUT;
        try(InputStream encryptedData = FileEncryptor.class.getResourceAsStream(INPUT);
            CipherInputStream decryptStream = new CipherInputStream(encryptedData, cipher);
            OutputStream decryptedOut = Files.newOutputStream(decryptedPath)){
            final byte[] bytes = new byte[1024];
            for(int length=decryptStream.read(bytes); length!=-1; length = decryptStream.read(bytes)){
                decryptedOut.write(bytes, 0, length);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileEncryptor.class.getName()).log(Level.SEVERE, "Unable to decrypt", ex);
        }

        LOG.info("Decryption complete, open " + decryptedPath);
    }

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            NoSuchAlgorithmException, IOException, InvalidKeyException {
        if (args.length > 0) fillParams(args);
        else {
            System.out.println("Missing parameters");
            System.exit(0);
        }
        switch (OPERATION){
            case "enc":
                encrypt();
                System.exit(0);
            case "dec":
                decrypt();
        }


    }
}