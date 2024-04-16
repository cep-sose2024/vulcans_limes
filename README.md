
# Rust Wrapper for Samsung Knox Vault

This Repo contains a Wrapper that is used to store cryptographic keys from applications in a Trusted Execution Environment (TEE). Specifically, this project is focused on Samsung Knox Vault as the TEE.

Please note that this project is still in early development.

## Goal
The eventual project goal is to provide an implementation for the Samsung Knox Vault in order to store cryptographic keys in it. In order to do so, the Android Keystore API will be used to communicate with the TEE. Since this API utilizes Java, the Java Native Interface (JNI) will be used to facilitate communication between Java and Rust.

## Motivation
This project is part of a student development project at Hochschule Mannheim (HSMA). The project goal is provided by j&s-soft GmbH as part of their project enmeshed. 

### Enmeshed
enmeshed is an open-source app designed to facilitate secure exchange of data and documents between companies, education providers and individuals.

### Placement within enmeshed
This project is supposed to be used to store keys securely on the device. These keys are to be used to facilitate end-to-end-encryption of all communication between the enmeshed app and server.

This project will attach to their crypto abstraction layer (link will follow), and provide a connection from there to the TEE.

## Installation 
For an Installation Guide, please refer to [this File](https://github.com/cep-sose2024/vulcans_limes/blob/master/Installation).

## Contributing
We welcome contributions from the community. Please submit a pull request if you detect any bugs, have ideas for enhancements, or would like to add new functionality.


## License

this project is pubilshed under the MIT Licence. For details check the [LICENCE File](https://github.com/cep-sose2024/vulcans_limes/blob/master/LICENSE).

## Acknowledgements

 - [j&s-soft GmbH](https://github.com/js-soft) for the project goal and the crypto abstaction layer provided by them
