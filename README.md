# Multitape Non Deterministic Turing Machine Simulator
An ```accept state``` seeking multitape non deterministic Turing machine simulator allows you to write and execute any Turing machine program with respect to **Syntax** and no constraints on the amount of tapes. In non deterministic cases it uses [Breadth-first search](https://en.wikipedia.org/wiki/Breadth-first_search) to find a path to a halting state, preferably a halt-accept state.

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

### Meanings

1. Transitions: the highlighted symbol indicates the head of a tape;
2. Program;
3. Controls:

 1. **Steps**: how many transitions have been executed until current moment;
 2. **Run**: executes the program;
 3. **Run at full speed**: runs as fast as your computer allows;
 4. **Step**: executes one transition;
 5. **Pause/Resume**: pauses/resumes the execution;
 6. **Set**: initiates the tapes and machine.

4. Tapes. One in each line;
5. Pre-execution controls

 1. **Decision Sequence**: shows the decisions made my machine until current moment; is used in non-deterministic cases. Will automatically fill itself when a transition is executed. Explanation on how to use in ***A Closer Look At Decision Sequence***;
 2. **Clear**: clears **Decision Sequence**;
 3. **Pick every step**: in non deterministic cases the machine will offer you to pick any possible transition you like;
 4. **Open**: opens a program from your computer;
 5. **Save**: saves the program written in **Program** field to your computer.
 
6. Syntax: syntax to follow on creation of a program;
7. Log: illustrates all executed transitions until current moment.

### A Closer Look At Decision Sequence

Assuming that you know what a [non deterministic Turing machine](https://en.wikipedia.org/wiki/Non-deterministic_Turing_machine) is, clarification on how to use **Decision Sequence** will follow.

Let's use a simple non deterministic program which can stay in state **0** in infinite loop or go to state **1** and halt.

0 * a r 0
0 0 b r 1

1 0 a r 1
1 1 a r 1 
1 _ _ * halt








![Alt text](Images/add_fitas.png?raw=true)
