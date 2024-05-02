package com.example.vulcans_limes;

import java.util.*;

class RustDef {
    /*
    This is the native method declaration that allows other Java classes to call and use the
    function implemented in Rust. Note that it has the same name as the Rust function. This is
    necessary to link the two together.

    Can be called with e.g. ArrayList<String> out = RustDef.special(temp, 2);
     */
    static native ArrayList<String> special(ArrayList<Integer> input1, int input2);
    static native void callRust();


    static {
        // This call loads the dynamic library containing the Rust code that we generated.
        System.loadLibrary("vulcanslimes");
    }

    static void callback(){
        System.out.println("Callback successful");
    }

}