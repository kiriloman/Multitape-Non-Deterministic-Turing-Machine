package ist.turingmachine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

//TODO: REMOVE SERIALIZABLE, review for loops (for each?)
public class Execution implements Serializable {
    private List<Tape> tapes;
    private List<State> states;
    private String currentStateName;

    public Execution(List<Tape> tapes, List<State> states) {
        this.states = states;
        this.tapes = tapes;
        currentStateName = null;
        /*for (State state : states) {
            state.setNextStates(findNextStates(states, state));
        }*/
    }

    //encontra os estados executaveis
    public List<State> findExecutableStates() {
        List<State> nextStates = new ArrayList<>();
        boolean found;
        for (State state : states) {
            if (!state.getName().equals(currentStateName))
                continue;
            found = true;
            for (int j = 0; j < tapes.size(); j++) {
                if (state.getRead().charAt(j) != '*' && state.getRead().charAt(j) != tapes.get(j).getHeadContent()) {
                    found = false;
                    break;
                }
            }
            if (found)
                nextStates.add(state);
        }
        Collections.sort(nextStates);
        clean(nextStates);
        return nextStates;
    }

    private void clean(List<State> states) {
        if (states.size() == 0 || states.get(0).getRead().equals("*"))
            return;
        else {
            Iterator iterator = states.iterator();
            while (iterator.hasNext()) {
                State state = (State) iterator.next();
                if (state.getRead().equals("*"))
                    iterator.remove();
            }
        }
    }

    /*private List<State> findNextStates(List<State> states, State state) {
        List<State> nextStates = new ArrayList<>();
        for (State nextState : states) {
            if (nextState.getName().equals(state.getNextState())) {
                nextStates.add(nextState);
            }
        }
        return nextStates;
    }*/

    /*public List<State> possibleStates() {
        List<State> possibleStates = new ArrayList<>();
        for (State state : states) {
            if (state.getName().equals(currentStateName) && state.isExecutable(tapes))
                possibleStates.add(state);
        }
        return possibleStates;
    }*/

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
        currentStateName = state.getNextState();
    }

    public String getCurrentStateName() {
        return currentStateName;
    }

    public void setCurrentStateName(String currentStateName) {
        this.currentStateName = currentStateName;
    }
}