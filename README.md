
# Rust Wrapper for Samsung Knox Vault

This Repo contains a Wrapper that is used to perfom cryptogrphic operations for mobile applications in a Secure Element (SE) on Android devices. Specifically, this project is focused on Samsung Knox Vault as the SE. The interface to the mobile application is provided in Rust, while the communication with the SE will be done using the Android Keychain API. 

Please note that this project is still in early development.

## Goal
The eventual project goal is to provide an implementation to store cryptographic keys within Samsung Knox Vault. In order to do so, the Android Keystore API will be used to communicate with the SE. Since this API utilizes Java, the Java Native Interface (JNI) will be used to facilitate communication between Java and Rust.

## Motivation
This project is part of a student development project at Hochschule Mannheim (HSMA). The project goal is provided by j&s-soft GmbH as part of their project enmeshed. 

### Enmeshed
enmeshed is an open-source app designed to facilitate the secure exchange of data and documents between companies, education providers and individuals.

### Placement within enmeshed
This project is supposed to be used to store keys securely on the device. These keys are to be used to facilitate end-to-end-encryption of all communication between the enmeshed app and server.

This project will attach to their [crypto abstraction layer](https://github.com/nmshd/rust-crypto), and provide a connection from there to the SE.

## Compatible Devices
This wrapper should work for all Android devices equiped with a SE, but the focus for our group is specifically on smartphones using Samsung Knox Vault. Therefore, testing will only be done using a Samsung smartphone, and in case there are incompatibilities between device manufacturers, Samsung will take priority. An up-to-date list of devices equipped with Knox Vault can be found [here](https://www.samsungknox.com/en/knox-platform/supported-devices) after selecting Knox Vault in the filter options. As of April 2024, the following devices are equipped with Knox Vault:

Smartphones 
- Samsung Galaxy A 35 / A55 
- Samsung Galaxy S 22 / S23 / S 24
  - the plus / ultra versions as well
- Samsung Galaxy Z Flip 3 / 4 / 5
- Samsung Galaxy Z Fold 3 / 4 / 5
- Samsung Galaxy X Cover 7 (Enterprise Edition) 

Tablets 
- Galaxy Tab S 8 / S 9
  - the plus, ultra, FE and 5G versions as well
- Galaxy Tab Active 5 (Enterprise Edition)
  - the 5G version as well

## Installation 
For an installation guide, please refer to [this File](https://github.com/cep-sose2024/vulcans_limes/blob/master/Installation.md).

## Contributing
We welcome contributions from the community. Please submit a pull request if you detect any bugs, have ideas for enhancements, or would like to add new functionality.


## License

this project is pubilshed under the MIT Licence. For details check the [LICENCE File](https://github.com/cep-sose2024/vulcans_limes/blob/master/LICENSE).

## Acknowledgements

 - [j&s-soft GmbH](https://github.com/js-soft) for the project goal and the crypto abstaction layer provided by them
