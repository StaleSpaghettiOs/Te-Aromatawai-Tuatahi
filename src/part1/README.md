# Te-Aromatawai-Tuatahi
**CYBR372 Assignment 1 - Part 1**
  
* JAVA Version:
  openjdk 11.0.16 2022-07-19
  OpenJDK Runtime Environment (build 11.0.16+8)
  OpenJDK 64-Bit Server VM (build 11.0.16+8, mixed mode)

* The format of encryption operation is java part1.FileEncryptor enc <INPUT> <OUTPUT>
* The format of decryption operation is java part1.FileEncryptor dec ((Base64 encoded key)) ((Base64 encoded IV)) <INPUT> <OUTPUT>
* Input and output paths can be just file names (will read and write to working directory)
* OR they can be absolute paths, *but* the directory must already exist
* OR they can be paths relative to working directory, *but* the directory must already exist

**Part 1 implementation**


I decided to separate the encryption and decryption operations in to their own methods
and check the input parameters in the main function, running the specified operation iff
the correct number of parameters is given. Few duplicate lines added to decrypt() as 
they're separate instances now.

The main function calls fillParams(String args[]) to handle the arguments of each operation
and assign them to the global static variables; INPUT, OUTPUT, SKEY, and INITV. A switch 
statement is used to determine which variables to assign based on operation and number of 
arguments.


Encryption:
The code inside encrypt() is virtually identical to the example code, but the hard-coded
input and outputs needed to be changed to INPUT and OUTPUT on lines 66 & 67. The Util
library became redundant once I made the key and IV output Base64, but I'm still including 
the file in the upload

The enc operation will only accept 2 arguments, and throws exceptions otherwise. The 
format of the operation is ~% java part1.FileEncryptor enc <INPUT> <OUTPUT>. The input expects 
a file (can be a path with a file), whereas the output expects either a path or a file. 
Output files without a path go to document root 

*SCREENSHOT DEMONSTRATION*
<img src="https://imgur.com/1oGuC0N">



Decryption:
The first step for the decryption process is to decode the user inputted base64 IV & Key 
into byte arrays. The code continues the same as SymmetrixExample, with the only significant
changes being the assignment of OUTPUT to decryptedPath and INPUT to the InputStream. 

The fillParams() method handles all the syntax of the dec keyword, so that decrypt() will
always be working with 2 Strings and 2 Paths. The program will exit without the correct 
number of arguments and if the input (file) or output (path) doesn't exist.

*SCREENSHOT DEMONSTRATION*

 <img src="https://imgur.com/kLT3zN1">
