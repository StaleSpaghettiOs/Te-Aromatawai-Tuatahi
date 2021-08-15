# Te-Aromatawai-Tuatahi
CYBR372 Te Aromatawai Tuatahi (Assignment 1)

* Encryption operand denoted by enc keyword
* Decryption operand denoted by dec keyword
* Input and output paths can be just file names (will read and write to directory of FileEncryptor.java) 
* OR they can be absolute paths

Part 1 implementation
* enc inputfile outputfile
* dec ((Base64 encoded secret key)) ((Base64 encoded iv)) inputfile outputfile
* e.g. 
* java FileEncryptor enc plaintext.txt ciphertext.txt
* INFO: Random key= w+4KkpW8hzDT/85DuQdpQw==
* INFO: initVector= ds4/N743sjjOGH0W/I1aEw==
* INFO: Encryption finished, saved at C:\Users\jakob\IdeaProjects\Crypto\src\ciphertext.enc
