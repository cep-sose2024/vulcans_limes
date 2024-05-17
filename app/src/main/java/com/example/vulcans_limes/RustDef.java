package com.example.vulcans_limes;


import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * This class provides the method declarations that are the interface for the JNI.
 * The first part are Rust-methods that can be called from other Java-classes,
 * while the second part contains full Java-methods that can be called from Rust.
 * <p>
 * This class also loads the compiled Rust coda as a dynamic library
 * <p>
 * All methods defined in this class hava to have a corresponding method defined in lib.rs,
 * with the same method name and corresponding input and output parameters, according to this table:
 * <p>
 * Rust     	        Java
 * ------------------------
 * i32                 int
 * bool     	        boolean
 * char      	        char
 * i8   	            byte
 * f32   	            float
 * f64   	            double
 * i64   	            long
 * i16   	            short
 * String    	        String
 * Vec<T> 	             ArrayList<T>
 * Box<[u8]> 	        byte[]
 * jni::JObject<'env>  (any Java object as input type)
 * jni::jobject 	    (any Java object as output)
 *
 * @noinspection unused - Methods called from Rust are not recognized as being in use
 */
class RustDef {

    /*
    CryptoManger object for execution of methods
     */
    static CryptoManager cryptoManager;

    static {
        // This call loads the dynamic library containing the Rust code.
        System.loadLibrary("vulcanslimes");
    }

    //----------------------------------------------------------------------------------------------
    //Rust methods that can be called from Java

    /**
     * Proof of concept - shows type conversion
     * DO NOT USE
     */
    static native ArrayList<String> special(ArrayList<Integer> input1, int input2);

    /**
     * Proof of concept method - shows callback from Rust to a java method
     * ONLY USE FOR TESTING
     */
    static native String callRust();

    /**
     * Is called to start all demo method calls from the Rust side
     * --temporary--
     */
    static native byte[] demoEncrypt(byte[] data);

    static native void demoCreate(String key_id, String keyGenInfo);

    static native void demoInit();


    static native byte[] demoDecrypt(byte[] data);

    static native byte[] demoSign(byte[] data);

    static native boolean demoVerify(byte[] data);

    static native void demoLoad(String key_id);

    //----------------------------------------------------------------------------------------------
    //Java methods that can be called from Rust

    /*
     Proof of concept method - get called from Rust when callRust() gets called
        DO NOT USE
     */
    static void callback() {
        System.out.println("Callback successful");
    }

    /**
     * Creates a new cryptographic key identified by `key_id`.
     * <p>
     * This method generates a new cryptographic key within the TPM. The key is made persistent
     * and associated with the provided `key_id`.
     *
     * @param key_id - String that uniquely identifies the key so that it can be retrieved later
     */
    static void create_key(String key_id, String keyGenInfo) throws InvalidAlgorithmParameterException, UnrecoverableKeyException, CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
        cryptoManager.genKey(key_id, keyGenInfo);
    }

    /**
     * Loads an existing cryptographic key identified by `key_id`.
     * <p>
     * This method loads an existing cryptographic key from the TPM. The loaded key is
     * associated with the provided `key_id`.
     *
     * @param key_id - String that uniquely identifies the key so that it can be retrieved later
     */
    static void load_key(String key_id) throws UnrecoverableKeyException, KeyStoreException {
        cryptoManager.loadKey(key_id);
    }

    /**
     * Initializes the TPM module and returns a handle for further operations.
     * <p>
     * This method initializes the TPM context and prepares it for use. It should be called
     * before performing any other operations with the TPM.
     */
    static void initialize_module() throws KeyStoreException {
        cryptoManager = new CryptoManager();

    }

    /**
     * Signs the given data using the key managed by the TPM
     *
     * @param data - A byte array representing the data to be signed
     * @return - The signed data
     */
    static byte[] sign_data(byte[] data) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, SignatureException, InvalidKeyException, InvalidKeySpecException, NoSuchProviderException {
        return cryptoManager.signData(data);
    }

    /**
     * Verifies the signature of the given data using the key managed by the TPM
     *
     * @param data      - A byte array representing the data to be verified
     * @param signature - A byte array representing the signature to be verified against the data
     * @return - true if the signature is vaild, false otherwise
     */
    static boolean verify_signature(byte[] data, byte[] signature) throws SignatureException, KeyStoreException, NoSuchAlgorithmException, InvalidKeyException, UnrecoverableKeyException, InvalidKeySpecException, NoSuchProviderException {
        return cryptoManager.verifySignature(data, signature);
    }

    /**
     * Encrypts the given data using the key managed by the TPM
     *
     * @param data - a byte array representing the data to be encrypted
     * @return - an ArrayList\<Byte\> containing the encrypted data
     */
    static byte[] encrypt_data(byte[] data) throws Exception {
        return cryptoManager.encryptData(data);
    }

    /**
     * Decrypts the given data using the key managed by the TPM
     *
     * @param encrypted_data - a byte array representing the data to be decrypted
     * @return - an ArrayList\<Byte\> containing the encrypted data
     */
    static byte[] decrypt_data(byte[] encrypted_data) throws InvalidAlgorithmParameterException, UnrecoverableKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, CertificateException, NoSuchAlgorithmException,
            IOException, KeyStoreException, BadPaddingException, InvalidKeySpecException, InvalidKeyException,
            NoSuchProviderException {
        return cryptoManager.decryptData(encrypted_data);
    }
}