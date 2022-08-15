# Te-Aromatawai-Tuatahi
**CYBR372 Assignment 1 - Part 3**

* JAVA Version:
  openjdk 11.0.16 2022-07-19
  OpenJDK Runtime Environment (build 11.0.16+8)
  OpenJDK 64-Bit Server VM (build 11.0.16+8, mixed mode)



* The format of encryption operation is java part3.FileEncryptor enc "password" <INPUT> <OUTPUT>
* The format of decryption operation is java part3.FileEncryptor dec "password" <INPUT> <OUTPUT>
* Input and output paths can be just file names (will read and write to working directory)
* OR they can be absolute paths, *but* the directory must already exist
* OR they can be paths relative to working directory, *but* the directory must already exist

**Part 3 implementation**

<p>For this part, I switched the encryption algorithm to PBEWithHmacSHA256AndAES_256 to utilise password-based encryption with salt and IV. To increase entropy, the approach is to salt, iterate, and hash 1000 times (standard iteration count)
The password must be retrieved and stored as a char array - this is because if it were stored as a String, we wouldn't have any way to zero out the contents because strings are immutable.
To decrease risk of chosen-plaintext attacks I chose to implement a minimum password length of 4, as the keys generated from smaller passwords have lower entropy.<br>
Similarly to part 2, the IV and salt are written to a file with the same name as the encrypted output with ".siv" appended. This is later read during decryption to gain 
the salt and IV used alongside the PBE key. This file doesn't need to be encrypted or hidden because each input is saltedwith unique random data. When the salt is unique
for each hash we create a computational bottleneck for a would-be attacker, who now has to compute a hash table for each hash. </p>

*Encrypting the same file with the same password*
<img src="https://i.imgur.com/Jul0C8V.png">
<a href="https://i.imgur.com/Jul0C8V.png">Direct link</a>
<p>Here I've used "some password" to encrypt a plaintext file twice, in both operations we can see different a salt and IV is generated, but the key generated from the password is the same.
Using hexdump we can confirm that both ciphertexts are different files.</p>

*Decrypting distinct ciphertexts with the same password*
<img src="https://i.imgur.com/A6jJUDW.png">
<a href="https://i.imgur.com/A6jJUDW.png">Direct link</a>
p>Here I've continued to the decryption of ciphertext1.enc and ciphertext2.enc, and using hexdump we can confirm that both plaintext results are exactly identical.</p>
