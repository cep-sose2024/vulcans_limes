package com.example.vulcans_limes;


import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

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
        // This call loads the dynamic library containing the Rust code.
        System.loadLibrary("vulcanslimes");

    }

    //----------------------------------------------------------------------------------------------
    //Rust methods that can be called from Java

     /**
    Proof of concept - shows type conversion
    DO NOT USE
     */
    static native ArrayList<String> special(ArrayList<Integer> input1, int input2);

    /**
    Proof of concept method - shows callback from Rust to a java method
    ONLY USE FOR TESTING
     */
    static native String callRust();

    static native boolean demoCreate(String keyName);

    static native byte[] demoEncrypt(byte[] data);

    static native byte[] demoDecrypt(byte[] data);

    static native byte[] demoSign(byte[] data);

    static native boolean demoVerify(byte[] data);

    //----------------------------------------------------------------------------------------------
    //Java methods that can be called from Rust

    /*
    CryptoManger object for execution of methods
     */
    static CryptoManager cryptoManager;

    /*
     Proof of concept method - get called from Rust when callRust() gets called
        DO NOT USE
     */
    public void callback() {
        System.out.println("Callback successful");
    }

    /**
     * Creates a new cryptographic key identified by `key_id`.
     * <p>
     * This method generates a new cryptographic key within the TPM. The key is made persistent
     * and associated with the provided `key_id`.
     * @param key_id - String that uniquely identifies the key so that it can be retrieved later
     */
    static boolean create_key(String key_id) {
        return cryptoManager.genKey(key_id);
    }

    /**
     * Loads an existing cryptographic key identified by `key_id`.
     * <p>
     * This method loads an existing cryptographic key from the TPM. The loaded key is
     * associated with the provided `key_id`.
     *
     * @param key_id - String that uniquely identifies the key so that it can be retrieved later
     */
    static void load_key(String key_id) {
//        cryptoManager.setKEY_NAME(key_id);
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
        //TODO @Erik MUST implement asymmetric encrytion in CryptoManager
        cryptoManager = new CryptoManager(key_algorithm, sym_algorithm, hash, key_usages);
    }

    /**
     * Signs the given data using the key managed by the TPM
     *
     * @param data - A byte array representing the data to be signed
     * @return - The signed data
     */
    static String sign_data(byte[] data) {
        //TODO @Erik implement signing of data in CryptoManager
        byte[] signedData = cryptoManager.signData(data);
        System.out.println("Recieved data in sign_data: "+ Arrays.toString(data));
        return byteToString(signedData);
    }


    /**
     * Verifies the signature of the given data using the key managed by the TPM
     *
     * @param data      - A byte array representing the data to be verified
     * @param signature - A byte array representing the signature to be verified against the data
     * @return - true if the signature is vaild, false otherwise
     */
    static boolean verify_signature(byte[] data, byte[] signature) {
        //TODO @Erik implement veryfication of signatures in CryptoManager
        return cryptoManager.verifySignature(data, signature);
    }


    /**
     * Encrypts the given data using the key managed by the TPM
     *
     * @param data - a byte array representing the data to be encrypted
     * @return - an ArrayList\<Byte\> containing the encrypted data
     */
    static String encrypt_data(byte[] data) {
        //TODO change return type to String with byteToString call
//        return new ArrayList<>(Arrays.asList(cryptoManager.toByte(cryptoManager.encryptData(data))));
        System.out.println("Recieved data in sign_data: " + Arrays.toString(data));
        byte[] result = new byte[data.length+1];
        System.arraycopy(data, 0, result, 0, data.length);
        result[data.length] = (byte) 242;


        return byteToString(result);
    }

    /**
     * Decrypts the given data using the key managed by the TPM
     *
     * @param encrypted_data - a byte array representing the data to be decrypted
     * @return - an ArrayList\<Byte\> containing the encrypted data
     */
    static String decrypt_data(byte[] encrypted_data) throws Exception {
        //TODO change return type to String with byteToString call
        return byteToString(cryptoManager.decryptData(encrypted_data));
    }

    //----------------------------------------------------------------------------------------------
    // Utility functions

    /**
     * This function converts a byte[] array into a String
     * by turning each byte into a hex value comprised of two digits. These are then separated from
     * the next byte by a "/".
     * This provides the necessary format so that interpret_result() in lib.rs can interpret the
     * returning data
     * @param data - the data to be converted
     * @return a String representing the data where each byte is represented by
     *         two hex digits and separated by "/"
     */
    private static String byteToString(byte[] data){
        StringBuilder result = new StringBuilder();
        for (byte datum : data) {
            result.append(String.format("%02X", datum)).append("/");
        }
        return result.toString();
    }
}