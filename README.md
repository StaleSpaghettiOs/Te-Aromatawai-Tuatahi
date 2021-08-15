# Te-Aromatawai-Tuatahi
**CYBR372 Te Aromatawai Tuatahi (Assignment 1)**

* Encryption operand denoted by enc keyword
* Decryption operand denoted by dec keyword
* Input and output paths can be just file names (will read and write to directory of FileEncryptor.java) 
* OR they can be absolute paths

**Part 1 implementation**

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


**Part 2 implementation**

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

*Demonstrating successful encryption and decryption of a file using hexdump*

*First file*
% C:\Users\jakob\IdeaProjects\Crypto\src> hexdump -C plaintext1.txt
* 000000  54 68 65 20 71 75 69 63 6b 20 62 72 6f 77 6e 20  The quick brown
* 000010  66 6f 78 20 6a 75 6d 70 65 64 20 6f 76 65 72 20  fox jumped over
* 000020  74 68 65 20 6c 61 7a 79 20 64 6f 67 2e           the lazy dog.

% C:\Users\jakob\IdeaProjects\Crypto\src> java FileEncryptor enc plaintext1.txt ciphertext1.enc

Random key= MqNMVDz9jIWCa9sF67Zd9A==
initVector= eqDSm4xx4kxT3ZdfMkMc0g==
Successfully stored IV at ciphertext1.enc.iv

FileEncryptor encrypt
INFO: Encryption finished, saved at C:\Users\jakob\IdeaProjects\Crypto\src\ciphertext1.enc

% C:\Users\jakob\IdeaProjects\Crypto\src> hexdump -C ciphertext1.enc
* 000000  ed 27 e2 a2 e8 6d a2 c3 ea 1b 0e 0b bd 94 06 09  .'...m..........
* 000010  14 69 02 4d 50 09 a2 d9 54 3a 86 93 10 1d 7f 46  .i.MP...T:.....F
* 000020  da f5 2b 40 bd b6 cb d7 8f 6c 1c 7a 1e 17 c2 02  ..+@.....l.z....

% C:\Users\jakob\IdeaProjects\Crypto\src> java FileEncryptor dec MqNMVDz9jIWCa9sF67Zd9A== ciphertext1.enc decoded1.txt
FileEncryptor decrypt
INFO: Decryption complete, open C:\Users\jakob\IdeaProjects\Crypto\src\decoded1.txt

% C:\Users\jakob\IdeaProjects\Crypto\src> hexdump -C decoded1.txt
* 000000  54 68 65 20 71 75 69 63 6b 20 62 72 6f 77 6e 20  The quick brown
* 000010  66 6f 78 20 6a 75 6d 70 65 64 20 6f 76 65 72 20  fox jumped over
* 000020  74 68 65 20 6c 61 7a 79 20 64 6f 67 2e           the lazy dog.


*Now, let's try encrypting and decrypting again with the same key*

% C:\Users\jakob\IdeaProjects\Crypto\src> java FileEncryptor enc MqNMVDz9jIWCa9sF67Zd9A== plaintext1.txt ciphertext1.enc

3 enc arguments detected, interpreting first as base64 encoded Secret Key
User inputted key= MqNMVDz9jIWCa9sF67Zd9A==
initVector= SpN3MenwrMrTphchJXjtJg==

FileEncryptor encrypt
INFO: Encryption finished, saved at C:\Users\jakob\IdeaProjects\Crypto\src\ciphertext1.enc

% C:\Users\jakob\IdeaProjects\Crypto\src> hexdump -C ciphertext1.enc
* 000000  0c 4e ad 70 4f cb 2f 78 9e 06 9f a5 0b 8f 85 ff  .N.pO./x........
* 000010  b8 c9 4a e5 c5 10 d2 01 78 91 b0 df d0 ce ea 53  ..J.....x......S
* 000020  16 28 97 c2 ef c5 28 af c4 33 cd f4 50 6d b0 88  .(....(..3..Pm..

% C:\Users\jakob\IdeaProjects\Crypto\src> hexdump -C decoded1.txt
* 000000  54 68 65 20 71 75 69 63 6b 20 62 72 6f 77 6e 20  The quick brown
* 000010  66 6f 78 20 6a 75 6d 70 65 64 20 6f 76 65 72 20  fox jumped over
* 000020  74 68 65 20 6c 61 7a 79 20 64 6f 67 2e           the lazy dog.

The IV is used to ensure the same plaintext under the same key will always encrypt to different ciphertexts. In the above example, both encryptions of the same file yielded different hexdump results in the ciphertext.


It doesn't pose any particular security threats to leave the iv unencrypted and unhidden, so the initialisation vector is stored in a separate file with the same name and .iv appended to it.
