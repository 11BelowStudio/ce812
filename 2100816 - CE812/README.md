# A Scientific Interpretation of Daily Life in the Space Towing Industry, Circa 3052 CE

A game made for CE812 Physics-Based Games

### Software/hardware requirements

* Compulsory
    * A computer
    * Java 8 or later installed on your computer
    * A keyboard plugged in to your computer
* Optional
    * A mouse (recommended)
    * Speakers
    * A monitor (HIGHLY recommended!)

### What each file/folder is

* README.md
    * This file you are reading right now.
* LICENSE.txt
    * The terms of the Mozilla Public License 2.0 (which CRAPPY is released under)
* SpaceTow.jar
    * This is *A Scientific Interpretation of Daily Life in the Space Towing Industry, Circa 3052 CE*,
      in executable .jar form
* 210086 - CE812 Report.pdf
    * The obligatory report on the coursework
* lib/
    * Contains [junit-4.13.2.jar](https://mvnrepository.com/artifact/junit/junit/4.13.2),
      without which this game won't actually compile if you try building it from source.
* bin/
    * Contains the compiled .class files for the game (with all of the resources).
      * Main class is `crappyGame.GameRunner`
        * bin/crappyGame/GameRunner.class
* src/
    * Contains the source .java files for the game (with all of the resources).
        * Main class is `crappyGame.GameRunner`
            * src/crappyGame/GameRunner.java

## How to run the game from source via terminal/command line

Windows users
```
java -Dfile.encoding=UTF-8 -classpath "lib\junit-4.13.2.jar;bin" crappyGame.GameRunner
```

Linux/Mac users
```
java -Dfile.encoding=UTF-8 -classpath "lib\junit-4.13.2.jar:bin" crappyGame.GameRunner
```

~~the `-Dfile.encoding=UTF-8` optional, but is included here just in case omitting it gets awkward.~~



## How to run the game via the jar using terminal/command line


```
java -jar SpaceTow.jar 
```

alternatively, you can just click on the SpaceTow.jar file,
and open that using Java if asked.

