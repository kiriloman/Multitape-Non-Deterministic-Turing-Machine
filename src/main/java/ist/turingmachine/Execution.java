package ist.turingmachine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO: review, remove _, String -> char, find states into 1 method, REMOVE SERIALIZABLE
public class Execution implements Serializable {
    private List<Tape> tapes;
    public List<String> states, read, write, move, goToNextState;

    public Execution(List<Tape> tapes, List<String> states, List<String> read, List<String> write, List<String> move, List<String> goToNextState) {
        this.tapes = tapes;
        this.states = states;
        this.read = read;
        this.write = write;
        this.move = move;
        this.goToNextState = goToNextState;
    }

    public List<Tape> getTapes() {
        return tapes;
    }

    public void modifyContent(Integer tapeId, String newChar) {
        if (!newChar.equals("*")) {
            int head = tapes.get(tapeId).getHead();
            tapes.get(tapeId).getContent().remove(head);
            tapes.get(tapeId).getContent().add(head, newChar);
        }
    }

    //switch somehow
    public void move(Integer tapeId, String direction) {
        int head = tapes.get(tapeId).getHead();
        if (head == tapes.get(tapeId).getContent().size() - 1 && direction.toLowerCase().equals("r")) {
            tapes.get(tapeId).getContent().add("_");
            tapes.get(tapeId).setHead(head + 1);
        } else {
            if (head == 0 && direction.toLowerCase().equals("l")) {
                tapes.get(tapeId).getContent().add(0, "_");
            } else {
                if (direction.toLowerCase().equals("r")) {
                    tapes.get(tapeId).setHead(head + 1);
                } else {
                    if (direction.toLowerCase().equals("l")) {
                        tapes.get(tapeId).setHead(head - 1);
                    }
                }
            }
        }
    }

    public List<Integer> find_states(List<String> states, String current_state) {
        List<Integer> possible_states_indexes = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).equals(current_state)) {
                possible_states_indexes.add(i);
            }
        }
        return possible_states_indexes;
    }

    public List<Integer> find_states_that_work() {
        String current_state = tapes.get(0).getState();
        List<Integer> poss = find_states(states, current_state);
        List<Integer> states_that_work = new ArrayList<>();
        Boolean pass;
        for (int j = 0; j < poss.size(); j++) {
            pass = true;
            for (int i = 0; i < tapes.size() && pass; i++) {
                if (!(read.get(poss.get(j)).charAt(i) == '*' || tapes.get(i).getContent().get(tapes.get(i).getHead()).equals(Character.toString(read.get(poss.get(j)).charAt(i))))) {
                    pass = false;
                }
            }
            if (pass) {
                states_that_work.add(poss.get(j));
            }
        }
        if (states_that_work.size() > 1) {
            return compare(states_that_work);
        }
        return states_that_work;
    }

    public List<Integer> compare(List<Integer> states_that_work) {
        int k;
        List<Integer> integers = new ArrayList<>(), states_to_return = new ArrayList<>();
        for (int i = 0; i < states_that_work.size(); i++) {
            k = 0;
            for (int j = 0; j < tapes.size(); j++) {
                if (read.get(states_that_work.get(i)).charAt(j) == '*') {
                    k++;
                }
            }
            integers.add(k);
        }
        int smallest = integers.get(0);
        for (int i = 1; i < integers.size(); i++) {
            if (integers.get(i) < smallest) {
                smallest = integers.get(i);
            }
        }
        for (int i = 0; i < integers.size(); i++) {
            if (integers.get(i) == smallest) {
                states_to_return.add(states_that_work.get(i));
            }
        }
        return states_to_return;
    }

    //for?
    public void execute(Integer index) {
        for (int i = 0; i < tapes.size(); i++) {
            modifyContent(i, Character.toString(write.get(index).charAt(i)));
            move(i, Character.toString(move.get(index).charAt(i)));
            if (goToNextState.size() > index && goToNextState.get(index) != null) {
                tapes.get(i).setState(goToNextState.get(index));
            }
        }
    }
}