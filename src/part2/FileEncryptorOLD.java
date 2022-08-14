package part2;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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


/**
 *
 * @author Erik Costlow, extended by Jakob Coker
 *
 */
 class FileEncryptorOLD {
    private static final Logger LOG = Logger.getLogger(FileEncryptorOLD.class.getSimpleName());

    private static final String ALGORITHM = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5PADDING";
    //Allow the user to specify encryption or decryption operation
    private static String OPERATION;
    //Allow the user to specify IO paths
    private static String INPUT;
    private static Path OUTPUT;
    private static boolean customSecretKey;
    private static boolean specificDecIV;
    //Allow the user to specify Secret Key and IV
    private static String SKEY;
    private static String INITV;

    /**
     * Method to handle the arguments and spit IllegalArgumentExceptions
     * @author Jakob
     */
    public static void fillParams(String[] args){
        OPERATION = args[0];
        switch (OPERATION) {
            case "enc":
                switch(args.length) {
                    case 3:
                        INPUT = args[1];
                        OUTPUT = Paths.get(args[2]);
                        return;
                    case 4:
                        System.out.println("3 enc arguments detected, interpreting first as base64 encoded Secret Key");
                        customSecretKey = true;
                        SKEY = args[1];
                        INPUT = args[2];
                        OUTPUT = Paths.get(args[3]);
                        return;
                    default:
                        throw new IllegalArgumentException("Wrong no. of arguments! Expected 2-3: secret key (optional), input, output, got: " + Integer.toString(args.length-1));
                }
            case "dec":
                switch(args.length) {
                    case 4:
                        SKEY = args[1];
                        INPUT = args[2];
                        OUTPUT = Paths.get(args[3]);
                        return;
                    case 5:
                        System.out.println("4 dec arguments detected, using second as init vector");
                        specificDecIV = true;
                        SKEY = args[1];
                        INITV = args[2];
                        INPUT = args[3];
                        OUTPUT = Paths.get(args[4]);
                        return;
                    default:
                        throw new IllegalArgumentException("Wrong no. of arguments! 3 required, got: " + Integer.toString(args.length-1));
                }
            default:
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

        //Check if user inputted their own secret key
        if (customSecretKey) {
            key = Base64.getDecoder().decode(SKEY);
            System.out.println("User inputted key= " + Base64.getEncoder().encodeToString(key));
        }else System.out.println("Random key= " + Base64.getEncoder().encodeToString(key));

        System.out.println("initVector= " + Base64.getEncoder().encodeToString(initVector));
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
        final Path encryptedPath = OUTPUT;
        try (InputStream fin = FileEncryptorOLD.class.getResourceAsStream(INPUT);
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

        //Use saved iv from encryption
        byte[] ivBytes = new byte[16];
        ivBytes = Files.readAllBytes(Paths.get(INPUT.concat(".iv")));
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
         //If the user insists on using a custom initialisation vector, otherwise defaults to the encrypted files
        if (specificDecIV) {
            byte[] decodedIV = Base64.getDecoder().decode(INITV);
            iv = new IvParameterSpec(decodedIV);
        }
        SecretKeySpec skeySpec = new SecretKeySpec(decodedKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        final Path decryptedPath = OUTPUT;


        try(InputStream encryptedData = FileEncryptorOLD.class.getResourceAsStream(INPUT);
            CipherInputStream decryptStream = new CipherInputStream(encryptedData, cipher);
            OutputStream decryptedOut = Files.newOutputStream(decryptedPath)){
            final byte[] bytes = new byte[1024];
            for(int length=decryptStream.read(bytes); length!=-1; length = decryptStream.read(bytes)){
                decryptedOut.write(bytes, 0, length);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileEncryptorOLD.class.getName()).log(Level.SEVERE, "Unable to decrypt", ex);
        }

        LOG.info("Decryption complete, open " + decryptedPath.toAbsolutePath());
    }

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            NoSuchAlgorithmException, IOException, InvalidKeyException {
        fillParams(args);

        switch (OPERATION){
            case "enc":
                encrypt();
                System.exit(0);
            case "dec":
                decrypt();
        }


    }
}