# Sha_3_Symmetric_Encryption
I made This class project for the University of Washington, Tacoma TCSS 487 Cryptography.

## Description
A custom implementation of the SHA-3 encryption scheme and symmetric encryption using Java.

## Program Instructions
### Command Line Mode:
To use this mode, run the program without any arguments. Then follow the instructions printed to the console.
### File Mode:
To use this mode, run the program with an argument.\
These are the acceptable flags: -KP, -PKF, -EE, -SF, -V

#### -KP:
Command structure: {input file path with name} -KP {passphrase}
Description: Generates an elliptic key pair from a given passphrase and writes the public and
private keys to a file.

#### -PKF:
Command structure: {input file path with name} -PKF {public key file path with name}\
Description: Encrypts a data file under a given elliptic public key file and writes the ciphertext to
a file.
##### PUBLIC KEY FILE REQUIREMENTS: This command assumes that the public key is represented by a point (x, y). X should be on the first line in the public key file and y should be on the second line.
Public key file example:\
132132dasf1231 (This line is X)\
1325adsf113asdf (This line is Y)

#### -EE:
Command structure: {input file path with name} -EE {passphrase}\
Description: Decrypts a given elliptic-encrypted file from a given password and writes the decrypted data to a file.
##### INPUT FILE REQUIREMENTS:
This command assumes that the input file is a symmetric cryptogram (Z, c, t) where Z is a point (x, y). The first line should be 𝑍_x, the second line should be 𝑍_y, the third line should be c, and the fourth line should be t.\
Input file example:\
132adsf1132 (This line is 𝑍_x)\
Asdff31121a (This line is 𝑍_y)\
Asdfadsfasfdafa (This line is c)\
Adsfasfafafadsfasfdasfasdfafddasf (This line is t)

#### -SF:
Command structure: {input file path with name} -SF {password}\
Description: Signs a given file from a given password and writes the signature to a file.

#### -V:
Command structure: {input file path with name} -V {signature file path with name} {public key file path with name}\
Description: Verifies a given data file and its signature file under a given public key file and writes VERIFIED or UNVERIFIED to a file.\
SIGNATURE FILE REQUIREMENTS: This command assumes the signature file is a Signature (h, z). The first line of the file should be h, and the second line should be z.\
Signature file example:\
Asdfa4564456465 (This line is h)\
Asdfadsfad44654 (This line is z)\
PUBLIC KEY FILE REQUIREMENTS: This command assumes that the public key is represented by a point (x, y). X should be on the first line in the public key file and y should be on the second line.\
Public key file example:\
132132dasf1231 (This line is X)\
1325adsf113asdf (This line is Y)
