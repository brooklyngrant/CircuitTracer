****************
* Circuit Tracer
* CS 221
* 12/8/2020
* Brooklyn Grant
**************** 

OVERVIEW:

The goal of this assignment was to create a to read in circuit 
layouts in this format and output your results in a similar output 
format to show that I can use stacks and queues with the situation.


INCLUDED FILES:

 * CircuitBoard.java
 * CircuitTracer.java
 * CircuitTracerTester.java
 * InvalidFileFormatException.java
 * OccupiedPositionException.java
 * Storage.java
 * TraceState.java
 * README - this file


COMPILING AND RUNNING:

Open java file in eclipse. Press the green run arrow on the top left.
Enjoy!


PROGRAM DESIGN AND IMPORTANT CONCEPTS:

The point of this assignment was to:
 - Complete the CircuitBoard constructor by making sure that the 
 constructor should parse the input file and populate the char[][] 
 board class variable and that any formatting problems with the input 
 file should cause an InvalidFileFormatException to be thrown from 
 the constructor. Pretty much read the file and set it as a char[][] 
 varible.
 - Complete the CircuitTracer class by making sure it:
    - validates the three command-line arguments (first is 
    stack/queue, second is console/gui, third is the file name)
    - constructs a CircuitBoard from the input file
    - searches for all optimal trace paths using the user's storage 
    choice
    - reports the results according to the user's display choice
    - Uses conditional checks and exception handling to 
    appropriately deal with input errors from the command line and 
    the input file.


TESTING:

I used the CircuitTracerTester.java to test if it works, and it did.
I got 100% for all ten files, and it all works accordingly.


DISCUSSION:
 
This project was a very fun and interesting project, I really liked 
getting the pseudo-code for the search algorithm because I was able
to really think through why I was doing what I was doing, and it gave
me a rough outline of what I had to do, and I liked that most of it was 
already laid out so it was focused on logic. Some struggles I had where 
that when testing, I did not realize that the console had to be in a
certain format, so I spent a lot of time trying to figure out why
the test was failing for all of the console views. I also had 
trouble getting the file invalid8 to throw an error, because it had
the right amount of cells, but the rows and the columns were 
switched. Since a number times another number will always be the same
no matter what order it is in, I could not use number of cells like I
previously was doing. I knew what I had to do, but I really didn't 
what to have to redo the grid getting scanned in. Eventually I caved 
and changed it to scan line by line rather than cell by cell (char by
char). It worked after that!

 
EXTRA CREDIT:

 - Fully functional GUI
