package com.example.vulcans_limes;

import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Main Activity for Android Device App.
 * The class displays and manages the devices screen.
 * It also generates a key which, depending on what method gets used, is stored in a KeyStore
 *
 * @author Erik Fribus
 */
public class MainActivity extends AppCompatActivity {

    public static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
            + KeyProperties.ENCRYPTION_PADDING_PKCS7;
    public static final String KEY_NAME = "key";
    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    byte[] encryptCipher;
    /**
     * @param keyText displaying Text on the devices screen
     */
    TextView keyText;
    /**
     * @param genKeyBtn generates a key upon activation
     */
    Button genKeyBtn;
    /**
     * @param scView for the function of scrolling on the device
     */
    ScrollView scView;
    /**
     * @param keyGen KeyGenerator for generating keys
     */
    KeyGenerator keyGen;
    /**
     * @param keyStore stores keys inside the KeyStore
     */
    KeyStore keyStore;


    @Override
    /**
     * This will run upon starting the app. It initializes the screen with its components.
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyText = findViewById(R.id.keyText);
        genKeyBtn = findViewById(R.id.genkey);
        scView = findViewById(R.id.scrollView);
        // Generates a AES key and prints provider and location info
        //genKeyBtn.setOnClickListener(view -> keyText.setText(keyText.getText() + "\n" + genKey()));
        // Just for Testing the AES Key, giving out all the information
        // genKeyBtn.setOnClickListener(view -> keyText.setText(keyText.getText() + "\n" + keyTestAES()));
        genKey();
        // Testing Encrypt Decript functionality
        byte [] bArray = new byte[] {2, 3, 4, 1};
        byte[] bDecArray;
        byte [] bEncArray;
        System.out.println("Before Encryption: " + Arrays.toString(bArray));
        try {
            bEncArray = encryptData(bArray);
            System.out.println("After Encryption: " + Arrays.toString(bEncArray));
        } catch (Exception e) {
            System.out.println("ERROR could not ENCRYPT!");
            throw new RuntimeException(e);
        }
            try {
                bDecArray = decryptData(bEncArray);
                System.out.println("After Decryption: " + Arrays.toString(bDecArray));
            } catch (Exception e){
                System.out.println("ERROR could not DECRYPT!");
                throw new RuntimeException(e);
            }
            if (Arrays.equals(bArray, bDecArray)){
                System.out.println("IT LIVES!!!");
            } else {
                System.out.println("ITS DEAD :(");
            }
    }

    /**
     * This class has been made for testing. It generates a 16-Byte AES key and builds a String with all the keys information.
     *
     * @return the String of the generated key with all its information like length, the actual key, and the Base64 encoded version.
     * @throws NoSuchAlgorithmException if there is no such algorithm for generating the key.
     */
    public String keyTestAES() {
        SecretKey sk1;
        try {
            sk1 = KeyGenerator.getInstance("AES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] aesKey = sk1.getEncoded();
        return aesKey +
                "\nAES Key length: " + aesKey.length +
                "\nAES Key: " + Arrays.toString(aesKey) +
                "\nAES Key Base64: " + (Base64.getEncoder().encodeToString(aesKey));
    }

    /**
     * This method calls upon initKeyGen() to initialize and build the KeyGenerator, then generates a key in this method.
     *
     * @return the information (provider and location) of the generated key or just the String "ERROR" if initKeyGen() could not initialize the key.
     * @throws Exception if anything goes wrong, it will throw an exception.
     */
    public String genKey() {
        if (initKeyGen()) {
            // Generates the key if everything went smoothly while initiating the keyGen Object
            try {
                Key key = keyGen.generateKey();
                return key.toString();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("generateKey() :: " + "initKey(): "
                        + " Error."
                        + " Generating Key failed."
                        + " Exception - " + e.getMessage()
                );
            }
        }
        return "ERROR";
    }

    /**
     * This method initializes the KeyGenerator for further use. It gets build with the instructions
     * to generate an AES key, provided by the AndroidKeyStore, save it in the AndroidKeyStore,
     * its purpose is to encrypt or decrypt only with the CBC block module aswell as with the
     * PKCS#7 encryption padding scheme.
     *
     * @return true or false, depending on if the KeyGenerator got initialized correctly.
     * @throws Exception                          For catching all out of the ordinary exceptions, this should normally never happen.
     * @throws NoSuchAlgorithmException           if the generation algorithm does not exist, and if the keystore doesnt exist
     * @throws NoSuchProviderException            if the provider does not exist
     * @throws InvalidAlgorithmParameterException for faulty or non existent parameters
     * @throws CertificateException               if the keystore cannot load any certificates
     * @throws IOException                        for in and out errors, like the keystore receiving a faulty password
     */
    public boolean initKeyGen() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("KeyStore.getInstance() :: " + "initKey(): "
                    + " Exception - " + e.getMessage()
            );
            return false;
        }
        try {
            // Creates the KeyGenerator with the AES algorithm
            keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
            // initializes the KeyGenerator with a, probably and hopefully, true random number
            // SecureRandom secRan = new SecureRandom();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            System.out.println("KeyGenerator.getInstance() :: " + "initKey(): "
                    + " Error."
                    + " Setting key algorithm failed."
                    + " Exception - " + e.getMessage()
            );
            return false;
        }
        try {
            keyStore.load(null);
            keyGen.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    //        .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } catch (NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException
                 | CertificateException | IOException e) {
            System.out.println("init() :: " + "initKey(): "
                    + " Error."
                    + " Initiating key failed."
                    + " Exception - " + e.getMessage()
            );
            return false;
        }
        return true;
    }

    /**
     * This method encrypts a data byte array
     * @param data the byte array to encrypt
     * @return the encrypted byte array
     * @throws Exception in case the algorithm and providers are not existend, aswell as the keyname
     */
    public byte[] encryptData(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        keyStore.load(null);
        SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        encryptCipher = cipher.getIV();
        return cipher.doFinal(data);
    }

    /**
     * this method decrypts a data byte array
     * @param encryptedData the encrypted array to decrypt
     * @return the decrypted data byte array
     * @throws Exception if the algorithm or provider are not existend, aswell as the key. Also throws an Exception if the IV encryptCipher is null.
     */
    public byte[] decryptData(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        keyStore.load(null);
        SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(encryptCipher));
        return cipher.doFinal(encryptedData);
    }
}