# Multitape Non Deterministic Turing Machine Simulator
An ```accept state``` seeking multitape non deterministic Turing machine simulator allows you to write and execute any Turing machine program with respect to **Syntax** and no constraints on the amount of tapes.

## Getting Started
To obtain a copy of the simulator just download Turing Machine.jar

### Prerequisites
* [Java 8] (http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)

#### Linux

```
How to install on Linux
```

#### Mac Os

```
How to install on mac
```

#### Windows

```
How to install on Windows
```

### Usage
To open the simulator:

```
java -jar "path to .jar"
```

### Syntax
Syntax inspired by [Anthony Morphett](http://morphett.info/turing/turing.html).

Any program must follow the structure:

 ```
 <current state> <character read> <character written> <direction to move> <state to transition into>
 ```
Use ```_``` to represent spaces, and ```*``` instead of a character read to mean any character, or instead of a character written/direction to mean no change. Refer to ```Examples``` folder to clarify.


## User Guide

This simulator needs a program and a certain amount of tapes. The number of tapes depends on the program. 
When opened the simulator shows a GUI where you will be working.

![Alt text](Images/turingmachine.png?raw=true)

### Meanings:

1. Transitions;
2. Program;
3. Controls:

* **Steps**: how many transitions have been executed until current moment;
* **Run**: executes the program;
* **Run at full speed**: runs as fast as your computer allows;
* **Step**: executes one transition;
* **Pause/Resume**: pauses/resumes the execution;
* **Set**: initiates the tapes and machine.

4. Tapes. One in each line;
5. Pre-execution controls
* **Decision Sequence**: shows the decisions made my machine untill current moment; is used in non-deterministic cases. Will automatically fill itself when a transition is executed. Explanation on how to use in **A Closer Look At Decision Sequence**;
* **Clear**: clears **Decision Sequence**;
* **Pick every step**: 










![Alt text](Images/add_fitas.png?raw=true)
