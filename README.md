# Multitape Non Deterministic Turing Machine Simulator
An ```accept state``` seeking multitape non deterministic Turing machine simulator allows you to write and execute any Turing machine program with respect to **Syntax** and with no constraints on the amount of tapes used. In non deterministic cases it uses [Breadth-first search](https://en.wikipedia.org/wiki/Breadth-first_search) to find a path to a halting state, preferably a halt-accept state.

## Getting Started
To obtain a copy of the simulator just download Turing Machine.jar

### Prerequisites
* [Java 8] (http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html)

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

4. Tapes: put one in each line;
5. Pre-execution controls:

 1. **Decision Sequence**: shows the decisions made my machine until current moment; is used in non-deterministic cases. Will automatically fill itself when a transition is executed. Explanation on how to use it is in ***A Closer Look At Decision Sequence***;
 2. **Clear**: clears **Decision Sequence**;
 3. **Pick every step**: in non deterministic cases the machine will offer you to pick any possible transition you like;
 4. **Open**: opens a program from your computer;
 5. **Save**: saves the program written in **Program** area to your computer.
 
6. Syntax: syntax to follow on creation of a program;
7. Log: illustrates all executed transitions until current moment.

### Running The Simulator

1. Load one of the program on your computer, or write your own in the **Program** area.
2. Enter your tapes in **Tapes** area. They will be written in **Transitions** area and the heads will be highlighted. Enter the sequence in the **Decision Sequence** field if needed. Click **Set** to initialise the machine.
3. Click on **Run** to start the Turing machine and run it until it halts (if ever). Click on **Pause/Resume** to interrupt the Turing machine while it is running. Alternatively, click **Step** to run a single step of the Turing machine.
4. Click **Set** to restore the Turing machine to its initial state so it can be run again.

### A Closer Look At Decision Sequence

Assuming that you know what a [non deterministic Turing machine](https://en.wikipedia.org/wiki/Non-deterministic_Turing_machine) is and have read about [Breadth-first search](https://en.wikipedia.org/wiki/Breadth-first_search), clarification on what **Decision Sequence** is and how to use it follows:

* When the Turing machine halts the decision sequence's content will be a string of numbers which explains the path in a tree from the initiation to the halting state;
* If not yet in halting state, the decision sequence will keep updating showing the path followed until current moment;

It is possible to transform a non deterministic problem into a deterministic one using the decision sequence.
Before initiating the machine you should type in the path you want the machine to take. It will follow it strictly and will perform every transition mentioned (if such exists).

It is **possible** to add values to the decision sequence in following cases:  

1. Before initiating the tapes and the machine.
2. Every time machine stops executing previous given sequence.

It is **not possible** to add values to the decision sequence in following cases:  

1. An initial sequence wasn't given.
2. An initial sequence was given, but the machine didn't finish its execution yet.
3. An initial sequence was given, the machine finished executing it and stopped not reaching any halt state and you didn't add any values before running again.

#### Example

#### No Initial Input

For the sake of a simple example let's use a not well formed non deterministic program and a single tape ```011``` with its head on ```0```.
Program:  

0 0 a r 0  
0 0 b r 1  
1 0 a r 1  
1 1 a r 1  
1 _ _ * halt  

Transition | Tape Content | Head | Decision Sequence
---------- | ------------ | ---- | -----------------
Initiation | 011          | 0    |
0 0 a r 0  | a11          | 1    | 1
Backtrack  | 011          | 0    |
0 0 b r 1  | b11          | 1    | 2
1 1 a r 1  | ba1          | 1    | 21
1 1 a r 1  | baa*_*       | *_*  | 211 
1 *_* *_* * halt | baa*_* | *_*  | 2111

As the last transition leads to a halting state the execution finishes and the final decision sequence is ```2111```.

#### With Initial Input

Let's use the example above.

* If you use ```2111``` as initial input in decision sequence, the machine will halt within 4 transitions because this sequence will take it to a halting state.
* If you, for example, use ```211``` as initial input, the machine will stop after those 3 transitions and you can add values to it before running it again.
* If you, for example, use ```3``` as initial input, the machine will abort because there are only 2 possible transitions for state ```0``` and head ```0```.
* If you, for example, use ```2``` as initial input and after machine stops you don't add any values to it and just run, the machine will run normally finishing in 4 transitions and decision sequence ```2111```.


