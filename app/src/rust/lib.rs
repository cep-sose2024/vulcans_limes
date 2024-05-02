use robusta_jni::bridge;

#[bridge]
mod jni {
    use robusta_jni::bridge;
    use robusta_jni::convert::Signature;
    use robusta_jni::jni::JNIEnv;

    #[derive(Signature)]
    #[package(com.example.vulcans_1limes)]
    struct RustDef;

    impl RustDef {
        pub extern "jni" fn special(mut input1: Vec<i32>, input2: i32) -> Vec<String> {

            input1.push(input2);
            input1.push(42);
            input1.iter().map(ToString::to_string).collect()
        }

        pub extern "jni" fn callRust(environment : &JNIEnv) -> (){

            //This calls a method in Java in the Class RustDef, with the method nam "callback"
            //and no arguments
            environment.call_static_method(
                        "com/example/vulcans_limes/RustDef",
                        "callback",
                        "()V",
                        &[],
            ).expect("Java func call failed");
        }
    }
}