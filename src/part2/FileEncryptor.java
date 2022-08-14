package part2;

import part2.Util;

import java.io.FileOutputStream;
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

/**
 *
 * @author Erik Costlow
 */
public class FileEncryptor {
    private static final Logger LOG = Logger.getLogger(part1.FileEncryptor.class.getSimpleName());

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

    /*
    Additional fields for part 2
     */
    //True if the user specified a decryption IV, false if not
    private static boolean specificDecIV;
    private static boolean customSecretKey;

    public static void fillParams(String[] args) throws IllegalArgumentException {
        OPERATION = args[0]; //First is operation
        switch (OPERATION) {
            case "enc":
                switch(args.length) {
                    case 3:
                        customSecretKey = false;
                        INPUT = Paths.get(args[1]);
                        OUTPUT = Paths.get(args[2]);
                        return;
                    case 4:
                        System.out.println("3 enc arguments detected, interpreting first as base64 encoded Secret Key");
                        customSecretKey = true;
                        SKEY = args[1];
                        INPUT = Paths.get(args[2]);
                        OUTPUT = Paths.get(args[3]);
                        return;
                    default:
                        throw new IllegalArgumentException("Wrong no. of arguments! Expected 2-3: secret key (optional), input, output, got: " + Integer.toString(args.length - 1));
                }
                    case "dec":
                switch (args.length) {
                    case 4:
                        specificDecIV = false;
                        SKEY = args[1]; //Set secret key
                        INPUT = Paths.get(args[2]); //Set input file
                        OUTPUT = Paths.get(args[3]); //Set output path
                        return;
                    case 5:
                        System.out.println("4 dec arguments detected, using second as init vector");
                        specificDecIV = true;
                        SKEY = args[1];
                        INITV = args[2];
                        INPUT = Paths.get(args[3]);
                        OUTPUT = Paths.get(args[4]);
                        return;
                    default:
                        throw new IllegalArgumentException("Wrong no. of dec arguments! 2-3 required, got: " + (args.length - 1));
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

        //Check if the user inputted their own secret key
        if (customSecretKey) {
            assert SKEY != null; //Ensure key was assigned
            key = Base64.getDecoder().decode(SKEY);
            System.out.println("User inputted key= \n" + Base64.getEncoder().encodeToString(key));
        } else System.out.println("Random key=\n" + Base64.getEncoder().encodeToString(key));

        System.out.println("initVector=\n" + Base64.getEncoder().encodeToString(initVector));
//        System.out.println("Random key=" + Util.bytesToHex(key));
//        System.out.println("initVector=" + Util.bytesToHex(initVector));
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        //Write the iv to a file with the same name as the output arg with .iv appended
        String ivPath = OUTPUT.toString()+".iv";
        try(FileOutputStream out = new FileOutputStream(ivPath)) {
            out.write(initVector);
        } catch (IOException e) {
            LOG.log(Level.INFO, "Unable to write IV", e);
        }

        final Path encryptedPath = OUTPUT; //Changed to output path
        try (InputStream fin = Files.newInputStream(INPUT); //Changed to newInputStream of input
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
        //Use saved iv from encryption
        byte[] ivBytes = new byte[16];
        //Read the input encrypted file and append .iv to find the init vector
        ivBytes = Files.readAllBytes(Paths.get(INPUT.toString().concat(".iv")));
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        //If the user chose to specify an IV, then use that instead
        if (specificDecIV) {
            byte[] decodedIV = Base64.getDecoder().decode(INITV);
            iv = new IvParameterSpec(decodedIV);
        }

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
            Logger.getLogger(part1.FileEncryptor.class.getName()).log(Level.SEVERE, "Unable to decrypt", ex);
        }

        LOG.info("Decryption complete, open " + decryptedPath.toAbsolutePath());
    }


    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
        //Handle arguments
        fillParams(args);

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