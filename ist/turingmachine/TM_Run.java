package ist.turingmachine;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.util.ArrayList;

public class TM_Run extends TM_Gui implements Runnable {
    public static ArrayList<String> states, read, write, move, goToState;
    public static ArrayList<Tape> tapes_with_content;
    public static int numpassos, number_of_tapes, decision_number, state_picked;
    public static boolean decisions_enabled;
    public static String decisions_string = "";
    @Override
    public void run() {
        tapes_with_content = new ArrayList<>();
        paneLog.setText("");
        numpassos = 0;
        number_of_tapes = 0;
        paneTapesOutput.setText("");
        String codeInput = paneInput.getText();
        String str = "";
        BufferedReader reader = new BufferedReader(new StringReader(codeInput));
        ArrayList<ArrayList<String>> tapes = new ArrayList<>();
        try {
            str = reader.readLine();
            ArrayList<String> inner;
            while (str != null) {
            	if (!str.equals("Initial tapes here, one in each line")) {
	            	paneTapesOutput.setText(paneTapesOutput.getText() + str + "\n");
	                inner = new ArrayList<>();
	                for (int i = 0; i < str.length(); i++) {
	                	inner.add(Character.toString(str.charAt(i)));
	                }
	                tapes_with_content.add(new Tape(number_of_tapes));
	                number_of_tapes++;
	                tapes.add(inner);
            	}
            	str = reader.readLine();
            }
        } catch (IOException e1) {

        }

        String codeCommands = paneCode.getText();
        BufferedReader reader_com = new BufferedReader(new StringReader(codeCommands));
        try {
            states = new ArrayList<>();
            read = new ArrayList<>();
            write = new ArrayList<>();
            move = new ArrayList<>();
            goToState = new ArrayList<>();
            String[] parts;
            str = reader_com.readLine();
            while (str != null) {
                str = str.replaceAll("\\s+", " ").trim();
                parts = str.split(" ");
                if (!str.equals("") && !parts[0].startsWith(";")) {
                	if (parts.length >= 5) {
	                    states.add(parts[0]);
	                    read.add(parts[1]);
	                    write.add(parts[2]);
	                    move.add(parts[3]);
	                    goToState.add(parts[4]);
	                    System.out.println(parts[4].length() + " " + parts[4]);
                	}
                    else {
                    	paneTapesOutput.setText(paneTapesOutput.getText() + "\n" + str + " does not have the right number of read/write/move.");
                    	paneLog.setFocusable(true);
                        NonDeterministicField.setFocusable(true);
                        step.setEnabled(true);
                        run_used = false;
                        stepped = false;
                        reset_used = false;
                        paneLog.setCaretPosition(paneLog.getDocument().getLength());
                        choose_steps.setEnabled(true);
                    	return;
                    }
                }
                str = reader_com.readLine();
            }
        } catch (IOException e1) {

        }
        
        if (states.size() != 0) {
            for (int i = 0; i < tapes_with_content.size(); i++) {
                tapes_with_content.get(i).setContent(tapes.get(i));
                tapes_with_content.get(i).setHead(0);
                tapes_with_content.get(i).setState(states.get(0));
            }
            if (read.get(0).length() > number_of_tapes) {
            	System.out.println("in");
            	int diff = read.get(0).length() - number_of_tapes;
            	ArrayList<String> aux;
            	
            	for (int i = 0; i < diff; i++) {
            		aux = new ArrayList<>();
            		aux.add("_");
            		number_of_tapes++;
            		System.out.println("in2");
            		paneTapesOutput.setText(paneTapesOutput.getText() + "_" + "\n");
            		tapes_with_content.add(new Tape(number_of_tapes));
            		tapes_with_content.get(number_of_tapes - 1).setContent(aux);
                    tapes_with_content.get(number_of_tapes - 1).setHead(0);
                    tapes_with_content.get(number_of_tapes - 1).setState(states.get(0));
            	}
            }
        }
        else {
            paneTapesOutput.setText(paneTapesOutput.getText() + "\n" + "You forgot the program.");
        }
        
        if (!paneTapesOutput.getText().equals("")) {
            try {
                String content = paneTapesOutput.getDocument().getText(0, paneTapesOutput.getDocument().getLength());
                int line = content.lastIndexOf("\n");
                paneTapesOutput.getDocument().remove(line, paneTapesOutput.getDocument().getLength() - line);
            } catch (BadLocationException e1) {

            }
        }
        System.out.println("sdla");
        
        System.out.println(check_coherence());
        if (check_coherence()) {
        	System.out.println("checked");	
            if (!NonDeterministicField.getText().equals("") && !NonDeterministicField.getText().equals("Decision Sequence")) {
                decision_number = 0;
                decisions_enabled = true;
                decisions_string = NonDeterministicField.getText();
            }
            else {
                NonDeterministicField.setText("");
                decision_number = 0;
                decisions_enabled = false;
            }
            NonDeterministicField.setHorizontalAlignment(SwingConstants.LEFT);
            NonDeterministicField.setOpaque(true);
            Execution execution = new Execution(tapes_with_content, states, read, write, move, goToState);
            try {
                treeSearchV2(execution);
            } catch (InterruptedException | BadLocationException e1) {

            }
        }
        paneLog.setFocusable(true);
        NonDeterministicField.setFocusable(true);
        step.setEnabled(true);
        run_used = false;
        stepped = false;
        reset_used = false;
        paneLog.setCaretPosition(paneLog.getDocument().getLength());
        choose_steps.setEnabled(true);
    }

    public static void treeSearchV2(Execution execution) throws BadLocationException, InterruptedException {
        draw(execution);
        String last_state = "", state_to_queue = "";
        boolean no_chill;
        int k = 0, l = 0;
        paused = true;
        ArrayList<Integer> states_to_use = new ArrayList<>();
        ArrayList<String> marked_states = new ArrayList<>();
        ArrayList<String> queue = new ArrayList<>();
        ArrayList<Execution> execution_clones = new ArrayList<>();
        int clone_number;
        marked_states.add("0");
        queue.add("0");
        execution_clones.add(execution);
        chill();
        while (queue.size() != 0) {
            if (execution_clones.get(0).find_states_that_work().size() > 0) {
                if (k == 1) {
                    k = 0;
                    no_chill = true;
                }
                else {
                    no_chill = false;
                    draw(execution_clones.get(0));
                    if (queue.get(0).substring(1).length() > decisions_string.length() || (queue.get(0).substring(1).length() <= decisions_string.length() && !queue.get(0).substring(1).equals(decisions_string) && !decisions_enabled)) {
                        NonDeterministicField.setText(queue.get(0).substring(1));
                    } else
                        NonDeterministicField.setText(decisions_string);
                }
            }
            else {
                k = 1;
                no_chill = true;
            }
            clone_number = execution_clones.size();

            if (decisions_enabled) {
                if (decision_number > decisions_string.length() - 1) {
                    paused = true;
                    choose_steps.setEnabled(true);
                    NonDeterministicField.setFocusable(true);
                    step.setEnabled(true);
                    if (last_state.equals("halt-accept") || last_state.equals("halt-reject") || last_state.equals("halt")) {
                        halt(execution_clones.get(execution_clones.size() - 1), last_state);
                        return;
                    }
                    highlight_decision(decision_number - 1);
                    chill();
                    if (NonDeterministicField.getText().length() > decisions_string.length()) {
                        decisions_string = NonDeterministicField.getText();
                        if (Character.getNumericValue(decisions_string.charAt(decision_number)) <= execution_clones.get(0).find_states_that_work().size()) {
                            states_to_use = new ArrayList<>();
                            states_to_use.add(execution_clones.get(0).find_states_that_work().get(Character.getNumericValue(decisions_string.charAt(decision_number)) - 1));
                            decision_number++;
                        }
                        else {
                            halt(execution_clones.get(execution_clones.size() - 1), "abort");
                            highlight_decision(decision_number);
                            return;
                        }
                    }
                    else {
                        decisions_enabled = false;
                        states_to_use = execution_clones.get(0).find_states_that_work();
                    }
                }
                else {
                    highlight_decision(decision_number - 1);
                    if (last_state.equals("halt-accept") || last_state.equals("halt-reject") || last_state.equals("halt")) {
                        halt(execution_clones.get(execution_clones.size() - 1), last_state);
                        return;
                    }
                    if (Character.getNumericValue(decisions_string.charAt(decision_number)) <= execution_clones.get(0).find_states_that_work().size()) {
                        states_to_use = new ArrayList<>();
                        states_to_use.add(execution_clones.get(0).find_states_that_work().get(Character.getNumericValue(decisions_string.charAt(decision_number)) - 1));
                        decision_number++;
                    }
                    else {
                        halt(execution_clones.get(0), "abort");
                        return;
                    }
                }
            }
            else {
                highlight_decision(decision_number - 1);
                states_to_use = execution_clones.get(0).find_states_that_work();
            }

            if (step_used && (execution_clones.size() != 1 || no_chill)) {
                paused = !paused;
            }
            for (int i = 0; i < states_to_use.size(); i++) {
                if (choose_steps.isSelected()) {
                    create_dialog(states_to_use);
                    if (state_picked != -1) {
                        l = states_to_use.indexOf(state_picked);
                        states_to_use = new ArrayList<>();
                        states_to_use.add(state_picked);
                        paused = false;
                    }
                    else
                        l = i;
                }
                else
                    l = i;
                if (last_state.equals("halt") || last_state.equals("halt-accept")) {
                    halt(execution_clones.get(execution_clones.size() - 1), last_state);
                    return;
                }
                if (states_to_use.size() <= 1 || i != 0) {
                    chill();
                }
                else {
                    if (step_used)
                        paused = !paused;
                }
                if (states_to_use.size() > 1) {
                    if (queue.get(0).substring(1).length() > decisions_string.length() || (queue.get(0).substring(1).length() <= decisions_string.length() && !queue.get(0).substring(1).equals(decisions_string)) && !decisions_enabled) {
                        NonDeterministicField.setText(queue.get(0).substring(1));
                    }
                    else
                        NonDeterministicField.setText(decisions_string);
                    highlight_decision(decision_number - 1);
                    if (step_used)
                        paused = !paused;
                    draw(execution_clones.get(0));
                    chill();
                }
                execution_clones.add((Execution) deepClone(execution_clones.get(0)));

                if (decisions_enabled) {
                    state_to_queue = queue.get(0) + String.valueOf(decisions_string.charAt(decision_number - 1));
                }
                else {
                    if (choose_steps.isSelected())
                        state_to_queue = queue.get(0) + (l + 1);
                    else
                        state_to_queue = queue.get(0) + (i + 1);
                }


                if (!marked_states.contains(state_to_queue)) {
                    paneLog.setText(paneLog.getText() + execution_clones.get(i + clone_number).states.get(states_to_use.get(i)) + "   " + execution_clones.get(i + clone_number).read.get(states_to_use.get(i)) + "   " + execution_clones.get(i + clone_number).write.get(states_to_use.get(i)) + "   " + execution_clones.get(i + clone_number).move.get(states_to_use.get(i)) + "   " + execution_clones.get(i + clone_number).goToNextState.get(states_to_use.get(i)) + "\n");
                    execution_clones.get(i + clone_number)._execute(states_to_use.get(i));
                    numpassos++;
                    draw(execution_clones.get(i + clone_number));
                    marked_states.add(state_to_queue);
                    queue.add(state_to_queue);

                    if (state_to_queue.substring(1).length() > decisions_string.length() || (state_to_queue.substring(1).length() <= decisions_string.length() && !state_to_queue.substring(1).equals(decisions_string) && !state_to_queue.equals("0") && !decisions_enabled)) {
                        NonDeterministicField.setText(state_to_queue.substring(1));
                    }
                    else
                        NonDeterministicField.setText(decisions_string);
                    highlight_decision(decision_number - 1);
                    if (step_used)
                        paused = !paused;
                } else {
                    if (queue.contains(state_to_queue)) {
                        paneLog.setText(paneLog.getText() + execution_clones.get(i + clone_number).states.get(states_to_use.get(i)) + "   " + execution_clones.get(i + clone_number).read.get(states_to_use.get(i)) + "   " + execution_clones.get(i + clone_number).write.get(states_to_use.get(i)) + "   " + execution_clones.get(i + clone_number).move.get(states_to_use.get(i)) + "   " + execution_clones.get(i + clone_number).goToNextState.get(states_to_use.get(i)) + "\n");
                        execution_clones.get(i + clone_number)._execute(states_to_use.get(i));
                        numpassos++;
                        draw(execution_clones.get(i + clone_number));
                        if (state_to_queue.substring(1).length() > decisions_string.length() || (state_to_queue.substring(1).length() <= decisions_string.length() && !state_to_queue.substring(1).equals(decisions_string) && !state_to_queue.substring(1).equals("0") && !decisions_enabled)) {
                            NonDeterministicField.setText(state_to_queue.substring(1));
                        }
                        else
                            NonDeterministicField.setText(decisions_string);
                        highlight_decision(decision_number - 1);
                        if (step_used)
                            paused = !paused;
                    }
                }
                last_state = execution_clones.get(i + clone_number).goToNextState.get(states_to_use.get(i)).toLowerCase();
            }

            if (states_to_use.size() == 0 && !last_state.equals("halt-reject") && !last_state.equals("halt") && !last_state.equals("halt-accept"))
                last_state = "";
            else {
                if (last_state.equals("halt") || last_state.equals("halt-accept")) {
                    halt(execution_clones.get(execution_clones.size() - 1), last_state);
                    return;
                }
                if (last_state.equals("halt-reject") && queue.size() == 2) {
                    halt(execution_clones.get(execution_clones.size() - 1), last_state);
                    return;
                }
            }
            if (queue.size() != 1) {
                queue.remove(0);
                execution_clones.remove(0);
            }
            else {
                halt(execution_clones.get(execution_clones.size() - 1), last_state);
                queue.remove(0);
            }
            if (!no_chill || k == 0) {
                if (decisions_enabled) {
                    if (decision_number != decisions_string.length())
                        chill();
                }
                else
                    chill();
            }
        }
    }

    public static void create_dialog(ArrayList<Integer> states_to_use) {
        if (states_to_use.size() != 1) {
            Object[] options = new Object[states_to_use.size()];
            for (int i = 0; i < states_to_use.size(); i++) {
                options[i] = states.get(states_to_use.get(i)) + " " + read.get(states_to_use.get(i)) + " " + write.get(states_to_use.get(i)) + " " + move.get(states_to_use.get(i)) + " " + goToState.get(states_to_use.get(i));
            }
            int action = JOptionPane.showOptionDialog(panelInput, "Pick a state.", "State picker", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (action != -1)
                state_picked = states_to_use.get(action);
            else {
                state_picked = -1;
                choose_steps.setSelected(false);
            }
        }
        else
            state_picked = -1;
    }
    public static void highlight_decision(int char_num) throws BadLocationException {
        if (char_num >= 0) {
            highlighter_decisions.removeAllHighlights();
            highlighter_decisions.addHighlight(char_num, char_num + 1, painter);
        }
    }
    public static void chill() throws InterruptedException {
        while (paused) {
            Thread.sleep(50);
        }
        if (!run_faster.isSelected()) {
            Thread.sleep(100);
        }
    }
    public static void halt(Execution execution, String halter) throws BadLocationException {
        int l = 0;
        switch (halter) {
            case "halt":
                paneTapesOutput.setText("");
                counterField.setText(String.valueOf(numpassos));
                for (int j = 0; j < execution.tapes_with_content.size(); j++) {
                    paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.tapes_with_content.get(j).getContent()) + "\n");
                }
                paneTapesOutput.setText(paneTapesOutput.getText() + "HALTED");
                for (int j = 0; j < execution.tapes_with_content.size(); j++) {
                    highlighter.addHighlight(l + execution.tapes_with_content.get(j).getHead(), l + execution.tapes_with_content.get(j).getHead() + 1, painter);
                    l += execution.tapes_with_content.get(j).getContent().size() + 1;
                }
                break;
            case "halt-accept":
                paneTapesOutput.setText("");
                counterField.setText(String.valueOf(numpassos));
                for (int j = 0; j < execution.tapes_with_content.size(); j++) {
                    paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.tapes_with_content.get(j).getContent()) + "\n");
                }
                paneTapesOutput.setText(paneTapesOutput.getText() + "ACCEPTED");
                for (int j = 0; j < execution.tapes_with_content.size(); j++) {
                    highlighter.addHighlight(l + execution.tapes_with_content.get(j).getHead(), l + execution.tapes_with_content.get(j).getHead() + 1, painter);
                    l += execution.tapes_with_content.get(j).getContent().size() + 1;
                }
                break;
            case "halt-reject":
                paneTapesOutput.setText("");
                counterField.setText(String.valueOf(numpassos));
                for (int j = 0; j < execution.tapes_with_content.size(); j++) {
                    paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.tapes_with_content.get(j).getContent()) + "\n");
                }
                paneTapesOutput.setText(paneTapesOutput.getText() + "REJECTED");
                for (int j = 0; j < execution.tapes_with_content.size(); j++) {
                    highlighter.addHighlight(l + execution.tapes_with_content.get(j).getHead(), l + execution.tapes_with_content.get(j).getHead() + 1, painter);
                    l += execution.tapes_with_content.get(j).getContent().size() + 1;
                }
                break;
            default:
                paneTapesOutput.setText("");
                counterField.setText(String.valueOf(numpassos));
                for (int j = 0; j < execution.tapes_with_content.size(); j++) {
                    paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.tapes_with_content.get(j).getContent()) + "\n");
                }
                paneTapesOutput.setText(paneTapesOutput.getText() + "ABORTED\nNO STATE TO FOLLOW");
                for (int j = 0; j < execution.tapes_with_content.size(); j++) {
                    highlighter.addHighlight(l + execution.tapes_with_content.get(j).getHead(), l + execution.tapes_with_content.get(j).getHead() + 1, painter);
                    l += execution.tapes_with_content.get(j).getContent().size() + 1;
                }
                break;
        }
    }
    public static void draw(Execution execution) throws BadLocationException {
        int l = 0;
        counterField.setText(String.valueOf(numpassos));
        paneTapesOutput.setText("");
        for (int j = 0; j < execution.tapes_with_content.size(); j++) {
            paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.tapes_with_content.get(j).getContent()) + "\n");
        }
        for (int j = 0; j < execution.tapes_with_content.size(); j++) {
            highlighter.addHighlight(l + execution.tapes_with_content.get(j).getHead(), l + execution.tapes_with_content.get(j).getHead() + 1, painter);
            l += execution.tapes_with_content.get(j).getContent().size() + 1;
        }
    }
    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
    public static String _arrayToString(ArrayList<String> array) {
        String str = "";
        for (int i = 0; i < array.size(); i++) {
            str += array.get(i);
        }
        return str;
    }
    public static boolean check_coherence() {
        if (states.size() == 0) {
            return false;
        }
        for (int i = 0; i < read.size(); i++) {
            if (read.get(i).length() != number_of_tapes || write.get(i).length() != number_of_tapes || move.get(i).length() != number_of_tapes) {
                paneTapesOutput.setText(paneTapesOutput.getText() + "\n" + states.get(i) + " " + read.get(i) + " " + write.get(i) + " " + move.get(i) + " " + goToState.get(i) + " does not have the right number of read/write/move.");
                return false;
            }
            for (int j = 0; j < move.get(i).length(); j++) {
                if (move.get(i).toLowerCase().charAt(j) != 's' && move.get(i).charAt(j) != '*' && move.get(i).toLowerCase().charAt(j) != 'l' && move.get(i).toLowerCase().charAt(j) != 'r') {
                    paneTapesOutput.setText(paneTapesOutput.getText() + "\n" + states.get(i) + " " + read.get(i) + " " + write.get(i) + " " + move.get(i) + " " + goToState.get(i) + " has wrong movement.");
                    return false;
                }
            }
        }
        return true;
    }
}
