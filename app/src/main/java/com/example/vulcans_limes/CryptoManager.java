package com.example.vulcans_limes;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoManager {

    private final String TRANSFORMATION;

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private final String KEY_ALGORITHM;

    private String KEY_NAME;

    private byte[] encryptCipher;

    private KeyStore keyStore;

    private KeyGenerator keyGen;

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     *  Constructor of CryptoManager. Starts initiating the KeyGenerator.
     * @param keyAlgorithm  The asymmetric algorithm to be used with the key
     * @param symAlgorithm  the symmetric algorithm to be used with the key
     * @param hash          the hash algorithm to be used with the key
     * @param keyUsages     a list of intended usages of the key
     */
    public CryptoManager(String keyAlgorithm, String symAlgorithm, String hash, ArrayList<String> keyUsages) {
        KEY_ALGORITHM = keyAlgorithm;
        TRANSFORMATION = KEY_ALGORITHM +
                "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7;
        initKeyGen();
    }

    /**
     * This constructor is for the demo.
     * @param keyAlgorithm
     */
    public CryptoManager(String keyAlgorithm) {
        KEY_ALGORITHM = keyAlgorithm;
        TRANSFORMATION = KEY_ALGORITHM +
                "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7;
        initKeyGen();
    }


    /**
     * This method initializes the KeyGenerator for further use. It gets build with the instructions
     * to generate AES keys, provided by the AndroidKeyStore, saved in the AndroidKeyStore,
     * its purpose is to encrypt or decrypt only with the CBC block module as well as with the
     * PKCS#7 encryption padding scheme.
     *
     * @return true or false, depending on if the KeyGenerator got initialized correctly.
     * @throws Exception                          For catching all out of the ordinary exceptions, this should normally never happen.
     * @throws NoSuchAlgorithmException           if the generation algorithm does not exist, and if the keystore doesn't exist
     * @throws NoSuchProviderException            if the provider does not exist
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
            keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, ANDROID_KEY_STORE);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            System.out.println("KeyGenerator.getInstance() :: " + "initKey(): "
                    + " Error."
                    + " Setting key algorithm failed."
                    + " Exception - " + e.getMessage()
            );
            return false;
        }

        return true;
    }
    /**
     * This method builds the KeyGenerator, then generates a key to be saved inside the KeyStore
     *
     * @return the information (provider and location) of the generated key or just the String "ERROR" if initKeyGen() could not initialize the key.
     * @throws NoSuchAlgorithmException           if the generation algorithm does not exist, and if the keystore doesn't exist
     * @throws InvalidAlgorithmParameterException for faulty or non existent parameters
     * @throws CertificateException               if the keystore cannot load any certificates
     * @throws IOException                        for in and out errors, like the keystore receiving a faulty password
     * @throws Exception                          if anything goes wrong, it will throw an exception.
     */
    public boolean genKey(String key_id) {
        if (keyGen != null) {
            setKEY_NAME(key_id);
            try {
                keyStore.load(null);
                keyGen.init(new
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
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
            try {
                SecretKey secretKey = keyGen.generateKey();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("generateKey() :: " + "initKey(): "
                        + " Error."
                        + " Generating Key failed."
                        + " Exception - " + e.getMessage()
                );
                return false;
            }
        } else return false;
    }

    /**
     * This method encrypts a data byte array
     * @param data the byte array to encrypt
     * @return the encrypted byte array
     * @throws Exception in case the algorithm and providers are not existent, as well as the KEY_NAME
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
     * @throws Exception if the algorithm or provider are not existent, as well as the key. Also throws an Exception if the IV encryptCipher is null.
     */
    public byte[] decryptData(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        keyStore.load(null);
        SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(encryptCipher));
        return cipher.doFinal(encryptedData);
    }

    /**
     * Hashes the given string data using the SHA-256 algorithm.
     *
     * @param data The input string data to be hashed.
     * @return A byte array containing the SHA-256 hash of the data.
     * @throws Exception If an error occurs during hashing.
     */
    public static byte[] hashData(String data) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        //System.out.println(bytesToHex((messageDigest.digest(data.getBytes()))));
        return messageDigest.digest(data.getBytes());

    }

    /**
     * Signs the given string data using the private key from the provided key pair and the specified signature algorithm.
     *
     * @param data    The input string data to be signed.
     * @param keyPair The KeyPair object containing the private key for signing.
     * @return A byte array containing the signature of the data.
     * @throws Exception If an error occurs during signing.
     */

    // wir geben rein ein (byte[])
    public static byte[] signData(String data, KeyPair keyPair) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(keyPair.getPrivate());
        signature.update(data.getBytes());
        System.out.println(Arrays.toString(signature.sign()));
        return signature.sign();
    }


    /**
     * Generates a new RSA key pair with a key size of 2048 bits.
     *
     * @return The generated KeyPair object.
     * @throws Exception If an error occurs during key generation.
     */
    public static KeyPair generateKeyPair() throws Exception {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Verifies if the given signature is valid for the provided data and public key.
     *
     * @param data      The input string data to be verified.
     * @param signature The byte array containing the signature to be verified.
     * @param publicKey The PublicKey object to be used for verification.
     * @return True if the signature is valid, false otherwise.
     * @throws Exception If an error occurs during verification.
     */

    // wir geben rein (byte[] data,byte[] signature) PublicKey wird in der methode gehandelt
    // PublicKey wird in der Methode über KeyName
    public static boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        try {
            Signature verificationSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            verificationSignature.initVerify(publicKey);
            verificationSignature.update(data);
            return verificationSignature.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Byte[] toByte(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        Arrays.setAll(bytes, n -> bytesPrim[n]);
        return bytes;
    }

    /**
     * this method sets KEY_NAME to the key_id for further use (e.g. before generating/loading a key with the given key_id)
     * @param key_id    unique identifier of a key
     */
    public void setKEY_NAME(String key_id) {
        KEY_NAME = key_id;
    }
}
