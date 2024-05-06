use robusta_jni::bridge;

#[bridge]
mod jni {
    use std::fmt::Error;

    use robusta_jni::bridge;
    use robusta_jni::convert::IntoJavaValue;
    use robusta_jni::convert::Signature;
    use robusta_jni::jni::JNIEnv;
    use robusta_jni::jni::objects::JValue;

    #[derive(Signature)]
    #[package(com.example.vulcans_1limes)]
    struct RustDef {}

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
    /// | Box\<[u8]\>                                        | byte[]                            |
    /// | [jni::JObject<'env>](jni::objects::JObject)        | *(any Java object as input type)* |
    /// | [jni::jobject](jni::sys::jobject)                  | *(any Java object as output)*     |
    /// |----------------------------------------------------------------------------------------|
    #[allow(non_snake_case)]
    impl RustDef {

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
        pub extern "jni" fn callRust(environment : &JNIEnv) -> (){

            //example usage of a java method call from rust
            // Self::create_key(environment, String::from("moin")).unwrap()

            //DOESNT WORK YET
             let bytes: [u8; 5] = [10, 20, 30, 40, 50];
             // Self::sign_data_call(environment, &bytes);
        }

        /// Is called to Demo Encryption from Rust
        pub extern "jni" fn demoEncrypt(environment: &JNIEnv, data: Box<[u8]>) -> Box<[u8]> {
            //TESTING - CAN BE REMOVED
            let mut result = data.into_vec();
            result.push(42);
            result.into_boxed_slice()

        }

        pub extern "jni" fn demoDecrypt(environment: &JNIEnv, data: Box<[u8]>) -> Box<[u8]> {
            todo!()
        }

        pub extern "jni" fn demoSign(environment: &JNIEnv, data: Box<[u8]>) -> Box<[u8]> {
            todo!()
        }

        pub extern "jni" fn demoVerify(environment: &JNIEnv, data: Box<[u8]>) -> bool {
            todo!()
        }


        //------------------------------------------------------------------------------------------
        // Java methods that can be called from rust

        ///Proof of concept method - shows callback from Rust to a java method
        ///     DO NOT USE
        pub fn callback(environment: &JNIEnv) -> () {
            //This calls a method in Java in the Class RustDef, with the method name "callback"
            //and no arguments
            let result = environment.call_static_method(
                        "com/example/vulcans_limes/RustDef",
                        "callback",
                        "()V",
                        &[],
            ).expect("Java func call failed");
        }


        /// Creates a new cryptographic key identified by `key_id`.
        ///
        /// This method generates a new cryptographic key within the TPM.
        /// The key is made persistent and associated with the provided `key_id`.
        ///
        /// # Arguments
        /// `key_id` - String that uniquely identifies the key so that it can be retrieved later
        pub fn create_key(environment: &JNIEnv, key_id: String) -> Result<(), Error> {
            let key_id = JValue::from(environment.new_string(key_id).unwrap());
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "create_key",
                "(Ljava/lang/String;)V",
                &[key_id],
            );
            return match result {
                Ok(..) => Ok(()),
                Err(e) => Err(Error),
            }
        }

        /// Loads an existing cryptographic key identified by `key_id`.
        ///
        /// This method generates a new cryptographic key within the TPM.
        ///  The loaded key is associated with the provided `key_id`.
        ///
        /// # Arguments
        /// `key_id` - String that uniquely identifies the key so that it can be retrieved later
        pub fn load_key(environment: &JNIEnv, key_id: String) -> Result<(), Error> {
            let key_id = JValue::from(environment.new_string(key_id).unwrap());
            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "create_key",
                "(Ljava/lang/String;)V",
                &[key_id],
            );
            return match result {
                Ok(..) => Ok(()),
                Err(e) => Err(Error),
            }
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
            let key_algorithm = JValue::from(environment.new_string(key_algorithm).unwrap());
            let sym_algorithm = JValue::from(environment.new_string(sym_algorithm).unwrap());
            let hash = JValue::from(environment.new_string(hash).unwrap());
            let key_usages = JValue::from(environment.new_string(key_usages).unwrap());

            let result = environment.call_static_method(
                "com/example/vulcans_limes/RustDef",
                "initialize_module",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                &[key_algorithm, sym_algorithm, hash, key_usages],
            );
            return match result {
                Ok(..) => Ok(()),
                Err(e) => Err(Error),
            }
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
        fn sign_data_call(environment: &JNIEnv, data: &[u8]) -> Vec<u8> {
            data.to_vec()
            // let data = data.to_vec();
            // Self::sign_data(environment, data)

            // let data = JValue::from(data.to_vec()); //DOESNT WORK; NEEDS FIX
            // let result = environment.call_static_method(
            //     "com/example/vulcans_limes/RustDef",
            //     "initialize_module",
            //     "(Ljava.util.ArrayList<byte>)V",
            //     &[data],
            // );
            // return match result {
            //     Ok(..) => Ok(()),
            //     Err(e) => Err(Error),
            // }
        }

        // pub extern "java" fn sign_data(environment: &JNIEnv, data: Vec<u8>) -> Result<_, Error> {}
    }
}