package ist.turingmachine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Execution will save the state which it will execute next;
 */


//TODO: REMOVE SERIALIZABLE, review for loops (for each?)
public class Execution implements Serializable {
    private List<Tape> tapes;
    private List<State> states;
    private State state;

    public Execution(List<Tape> tapes, List<State> states) {
        this.states = states;
        this.tapes = tapes;
        state = getInitialState();
        for (State state : states) {
            state.setNextStates(findNextStates(states, state));
        }
    }

    // maybe begin always on the first state? YES
    private State getInitialState() {
        boolean searching;
        for (State state : states) {
            searching = true;
            for (int j = 0; j < tapes.size() && searching; j++) {
                if (state.getRead().charAt(j) != '*' && state.getRead().charAt(j) != tapes.get(j).getHeadContent())
                    searching = false;
            }
            if (searching)
                return state;
        }
        return null;
    }

    private List<State> findNextStates(List<State> states, State state) {
        List<State> nextStates = new ArrayList<>();
        for (State nextState : states) {
            if (nextState.getName().equals(state.getNextState())) {
                nextStates.add(nextState);
            }
        }
        return nextStates;
    }

    public List<Tape> getTapes() {
        return tapes;
    }

    public List<State> getStates() {
        return states;
    }

    private void modifyContent(Integer tapeId) {
        if (!state.getWrite().equals("*")) {
            int head = tapes.get(tapeId).getHead();
            tapes.get(tapeId).getContent().remove(head);
            tapes.get(tapeId).getContent().add(head, state.getWrite().charAt(tapeId));
        }
    }

    private void move(Integer tapeId) {
        int head = tapes.get(tapeId).getHead();
        switch (state.getMove().toLowerCase()) {
            case "r":
                if (head == tapes.get(tapeId).getContent().size() - 1) {
                    tapes.get(tapeId).getContent().add('_');
                    tapes.get(tapeId).setHead(head + 1);
                    break;
                }
                tapes.get(tapeId).setHead(head + 1);
                break;
            case "l":
                if (head == 0) {
                    tapes.get(tapeId).getContent().add(0, '_');
                    break;
                }
                tapes.get(tapeId).setHead(head - 1);
                break;
        }
    }

    public void execute() {
        // executes current state
        for (int i = 0; i < tapes.size(); i++) {
            modifyContent(i);
            move(i);
        }
        // searches for next state
        List<State> executableNextStates = state.getExecutableNextStates(tapes);
        if (executableNextStates.size() != 0 && executableNextStates.get(0) != null) {
            state = executableNextStates.get(0);
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}