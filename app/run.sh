# USE THIS SCRIPT WHEN BUILDING ON A LINUX MACHINE

# This script builds the rust library as specified in the Cargo.toml,
# and then runs gradle to build and install the app on the phone

# you may need to adjust this for your own machine
# you can find the location for the SDK under Settings > Languages & Frameworks
# > Android SDK > SDK Tools and go from there

export ANDROID_NDK_HOME="$HOME/Android/Sdk/ndk/27.0.11718014"

cd src/rust || exit
echo
echo building rust
echo
cargo ndk -t armeabi-v7a -t arm64-v8a -o ../main/jniLibs build || exit
cd ../..

cd ..
echo
echo -----------------------------------------------------------------------------------------------
echo building apk
./gradlew -D java.library.path=app/src/main/jniLibs installDebug