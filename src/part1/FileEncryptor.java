package part1;

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
import part1.Util;

/**
 *
 * @author Erik Costlow
 */
public class FileEncryptor {
    private static final Logger LOG = Logger.getLogger(FileEncryptor.class.getSimpleName());

    private static final String ALGORITHM = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5PADDING";

    /*
    Additional fields for part 1
     */
    //For storing enc or dec operation
    private static String OPERATION;
    //For storing input and output paths
    private static Path INPUT;
    private static Path OUTPUT;
    //For storing secret key and IV
    private static String SKEY;
    private static String INITV;

    public static void fillParams(String[] args) throws IllegalArgumentException {
        OPERATION = args[0]; //First is operation
        switch (OPERATION) {
            case "enc":
                switch (args.length) {
                    case 3:
                        INPUT = Paths.get(args[1]);
                        OUTPUT = Paths.get(args[2]);
                        return;
                    default:
                        throw new IllegalArgumentException("Error: Expected 2 arguments following enc keyword, got " + (args.length - 1));
                }
            case "dec":
                switch (args.length) {
                    case 5:
                        SKEY = args[1]; //Set secret key
                        INITV = args[2]; //Set initialisation vector
                        INPUT = Paths.get(args[3]); //Set input file
                        OUTPUT = Paths.get(args[4]); //Set output path
                        System.out.println("Using secret key = " + SKEY); //Display secret key
                        System.out.println("Using init vector = " + INITV); //Display initialisation vector
                        return;
                    default:
                        throw new IllegalArgumentException("Wrong no. of arguments! 3 required, got: " + (args.length - 1));
                }
            default:
                throw new IllegalArgumentException("Incorrect operand, please use enc or dec to specify operation.");
        }
        }


    /*
    Moved encryption operation to its own method
     */
    public static void encrypt() throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        //This snippet is literally copied from SymmetrixExample
        SecureRandom sr = new SecureRandom();
        byte[] key = new byte[16];
        sr.nextBytes(key); // 128 bit key
        byte[] initVector = new byte[16];
        sr.nextBytes(initVector); // 16 bytes IV
        //Changed to return a copyable base64 string
        System.out.println("Random key=\n" + Base64.getEncoder().encodeToString(key));
        System.out.println("initVector=\n" + Base64.getEncoder().encodeToString(initVector));
//        System.out.println("Random key=" + Util.bytesToHex(key));
//        System.out.println("initVector=" + Util.bytesToHex(initVector));
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        final Path encryptedPath = OUTPUT; //Changed to output path
        try (InputStream fin = Files.newInputStream(INPUT); //Changed to stream of input
             OutputStream fout = Files.newOutputStream(encryptedPath);
             CipherOutputStream cipherOut = new CipherOutputStream(fout, cipher) {
             }) {
            final byte[] bytes = new byte[1024];
            for(int length = fin.read(bytes); length!=-1; length = fin.read(bytes)){
                cipherOut.write(bytes, 0, length);
            }
        } catch (IOException e) {
            LOG.log(Level.INFO, "Unable to encrypt", e);
        }

        LOG.info("Encryption finished, saved at " + encryptedPath.toAbsolutePath());
    }

    /*
    Moved decryption operation to its own method
     */
    public static void decrypt() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            NoSuchAlgorithmException, IOException, InvalidKeyException {
        //Decode the user inputted base64 encoded IV and key into byte arrays
        byte[] decodedKey = Base64.getDecoder().decode(SKEY);
        byte[] decodedIV = Base64.getDecoder().decode(INITV);
        IvParameterSpec iv = new IvParameterSpec(decodedIV);
        SecretKeySpec skeySpec = new SecretKeySpec(decodedKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER);

        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        final Path decryptedPath = OUTPUT; //Changed to OUTPUT
        try(InputStream encryptedData = Files.newInputStream(INPUT);
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


    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        /*
        New code here
        */
        if(args.length >= 1){ //Make sure arguments are given
            fillParams(args);
        } else {
            System.out.println("No parameters specified. Program exiting.");
            System.exit(0);
        }
        //Switch case to execute enc or dec
        switch (OPERATION) {
            case "enc":
                encrypt();
                return;
            case "dec":
                decrypt();
            default:
                System.exit(0);
        }

    }
}