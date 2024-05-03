package com.example.vulcans_limes;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
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

    public static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES +
                                                "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                                                 + KeyProperties.ENCRYPTION_PADDING_PKCS7;
    public static final String KEY_NAME = "key";
    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    byte[] encryptCipher;

    KeyStore keyStore;

    KeyGenerator keyGen;

    private ImageView imageView;
    private Button encButton, decButton;
    private ActivityResultLauncher<Intent> launcher;



    @Override
    /**
     * This will run upon starting the app. It initializes the screen with its components.
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.idIVimage);
        encButton = findViewById(R.id.idBtnEncrypt);
        decButton = findViewById(R.id.idBtnDecrypt);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
            }
        } );

        encButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });

        decButton.setOnClickListener(v -> {
            try {
              // TODO:  decrypt();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Fail to decrypt image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // on below line getting image uri
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            // on below line getting image uri.
            Uri imgUri = data.getData();

            // on below line getting file path
            String[] filePath = {MediaStore.Images.Media.DATA};

            // on below line creating a cursor and moving to next.
            Cursor cursor = getContentResolver().query(imgUri, filePath, null, null, null);
            cursor.moveToFirst();

            // on below line creating an index for column
            int columnIndex = cursor.getColumnIndex(filePath[0]);

            // on below line creating a string for path.
            String picPath = cursor.getString(columnIndex);

            // on below line closing our cursor.
            cursor.close();

            // on below line we are encrypting our image.
            try {
                encrypt(picPath);
                // on below line we are encrypting our image.
                Toast.makeText(this, "Image encrypted..", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Fail to encrypt image : " + e, Toast.LENGTH_SHORT).show();
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