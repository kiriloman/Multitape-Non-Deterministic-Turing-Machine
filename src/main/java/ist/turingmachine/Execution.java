package ist.turingmachine;

import java.util.ArrayList;
import java.util.List;

public class Execution {
    public List<Tape> tapes_with_content;
    public List<String> states, read, write, move, goToNextState;

    public Execution(List<Tape> tapes_with_content, List<String> states, List<String> read, List<String> write, List<String> move, List<String> goToNextState) {
        this.tapes_with_content = tapes_with_content;
        this.states = states;
        this.read = read;
        this.write = write;
        this.move = move;
        this.goToNextState = goToNextState;
    }

    public void modifyContent(Integer tape_num, String modifier) {
        if (!modifier.equals("*")) {
            int h = tapes_with_content.get(tape_num).getHead();
            tapes_with_content.get(tape_num).getContent().remove(h);
            tapes_with_content.get(tape_num).getContent().add(h, modifier);
        }
    }

    public void move(Integer tape_num, String direction) {
        int h = tapes_with_content.get(tape_num).getHead();
        if (h == tapes_with_content.get(tape_num).getContent().size() - 1 && direction.toLowerCase().equals("r")) {
            tapes_with_content.get(tape_num).getContent().add("_");
            tapes_with_content.get(tape_num).setHead(h + 1);
        } else {
            if (h == 0 && direction.toLowerCase().equals("l")) {
                tapes_with_content.get(tape_num).getContent().add(0, "_");
            } else {
                if (direction.toLowerCase().equals("r")) {
                    tapes_with_content.get(tape_num).setHead(h + 1);
                } else {
                    if (direction.toLowerCase().equals("l")) {
                        tapes_with_content.get(tape_num).setHead(h - 1);
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
        String current_state = tapes_with_content.get(0).getState();
        List<Integer> poss = find_states(states, current_state);
        List<Integer> states_that_work = new ArrayList<>();
        Boolean pass;
        for (int j = 0; j < poss.size(); j++) {
            pass = true;
            for (int i = 0; i < tapes_with_content.size() && pass; i++) {
                if (!(read.get(poss.get(j)).charAt(i) == '*' || tapes_with_content.get(i).getContent().get(tapes_with_content.get(i).getHead()).equals(Character.toString(read.get(poss.get(j)).charAt(i))))) {
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
            for (int j = 0; j < tapes_with_content.size(); j++) {
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

    public void execute(Integer state_index) {
        for (int i = 0; i < tapes_with_content.size(); i++) {
            modifyContent(i, Character.toString(write.get(state_index).charAt(i)));
            move(i, Character.toString(move.get(state_index).charAt(i)));
            if (goToNextState.size() > state_index && goToNextState.get(state_index) != null) {
                tapes_with_content.get(i).setState(goToNextState.get(state_index));
            }
        }
    }
}
