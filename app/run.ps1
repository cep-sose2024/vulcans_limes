# USE THIS SCRIPT WHEN BUILDING ON A WINDOWS MACHINE

# This script builds the rust library as specified in the cargo.toml,
# and then runs gradle to build and install the app on the phone

# to run this script, you may need to execute the following command as an administrator:
# Set-ExecutionPolicy -ExecutionPolicy RemoteSigned
# you can find more info about this here: https://learn.microsoft.com/previous-versions/ee176961(v=technet.10)


# you may need to adjust this for your own machine
# you can find the location for the SDK under Settings > Languages & Frameworks
# > Android SDK > SDK Tools and go from there
$env:ANDROID_NDK_HOME = "$env:ANDROID_HOME\ndk\27.0.11718014"

Set-Location src
Write-Output ""
Write-Output "building rust"
Write-Output ""
cargo ndk -t armeabi-v7a -t arm64-v8a -o main/jniLibs build
Set-Location ..

Write-Output ""
Write-Output "-----------------------------------------------------------------------------------------------"
Write-Output "building apk"
../gradlew -D java.library.path=app/src/target/x86_64-linux-android/debug installDebug
