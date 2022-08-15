# Te-Aromatawai-Tuatahi
**CYBR372 Assignment 1 - Part 2**

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
<p>I extended my swtich statement from part 1 to check for an additional parameter on enc and dec operations. When enc has 3 arguments following it the program will interpret the first as the base64 encoded secret key, and when decrypt has 4 following it the program will assume there is an existing IV from the encryption operation with the same name as the encrypted file with ".iv" appended.
  <br><br>
  It doesn't pose any particular security threats to leave the iv unencrypted and unhidden, because the same 'salt' will never be generated for the same key and an attacker would need to have the secret key as well as the IV. A more common practise is to prepend the IV to the encrypted file, but I couldn't get this to work as well as my method.
</p>

**Encrypting:**

  * java part2.FileEncryptor enc ((Base64 encoded secret key)) <INPUT> <OUTPUT>
  * OR
  * java part2.FileEncryptor enc <INPUT> <OUTPUT>
  
<p>When given a secret key, the encryption process will use that key instead of generating a random one. This will allow us to confirm that the IV is salting the key, and a file with the same key will yield different results each time.</p>

  *Encryption of the same file with the same key*
  <img src="https://i.imgur.com/DXolCLH.png">
  Screenshot direct link: https://i.imgur.com/DXolCLH.png<br/>
  <p>The IV is used to ensure the same plaintext under the same key will always encrypt to different ciphertexts. In the above screenshot, both encryptions of the same file yielded different hexdump results in the ciphertext.
  </p>

  
**Decrypting**

* java part1.FileEncryptor dec ((Base64 encoded secret key)) ((Base64 encoded IV)) <INPUT> <OUTPUT>
* OR
* java part1.FileEncryptor dec ((Base64 encoded secret key)) <INPUT> <OUTPUT>

*Decryption of both encrypted files without specifying IV*
<img src="https://i.imgur.com/uXCNgMO.png">
Screenshot direct link: https://i.imgur.com/uXCNgMO.png<br/>
  <p>In the above screenshot, both encryped files are decrypted without specifying the IV. Both decoded files are exactly the same as shown in hexdump, and each operation uses its own unique IV in combination with the given key, which in this example are stored in files named ciphertext.enc.iv and ciphertext2.enc.iv respectively.</p>


