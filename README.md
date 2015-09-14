CFMXDC
======

Re-wrote the bit-twiddling that was ColdFusion MX 6 administrator password encryption after
reverse engineering the Java archives jrun was executing. I used jad to decompile my own code
and then the JDK to re-compile it again with javac. Even though the two CLASS files are the
same size, the JVM byte code is actually different according to their SHA-512 checksums. See also [CVE-2010-2861](https://vulners.com/cve/CVE-2010-2861) and [cfide-autopwn](https://code.google.com/p/cfide-autopwn/).


