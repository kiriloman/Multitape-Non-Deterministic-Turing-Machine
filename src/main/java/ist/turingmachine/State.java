package ist.turingmachine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO: Review for loops, REMOVE SERIALIZABLE
public class State implements Comparable<State>, Serializable {
    private String name, read, write, move, nextState;
    private List<State> nextStates;

    public State(String state) {
        String[] partsOfState = state.split(" ");
        name = partsOfState[0];
        read = partsOfState[1];
        write = partsOfState[2];
        move = partsOfState[3];
        nextState = partsOfState[4];
        nextStates = new ArrayList<>();
    }

    //review (inutil?)
    public List<State> getExecutableNextStates(List<Tape> tapes) {
        List<State> executableStates = new ArrayList<>();
        boolean pass;
        for (int j = 0; j < nextStates.size(); j++) {
            pass = true;
            for (int i = 0; i < tapes.size() && pass; i++) {
                if (!(read.charAt(i) == '*' || tapes.get(i).getHeadContent() == read.charAt(i))) {
                    pass = false;
                }
            }
            if (pass) {
                executableStates.add(nextStates.get(j));
            }
        }
        Collections.sort(executableStates);
        return executableStates;
    }

    public void setNextStates(List<State> nextStates) {
        this.nextStates = nextStates;
    }

    public String getName() {
        return name;
    }

    public String getRead() {
        return read;
    }

    public String getWrite() {
        return write;
    }

    public String getMove() {
        return move;
    }

    public String getNextState() {
        return nextState;
    }

    @Override
    public int hashCode() {
        return 31 + name.hashCode() + read.hashCode() + write.hashCode() + move.hashCode() + nextState.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof State)) return false;
        State other = (State) o;
        return (name.equals(other.name) &&
                read.equals(other.read) &&
                write.equals(other.write) &&
                move.equals(other.move) &&
                nextState.equals(other.nextState));
    }

    @Override
    public int compareTo(State o) {
        int thisNumOfAsterisks = 0;
        int otherNumOfAsterisks = 0;
        for (int i = 0; i < read.length(); i++) {
            if (read.charAt(i) == '*')
                thisNumOfAsterisks++;
            if (o.read.charAt(i) == '*')
                otherNumOfAsterisks++;
        }
        return thisNumOfAsterisks - otherNumOfAsterisks;
    }

    @Override
    public String toString() {
        return name + " " + read + " " + write + " " + move + " " + nextState;
    }
}
