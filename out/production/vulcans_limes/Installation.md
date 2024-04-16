# Installation Guide
## Installing Android Studio

## Installing Rust

## Run first Test in Android Studio
The Goal of this section is to test Android Studio and run a "Hello World"-App on a real device. We will use a USB connection to get the app onto the device, although WiFi should also be possible.
- make sure that USB debugging is enabled on the phone. Refer to [this guide](https://developer.android.com/studio/debug/dev-options) if necessary
- install the proper driver for your smartphone on your PC / Laptop according to [this list](https://developer.android.com/studio/run/oem-usb#Drivers)
- Open a new project in Android Studio and select Phone/Tablet on the left and use "Empty Activity" as the template
- plug your phone into your PC / Laptop with a usb cable capable of transmitting data
- you should be able to see your phone's storage in your file explorer
- in Android Studio, go to "Running Devices" (on the top right by default), click on the plus sign and select your phone
- the screen of your phone will be mirrored inside Android Studio
- build and run your application and open it on the phone
