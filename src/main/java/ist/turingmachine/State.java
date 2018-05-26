package ist.turingmachine;

import java.util.ArrayList;
import java.util.List;

public class State {
    private String name, read, write, nextState;
    private List<State> nextStates;

    public State(String state) {
        String[] partsOfState = state.split(" ");
        name = partsOfState[0];
        read = partsOfState[1];
        write = partsOfState[2];
        nextState = partsOfState[3];
        nextStates = new ArrayList<>();
    }

    private void setNextStates(List<State> states) {
        nextStates = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).name.equals(nextState)) {
                nextStates.add(states.get(i));
            }
        }
    }

    public List<State> getNextStates() {
        return nextStates;
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

    public String getNextState() {
        return nextState;
    }

    @Override
    public int hashCode() {
        return 31 + name.hashCode() + read.hashCode() + write.hashCode() + nextState.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof State)) return false;
        State other = (State) o;
        return (name.equals(other.name) &&
                read.equals(other.read) &&
                write.equals(other.write) &&
                nextState.equals(other.nextState));
    }
}
