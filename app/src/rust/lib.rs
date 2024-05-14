use robusta_jni::bridge;

#[bridge]
pub mod jni {
    #[allow(unused_imports)]
    use robusta_jni::bridge;
    use robusta_jni::convert::{IntoJavaValue, Signature, TryFromJavaValue, TryIntoJavaValue};
    use robusta_jni::jni::errors::Error;
    use robusta_jni::jni::errors::Result as JniResult;
    use robusta_jni::jni::JNIEnv;
    use robusta_jni::jni::objects::{AutoLocal, JString, JValue};

    #[derive(Signature, TryIntoJavaValue, IntoJavaValue, TryFromJavaValue)]
    #[package(com.example.vulcans_1limes)]
    pub struct RustDef<'env: 'borrow, 'borrow> {
        #[instance]
        pub raw: AutoLocal<'env, 'borrow>,
    }

    /// This Implementation provides the method declarations that are the interface for the JNI.
    /// The first part are Rust-methods that can be called from other Java-classes,
    /// while the second part contains Java-methods that can be called from Rust.
    ///
    /// All method signatures have to correspond to their counterparts in RustDef.java, with the
    /// same method name and corresponding parameters according to this table:
    /// | **Rust**                                           | **Java**                          |
    /// |----------------------------------------------------|-----------------------------------|
    /// | i32                                                | int                               |
    /// | bool                                               | boolean                           |
    /// | char                                               | char                              |
    /// | i8                                                 | byte                              |
    /// | f32                                                | float                             |
    /// | f64                                                | double                            |
    /// | i64                                                | long                              |
    /// | i16                                                | short                             |
    /// | String                                             | String                            |
    /// | Vec\<T\>                                           | ArrayList\<T\>                    |
    /// | Box\<\[u8]\>                                        | byte[]                            |
    /// | [jni::JObject<'env>](jni::objects::JObject)        | *(any Java object as input type)* |
    /// | [jni::jobject](jni::sys::jobject)                  | *(any Java object as output)*     |
    /// |----------------------------------------------------------------------------------------|
    #[allow(non_snake_case)]
    impl<'env: 'borrow, 'borrow> RustDef<'env, 'borrow> {

        //------------------------------------------------------------------------------------------
        // Rust methods that can be called from Java

        ///Proof of concept - shows type conversion
        ///     DO NOT USE
        pub extern "jni" fn special(mut input1: Vec<i32>, input2: i32) -> Vec<String> {
            input1.push(input2);
            input1.push(42);
            input1.iter().map(ToString::to_string).collect()
        }

        ///Proof of concept method - shows callback from Rust to a java method
        ///     ONLY USE FOR TESTING
        pub extern "jni" fn callRust(environment: &'borrow JNIEnv<'env>) -> String {
            // return String::from("Called");
            let class = environment.find_class("com/example/vulcans_limes/RustDef").unwrap();
            if environment.exception_check().unwrap() {
                environment.exception_describe().expect("Describe failed");
                environment.exception_clear().expect("Clear failed");
                return String::from("Exception1")
            }

            let result = Self::callback(&environment);
            if environment.exception_check().unwrap() {
                eprintln!("Test");
                environment.exception_describe().expect("Describe failed");
                environment.exception_clear().expect("Clear failed");
                return String::from("Exception2")
            }
            // return match result {
            //     Ok(_) => String::from("Success"),
            //     Err(_) => String::from("Failure")
            // };
            return String::from("End reached")
        }

        pub extern "jni" fn demoCreate(environment: &JNIEnv, keyName: String) -> bool {
            Self::create_key(environment, keyName).unwrap()
        }

        /// Is called to Demo Encryption from Rust
        pub extern "jni" fn demoEncrypt(environment: &JNIEnv, data: Box<[u8]>) -> Box<[u8]> {
            let result = Self::encrypt_data(environment, data.as_ref())
                .expect("Sign_data failed");
            result.into_boxed_slice()
        }

        pub extern "jni" fn demoDecrypt(environment: &JNIEnv, data: Box<[u8]>) -> Box<[u8]> {
            let result = Self::decrypt_data(environment, data.as_ref())
                .expect("Sign_data failed");
            result.into_boxed_slice()
        }

        pub extern "jni" fn demoSign(environment: &JNIEnv, data: Box<[u8]>) -> Box<[u8]> {
            let result = Self::sign_data(environment, data.as_ref())
                .expect("Sign_data failed");
            result.into_boxed_slice()
        }

        pub extern "jni" fn demoVerify(environment: &JNIEnv, data: Box<[u8]>) -> bool {
            todo!()
        }


        //------------------------------------------------------------------------------------------
        // Java methods that can be called from rust

        // Version using Robusta - currently not working due to ClassNotFoundException
        pub extern "java" fn callback(
            &self,
            env: &JNIEnv
        ) -> JniResult<()> {}

        //Version without Robusta, currently working
        // pub fn callback(environment: &JNIEnv) -> () {
        //     //This calls a method in Java in the Class RustDef, with the method name "callback"
        //     //and no arguments
        //     environment.call_static_method(
        //         "com/example/vulcans_limes/RustDef",
        //         "callback",
        //         "()V",
        //         &[],
        //     ).expect("Java func call failed");
        // }

        /// Creates a new cryptographic key identified by `key_id`.
        ///
        /// This method generates a new cryptographic key within the TPM.
        /// The key is made persistent and associated with the provided `key_id`.
        ///
        /// # Arguments
        /// `key_id` - String that uniquely identifies the key so that it can be retrieved later
        pub fn create_key(environment: &JNIEnv, key_id: String) -> Result<bool, Error> {
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "create_key",
                "(Ljava/lang/String;)Z",
                &[JValue::from(environment.new_string(key_id).unwrap())],
            );
            return match result {
                Ok(o) => Ok(o.z().unwrap()),
                Err(e) => Err(e),
            };
        }

        /// Loads an existing cryptographic key identified by `key_id`.
        ///
        /// This method generates a new cryptographic key within the TPM.
        ///  The loaded key is associated with the provided `key_id`.
        ///
        /// # Arguments
        /// `key_id` - String that uniquely identifies the key so that it can be retrieved later
        pub fn load_key(environment: &JNIEnv, key_id: String) -> Result<(), Error> {
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "create_key",
                "(Ljava/lang/String;)V",
                &[JValue::from(environment.new_string(key_id).unwrap())],
            );
            return match result {
                Ok(..) => Ok(()),
                Err(e) => Err(e),
            };
        }

        /// Initializes the TPM module and returns a handle for further operations.
        ///
        /// This method initializes the TPM context and prepares it for use. It should be called
        /// before performing any other operations with the TPM.
        ///
        /// # Arguments
        ///
        /// * `key_id` - A string slice that uniquely identifies the key to be loaded.
        /// * `key_algorithm` - The asymmetric encryption algorithm used for the key.
        /// * `sym_algorithm` - An optional symmetric encryption algorithm used with the key.
        /// * `hash` - An optional hash algorithm used with the key.
        /// * `key_usages` - A vector of `AppKeyUsage` values specifying the intended usages for the key.
        ///
        /// # Returns
        ///
        /// A `Result` that, on success, contains `()`,
        /// indicating that the module was initialized successfully.
        /// On failure, it returns an Error
        pub fn initialize_module(environment: &JNIEnv,
                                 key_algorithm: String,
                                 sym_algorithm: String,
                                 hash: String,
                                 key_usages: String)
                                 -> Result<(), Error> {
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "initialize_module",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                &[JValue::from(environment.new_string(key_algorithm).unwrap()),
                    JValue::from(environment.new_string(sym_algorithm).unwrap()),
                    JValue::from(environment.new_string(hash).unwrap()),
                    JValue::from(environment.new_string(key_usages).unwrap())],
            );
            return match result {
                Ok(..) => Ok(()),
                Err(e) => Err(e),
            };
        }

        /// Signs the given data using the cryptographic key managed by the TPM provider.
        ///
        /// # Arguments
        ///
        /// * `data` - A byte slice representing the data to be signed.
        ///
        /// # Returns
        ///
        /// A `Result` containing the signature as a `Vec<u8>` on success,
        /// or an `Error` on failure.
        fn sign_data(environment: &JNIEnv, data: &[u8]) -> Result<Vec<u8>, Error> {
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "sign_data",
                "([B)Ljava/lang/String;",
                &[JValue::from(environment.byte_array_from_slice(data).unwrap())],
            );
            Self::interpret_result(environment, result)
        }

        /// Verifies the signature of the given data using the key managed by the TPM
        ///
        /// # Arguments
        ///
        /// * `data` - A byte slice representing the data whose signature is to be verified
        /// * `signature` - A byte slice representing the signature to be verified.
        ///
        /// # Returns
        ///
        /// A `Result` containing a `bool` signifying whether the signature is valid,
        /// or an `Error` on failure to determine the validity.
        fn verify_signature(environment: &JNIEnv, data: &[u8], signature: &[u8]) -> Result<bool, Error> {
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "verify_signature",
                "([B[B)Z",
                &[JValue::from(environment.byte_array_from_slice(data).unwrap()),
                    JValue::from(environment.byte_array_from_slice(signature).unwrap())],
            );
            result.unwrap().z()
        }

        /// Encrypts the given data using the key managed by the TPM
        ///
        /// # Arguments
        ///
        /// * `data` - A byte slice representing the data to be encrypted.
        ///
        /// # Returns
        ///
        /// A `Result` containing the encrypted data as a `Vec<u8>` on success,
        /// or an `Error` on failure.
        fn encrypt_data(environment: &JNIEnv, data: &[u8]) -> Result<Vec<u8>, Error> {
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "encrypt_data",
                "([B)Ljava/lang/String;",
                &[JValue::from(environment.byte_array_from_slice(data).unwrap())],
            );
            Self::interpret_result(environment, result)
        }

        /// Decrypts the given data using the key managed by the TPM
        ///
        /// # Arguments
        ///
        /// * `data` - A byte slice representing the data to be Decrypted.
        ///
        /// # Returns
        ///
        /// A `Result` containing the Decrypted data as a `Vec<u8>` on success,
        /// or an `Error` on failure.
        fn decrypt_data(environment: &JNIEnv, data: &[u8]) -> Result<Vec<u8>, Error> {
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "decrypt_data",
                "([B)Ljava/lang/String;",
                &[JValue::from(environment.byte_array_from_slice(data).unwrap())],
            );
            Self::interpret_result(environment, result)
        }

        //------------------------------------------------------------------------------------------
        // Utility Functions that are only used by other Rust functions.
        // These functions have no relation to RustDef.java


        /// Interprets the result of a JNI method call that returns a byte[],
        /// converting a `Result<JValue, Error>`into a `Result<Vec<u8>, Error>`.
        /// <p>
        /// e.g. the JValue containing
        /// <p>
        /// \[48, 49, 47, 48, 48, 47, 48, 48, 47, 48, 48, 47, 48, 48, 47, 70, 50, 47, 42]
        /// <p>
        /// first gets converted to the ASCII values "01/00/00/00/00/F2/*",
        /// and the method returns \[1,0,0,0,0,242]
        ///
        /// # Arguments
        ///
        /// * `environment` - A reference to the JNIEnv.
        /// * `result` - The result of the JNI operation, containing a `JValue` representing
        ///              a hexadecimal representation of bytes, separated by '/'.
        ///
        /// # Returns
        ///
        /// A `Result` containing a `Vec<u8>` if the operation was successful,
        /// or an `Error` if it failed.
        ///
        /// # Errors
        ///
        /// This function may return an error if any of the JNI operations fail.
        fn interpret_result(environment: &JNIEnv, result: Result<JValue, Error>)
                            -> Result<Vec<u8>, Error> {
            match result {
                Ok(data) => {
                    // Convert JValue to Vec<u8> containing the ASCII values
                    // for the transmitted bytes
                    let obj = data.l().expect("to JObj failed");
                    let string = JString::from(obj);
                    let rustring = environment.get_string(string).expect("JavaStr failed");
                    let mut vec = Vec::from(rustring.to_str().unwrap());

                    //Convert the ASCII values to bytes and store them in a Vec<u8>
                    vec.remove(vec.len() - 1); //discard last transmitted symbol - is always a '*'
                    let mut result: Vec<u8> = Vec::new();
                    result.reserve(vec.len());
                    for i in (0..vec.len() as i32).step_by(3) {
                        //Convert the next two ASCII values to their corresponding Hex Values
                        let mut v1 = (*vec.get(i as usize).unwrap() as char)
                            .to_digit(16).unwrap() as u8;
                        let v2 = (*vec.get((i + 1) as usize).expect("1") as char)
                            .to_digit(16).expect("2") as u8;
                        // Combine both Hex Numbers into one with bitshift and bitwise OR
                        v1 = (v1 << 4) | v2;
                        //store result
                        result.push(v1);
                    }
                    Ok(result)
                }
                Err(e) => { Err(e) } // pass any errors through unchanged
            }
        }
    }
}