# Te-Aromatawai-Tuatahi
**CYBR372 Assignment 1 - Part 2**

* This was compiled with OpenJDK 11, java runtime must recognise class file versions up to 55.0

* JAVA Version:
  openjdk 11.0.16 2022-07-19
  OpenJDK Runtime Environment (build 11.0.16+8)
  OpenJDK 64-Bit Server VM (build 11.0.16+8, mixed mode)
* Encryption operand denoted by enc keyword
* Decryption operand denoted by dec keyword
* Input and output paths can be just file names (will read and write to working directory)
* OR they can be absolute paths, *but* the directory must already exist
* OR they can be paths relative to working directory, *but* the directory must already exist


**Part 2 implementation**
<p>I extended my swtich statement from part 1 to check for an additional parameter on enc and dec operations. When enc has 3 arguments following it the program will interpret the first as the base64 encoded secret key, and when decrypt has 4 following it the program will assume there is an existing IV from the encryption operation with the same name as the encrypted file with ".iv" appended.</p>

**Encrypting:**
*Syntax: 
  * % java part2.FileEncryptor enc ((Base64 encoded secret key)) <INPUT> <OUTPUT>
  * OR
  * % java part2.FileEncryptor enc <INPUT> <OUTPUT>
  
<p>When given a secret key, the encryption process will use that key instead of generating a random one. This will allow us to confirm that the IV is salting the key, and a file with the same key will yield different results each time.</p>

**Decrypting:**
*Syntax
* part1.FileEncryptor dec ((Base64 encoded secret key)) ((Base64 encoded IV)) inputfile outputfile
* % java part1.FileEncryptor dec w+4KkpW8hzDT/85DuQdpQw== ds4/N743sjjOGH0W/I1aEw== ciphertext.enc decoded.txt
* INFO: Decryption complete, open C:\Users\name\Crypto\src\decoded.txt

*Encryption of the same file with the same key*
<img src="https://i.imgur.com/DXolCLH.png">
Screenshot direct link: https://i.imgur.com/DXolCLH.png
The IV is used to ensure the same plaintext under the same key will always encrypt to different ciphertexts. In the above example, both encryptions of the same file yielded different hexdump results in the ciphertext.

*Decryption of both encrypted files without specifying IV*
<img src="https://i.imgur.com/uXCNgMO.png">
Screenshot direct link: https://i.imgur.com/uXCNgMO.png


**The design**


It doesn't pose any particular security threats to leave the iv unencrypted and unhidden, so the initialisation vector is stored in a separate file with the same name and .iv appended to it. While this isn't the most secure way to store the IV, it's unlikely the same salt will ever be generated for the same key.

