# Te-Aromatawai-Tuatahi
CYBR372 Te Aromatawai Tuatahi (Assignment 1)

* Encryption operand denoted by enc keyword
* Decryption operand denoted by dec keyword
* Input and output paths can be just file names (will read and write to directory of FileEncryptor.java) 
* OR they can be absolute paths

Part 1 implementation

Encrypting:
* FileEncryptor enc inputfile outputfile
* e.g. 
* % java FileEncryptor enc plaintext.txt ciphertext.txt
* INFO: Random key= w+4KkpW8hzDT/85DuQdpQw==
* INFO: initVector= ds4/N743sjjOGH0W/I1aEw==
* INFO: Encryption finished, saved at C:\Users\name\Crypto\src\ciphertext.enc
Decrypting:
* FileEncryptor dec ((Base64 encoded secret key)) ((Base64 encoded IV)) inputfile outputfile
* % java FileEncryptor dec w+4KkpW8hzDT/85DuQdpQw== ds4/N743sjjOGH0W/I1aEw== ciphertext.enc decoded.txt
* INFO: Decryption complete, open C:\Users\name\Crypto\src\decoded.txt


Part 2 implementation

* User can specify custom Base64 encoded secret key (16 bytes)
* User no longer required to input initialisation vector
Encrypting:
* FileEncryptor enc ((Base64 encoded secret key)) inputfile outputfile
* e.g. 
* % java FileEncryptor enc w+4KkpW8hzDT/85DuQdpQw== plaintext.txt ciphertext.txt
* INFO: Random key= w+4KkpW8hzDT/85DuQdpQw==
* INFO: initVector= ds4/N743sjjOGH0W/I1aEw==
* INFO: Encryption finished, saved at C:\Users\name\Crypto\src\ciphertext.enc
Decrypting:
* FileEncryptor dec ((Base64 encoded secret key)) ((Base64 encoded IV)) inputfile outputfile
* % java FileEncryptor dec w+4KkpW8hzDT/85DuQdpQw== ds4/N743sjjOGH0W/I1aEw== ciphertext.enc decoded.txt
* INFO: Decryption complete, open C:\Users\name\Crypto\src\decoded.txt

