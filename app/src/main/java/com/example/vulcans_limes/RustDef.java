package com.example.vulcans_limes;

import java.util.ArrayList;
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

    static {
        // This call loads the dynamic library containing the Rust code that we generated.
        System.loadLibrary("vulcanslimes");
    }

    //----------------------------------------------------------------------------------------------
    //Rust methods that can be called from Java

     /*
    Proof of concept - shows type conversion
    DO NOT USE
     */
    static native ArrayList<String> special(ArrayList<Integer> input1, int input2);

    /*
    Proof of concept method - shows callback from Rust to a java method
    DO NOT USE
     */
    static native void callRust();

    /**
     * Is called to start all demo method calls from the Rust side
     * --temporary--
     */
    static native void startDemo();

    //----------------------------------------------------------------------------------------------
    //Java methods that can be called from Rust


    /*
     Demo method - get called from Rust when callRust() gets called
     */
    static void callback() {
        System.out.println("Callback successful");
    }

    /**
     * Creates a new cryptographic key identified by `key_id`.
     * <p>
     * This method generates a new cryptographic key within the TPM, using the specified
     * algorithm, symmetric algorithm, hash algorithm, and key usages. The key is made persistent
     * and associated with the provided `key_id`.
     * @param key_id - String that uniquely identifies the key so that it can be retrieved later
     * @param key_algorithm - The asymmetric encryption algorithm to be used for the key
     * @param sym_algorithm - An optional symmetric encryption algorithm to be used with the key
     * @param hash - An optional hash algorithm to be used with the key
     * @param key_usages - A vector of `AppKeyUsage` values specifying
     *                     the intended usages for the key
     */
    static void create_key(String key_id,
                           String key_algorithm,
                           String sym_algorithm,
                           String hash,
                           ArrayList<String> key_usages) {
        //TODO @Erik
    }

    /**
     * Loads an existing cryptographic key identified by `key_id`.
     * <p>
     * This method loads an existing cryptographic key from the TPM, using the specified
     * algorithm, symmetric algorithm, hash algorithm, and key usages. The loaded key is
     * associated with the provided `key_id`.
     *
     * @param key_id        - String that uniquely identifies the key so that it can be retrieved later
     * @param key_algorithm - The asymmetric encryption algorithm to be used for the key
     * @param sym_algorithm - An optional symmetric encryption algorithm to be used with the key
     * @param hash          - An optional hash algorithm to be used with the key
     * @param key_usages    - A vector of `AppKeyUsage` values specifying
     *                      the intended usages for the key
     */
    static void load_key(String key_id,
                         String key_algorithm,
                         String sym_algorithm,
                         String hash,
                         ArrayList<String> key_usages) {
        //TODO @Erik
    }

    /**
     * Initializes the TPM module and returns a handle for further operations.
     * <p>
     * This method initializes the TPM context and prepares it for use. It should be called
     * before performing any other operations with the TPM.
     *
     * @param key_algorithm - The asymmetric encryption algorithm to be used for the key
     * @param sym_algorithm - An optional symmetric encryption algorithm to be used with the key
     * @param hash          - An optional hash algorithm to be used with the key
     * @param key_usages    - A vector of `AppKeyUsage` values specifying
     *                      the intended usages for the key
     */
    static void initialize_module(String key_algorithm,
                                  String sym_algorithm,
                                  String hash,
                                  ArrayList<String> key_usages) {
        //TODO @Erik
    }

    /**
     * Signs the given data using the key managed by the TPM
     *
     * @param data - A byte array representing the data to be signed
     * @return - The signed data
     */
    static byte[] sign_data(byte[] data) {
        //TODO @Erik
        return null;
    }

    /**
     * Verifies the signature of the given data using the key managed by the TPM
     *
     * @param data      - A byte array representing the data to be verified
     * @param signature - A byte array representing the signature to be verified against the data
     * @return - true if the signature is vaild, false otherwise
     */
    static boolean verify_signature(byte[] data, byte[] signature) {
        //TODO @Erik
        return false;
    }

    /**
     * Encrypts the given data using the key managed by the TPM
     *
     * @param data - a byte array representing the data to be encrypted
     * @return - an ArrayList\<Byte\> containing the encrypted data
     */
    static ArrayList<Byte> encrypt_data(byte[] data) {
        //TODO @Erik
        return null;
    }

    /**
     * Decrypts the given data using the key managed by the TPM
     *
     * @param encrypted_data - a byte array representing the data to be decrypted
     * @return - an ArrayList\<Byte\> containing the encrypted data
     */
    static ArrayList<Byte> decrypt_data(byte[] encrypted_data){
        //TODO @Erik
        return null;
    }
}