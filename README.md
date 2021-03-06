# CFMXDC

* * *

## Summary
I re-wrote the bit-twiddling code that was the ColdFusion MX 6 administrator password encoding procedure after
reverse engineering Java archives that the J2EE `jrun` application server was executing. I used `jad` to decompile it, added my own driver code with a `Main` function and used the JDK to re-compile it again with `javac`. Even though the two CLASS files are the same size, the JVM byte code is actually different according to their SHA-512 checksums. See also [CVE-2010-2861](https://vulners.com/cve/CVE-2010-2861) and [cfide-autopwn](https://code.google.com/p/cfide-autopwn/).

* * *

## Background
When Adobe started maintaining ColdFusion--after Macromedia acquired it from Allaire, much of the code was ported to Java from Visual C++. One thing I noticed immediately about the new ColdFusion MX installation package was that some weak/predictable ciphertext representing the Administrator and Remote Developer Service (RDS) account passwords were left in a world readable setup log file named `installer.properties`. Typically, it would be found just above the web root directory, but sometimes right inside of it, thus making the password ciphers accessible remotely via the web site. Upon closer investigation and decompilation of the `.jar` (Java archive) installer, I discovered that the encoding function for user passwords was written from scratch with only a few sprinkles of bitwise operations. This allowed me to copy their decoding function almost exactly how the `jad` Java decompiler presented it to me. I was able to successfully decrypt plenty of password materials in the following years; it was essentially polyalphabetic substitution cipher with no salt, initialization vector, or entropy of any kind. Therefore, any local access to the box, ServerRoot directive misconfiguration, or poor input validation from a ColdFusion tag such as [CFQUERY](https://cfdocs.org/cfquery) or [CFFILE](https://cfdocs.org/cffile) would yield full access to the administrative console almost every time. Few webmasters move the ColdFusion administrative servlet out of the default `CFIDE` directory. There was a page on Adobe's old documentation site acknowledging the problem: [Adobe/Macromedia ColdFusion Support Center - Installation](http://web.archive.org/web/20120923044855/https://www.adobe.com/support/coldfusion/installation/installing_cfmx_on_unix/installing_cfmx_on_unix08.html "Macromedia ColdFusion - Installing Macromedia ColdFusion MX in silent mode: Installing with a properties file")  

* * * 
