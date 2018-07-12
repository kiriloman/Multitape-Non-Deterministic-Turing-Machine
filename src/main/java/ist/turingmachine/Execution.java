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
    private State currentState;

    public Execution(List<Tape> tapes, List<State> states) {
        this.states = states;
        this.tapes = tapes;
        currentState = null;
        for (State state : states) {
            state.setNextStates(findNextStates(states, state));
        }
    }
/*
    public List<State> getExecutableStates() {
        if (state == null) {
            return getInitialStates();
        }
        return state.getExecutableNextStates(tapes);
    }

    // maybe begin always on the first state? YES // refactor
    private List<State> getInitialStates() {
        String firstStateName = states.get(0).getName();
        List<State> possibleInitialStates = new ArrayList<>();
        boolean found;
        for (State state : states) {
            if (!state.getName().equals(firstStateName))
                return possibleInitialStates;
            found = true;
            for (int j = 0; j < tapes.size() && found; j++) {
                if (state.getRead().charAt(j) != '*' && state.getRead().charAt(j) != tapes.get(j).getHeadContent()) {
                    found = false;
                    break;
                }
            }
            if (found)
                possibleInitialStates.add(state);
        }
        return possibleInitialStates;
    }
*/
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

    private void modifyContent(Integer tapeId, State state) {
        if (!state.getWrite().equals("*")) {
            int head = tapes.get(tapeId).getHead();
            tapes.get(tapeId).getContent().remove(head);
            tapes.get(tapeId).getContent().add(head, state.getWrite().charAt(tapeId));
        }
    }

    private void move(Integer tapeId, State state) {
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

    public void execute(State state) {
        System.out.println(state.toString() + " executing this state");
        // executes current state
        for (int i = 0; i < tapes.size(); i++) {
            modifyContent(i, state);
            move(i, state);
        }
        // searches for next state
        /*List<State> executableNextStates = state.getExecutableNextStates(tapes);
        if (executableNextStates.size() != 0 && executableNextStates.get(0) != null) {
            state = executableNextStates.get(0);
        }*/
        currentState = state;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
}