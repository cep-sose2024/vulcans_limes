use robusta_jni::bridge;

#[bridge]
mod jni {
    use robusta_jni::convert::Signature;

    #[derive(Signature)]
    #[package(com.example.vulcans_1limes)]
    struct RustDef;

    impl RustDef {
        pub extern "jni" fn special(mut input1: Vec<i32>, input2: i32) -> Vec<String> {
        let (app_vm, class_ref) = crate::APP_CONTEXT
            .get()
            .ok_or_else(|| "Couldn't get APP_CONTEXT".to_string())?;
        let env = app_vm
            .attach_current_thread_permanently()
            .map_err(|_| "Couldn't attach to current thread".to_string())?;
        env.call_static_method(
            "com/example/vulcans_limes/RustDef",
            "callback",
            "()V",
            &[],
        );


        input1.push(input2);
        input1.iter().map(ToString::to_string).collect()
        }

       // pub extern "java" fn callback(*mut &JNIEnv) {}
    }
}