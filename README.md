# Altair 8800

This is a very simple emulator of the first microcomputer - MITS Altair 8800.

![MITS Altair 8800](http://i.imgur.com/I9HdSVf.jpg)

The main goal was to be able to run the original image of MITS BASIC 4K 3.2 on it, which happens to work now.

The emulator has no GUI, no front panel leds, switches etc. It uses user terminal as it was attached physically to the Altair via a serial line. That is why a terminal emulator with VT100 escape sequences support is required at the moment (so it probably won't work under Windows, but who cares...)

### Building and running
You need Maven to build it: ```$ mvn package```

Running: ```$ ./run.sh```

It will load BASIC into memory and start executing it. Sample output:

    MEMORY SIZE? 4096
    TERMINAL WIDTH? 40
    WANT SIN? Y
    
    726 BYTES FREE
    
    BASIC VERSION 3.2
    [4K VERSION]
    
    OK
    PRINT "HELLO, WORLD!"
    HELLO, WORLD!
    
    OK
    PRINT 23*46
     1058 
    
    OK
    PRINT 3.14159 * 5 * 5
     78.5398 
    
    OK

### TODO
* Find a way to slow down input/output, so that it resembles baud rate of 9600
* Implement MITS 88-2SIO serial controller
* Test interrupts with some actual running code
