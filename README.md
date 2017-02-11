# Multitape Non Deterministic Turing Machine Simulator
An accept-state seeking multitape non deterministic Turing machine simulator allows you to write and execute any Turing machine program with respect to **Syntax** and no constraints on the amount of tapes.

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
To open the simulator use 

```
java -jar "name of .jar"
```

### Syntax
Syntax inspired by [Anthony Morphett](http://morphett.info/turing/turing.html).

Any program must follow the structure:

 ```
 <current state> <character read> <character written> <direction to move> <state to transition into>
 ```
Use ```_``` to represent spaces, and ```*``` instead of a character read to mean any character, or instead of a character written/direction to mean no change. Refer to the example programs provided with the source to clarify.
