# PiCalculator

A simple java program to calculate pi to arbitrary precision using a series discovered by Ramanujan. Multiple threads may be used to speed up calculation.

The code calculates 1 000 000 digits in about 5 seconds (on a i7-4720HQ) on 8 threads.

You can compile the code with maven - just run - `mvn clean package` in the directory where the pom.xml file is located.

##Usage

You can use the compiled jar from the console with the following options:
- `-p precision` - specify the wanted precision to which to approximate pi (for e.g. -p 1000000 will calculate 1 million digits of pi)
- `-t numberOfThreads` - specify the number of threads to run the program on
- `-o outputFile` - specify where to write the calculated pi - by default it will write to a file in the directory where the program is run from
- `-q` - this way the program generates almost no output to the console
- `-b` - runs a benchmark on 1 to (2 * numberOfCores) threads. If this is selected `-t` is ignored and `-q` is on. Also calculated pi will not be written to disk.
