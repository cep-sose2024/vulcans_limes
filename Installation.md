# Installation Guide

## Installing Android Studio + RustRover
The easiest way to install both IDEs is to use the [JetBrains Toolbox](https://www.jetbrains.com/de-de/toolbox-app/), where you can install both IDEs with a single click each.
Alternatively you can also refer to the [Andriod CodeLab guide](https://developer.android.com/codelabs/basic-android-kotlin-compose-install-android-studio#0), and the [RustRover download page](https://www.jetbrains.com/rust/nextversion/).
After installing RustRover, you will need to install RustUp in order to compile Rust code. This can be done from within RustRover: After starting to create a new project, there will be a button below the toolchain location selection. Pressing it will install RustUp. Alternatively you can also install RustUp directly from [their page](https://rustup.rs/).


## First Test in Android Studio
The Goal of this section is to test Android Studio and run a "Hello World"-App on a real device. We will use a USB connection to get the app onto the device, although WiFi should also be possible.
- make sure that USB debugging is enabled on the phone. Refer to [this guide](https://developer.android.com/studio/debug/dev-options) if necessary
- install the proper driver for your smartphone on your PC / Laptop according to [this list](https://developer.android.com/studio/run/oem-usb#Drivers)
- Open a new project in Android Studio and select Phone/Tablet on the left and use "Empty Activity" as the template
- plug your phone into your PC / Laptop with a USB cable capable of transmitting data
- you should be able to see your phone's storage in your file explorer
- in Android Studio, go to "Running Devices" (on the top right by default), click on the plus sign and select your phone
- the screen of your phone will be mirrored inside Android Studio
- build and run your application and open it on the phone

## Further necessary tools
In order to use the Java Native Interface, and use Rust Code within our app, you will need some more tools:
- You will need the Android NDK (Native Development Kit) to build and run the project. The NDK can be installed from within Android Studio: Go to Settings > Languages & Frameworks > Android SDK > SDK Tools and select and install the NDK
- Cargo and [cargo-ndk](https://docs.rs/crate/cargo-ndk/3.5.4) are necessary to compile the Rust code in a format usable by Android. Cargo should already be installed by RustUp, cargo-ndk can be installed with `cargo install cargo-ndk`
- You will need to configure cargo further by using this command: `rustup target add aarch64-linux-android armv7-linux-androideabi i686-linux-android`

## Building and running the project
In order to compile and run the project, execute the run.sh (Linux) or run.bat (Windows) file in /app. This will compile the rust code into a dynamic library, which is then used by Java. After that, the script will start gradle to build the whole project and install it on your connected device for testing.
You will need to adjust the path to the NDK inside the script, instructions for that can be found in the script itself.
