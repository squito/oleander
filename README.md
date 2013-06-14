oleander
========

JVM data structures that know how to live right in a byte array.

This is *NOT* the same thing as serialization.  While java serialization is great, it reads
a data structure out of a byte array, and then puts the new data structure somewhere *else*
in memory.  In general, this is the behavior you want; but with distributed systems like
Hadoop & Spark, sometimes you just want to use those raw bytes directly, to save the time
and memory involved in serialization & deserialization.  

Unfortunately, b/c java doesn't
allow direct casts from byte arrays to other data types, this comes at some cost in performance.
But in some use cases, the decreased performance may be worth the memory savings & the time saved.
