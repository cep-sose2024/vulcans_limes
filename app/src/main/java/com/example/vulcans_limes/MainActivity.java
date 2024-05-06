package com.example.vulcans_limes;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

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
 * It also generates a key which, depending on what method gets used, is stored in a KeyStore.
 * This is just an example test app, for testing of our methods.
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
    private Button encButton, decButton, signButton, verifyButton;
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
        signButton = findViewById(R.id.idBtnSign);
        verifyButton = findViewById(R.id.idBtnVerify);

        // Activity for encryption on button press
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
            }
        } );

        // When encrypt button is pressed
        encButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });

        // When decrypt button is pressed
        decButton.setOnClickListener(v -> {
            try {
              // TODO:  decrypt();
                System.out.println("CheckCall");
                byte[] data = new byte[3];
                data[0] = 3;
                System.out.println(Arrays.toString(RustDef.demoEncrypt(data)));
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Fail to decrypt image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        //When sign button is pressed
        signButton.setOnClickListener(v -> {
            try{
                //TODO: sign();


                //Erfolg wird angezeigt
                Snackbar.make(v, "Der String \"Sign me!\" wurde signiert!", Snackbar.LENGTH_SHORT).show();

            } catch (Exception e){
                e.printStackTrace();
            }
        });

        //When verify button is pressed
        verifyButton.setOnClickListener(v -> {
            try{
                //TODO: verify();

                //Erfolgreiche Verifikation wird angezeigt
                Snackbar.make(v, "Erfolgreich verifiziert!", Snackbar.LENGTH_SHORT).show();
            } catch (Exception e){
                e.printStackTrace();
            }
        });


    }

    /**
     * This method gets called upon when an Action is launched (e.g. the encrypt button is pressed)
     * The user picks a picture out of their Media file system
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri imgUri = data.getData();

            String[] filePath = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imgUri, filePath, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePath[0]);

            String picPath = cursor.getString(columnIndex);

            cursor.close();

            try {
            // TODO: Splice File into Byte array for encryption    encrypt(picPath);
                Toast.makeText(this, "Image encrypted..", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Fail to encrypt image : " + e, Toast.LENGTH_SHORT).show();
            }
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
     * to generate AES keys, provided by the AndroidKeyStore, saved in the AndroidKeyStore,
     * its purpose is to encrypt or decrypt only with the CBC block module as well as with the
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