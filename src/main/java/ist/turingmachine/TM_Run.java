package ist.turingmachine;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TM_Run extends TM_Gui implements Runnable {
    public static List<Tape> tapes;
    public static List<State> states;
    public static int numpassos, number_of_tapes, decision_number, state_picked;
    public static boolean decisions_enabled;
    public static String decisions_string = "";


    private Tape createTape(String content) {
        Tape tape = new Tape(number_of_tapes);
        tape.setContent(content.chars().mapToObj(e -> (char) e).collect(Collectors.toList()));
        tape.setHead(0);
        number_of_tapes++;
        return tape;
    }

    //initializations to constructor
    @Override
    public void run() {
        tapes = new ArrayList<>();
        states = new ArrayList<>();
        paneLog.setText("");
        numpassos = 0;
        number_of_tapes = 0;
        paneTapesOutput.setText("");
        String codeInput = paneInput.getText();
        String str;
        BufferedReader reader = new BufferedReader(new StringReader(codeInput));
        //Lê e cria tapes
        try {
            str = reader.readLine();
            if (!str.equals("Initial tapes here, one in each line")) {
                paneTapesOutput.setText(paneTapesOutput.getText() + str + "\n"); //maybe no \n
                while (str != null) {
                    tapes.add(createTape(str));
                    str = reader.readLine();
                }
            }
        } catch (IOException e1) {
            System.out.println("Failed to create tapes.");
        }

        String codeCommands = paneCode.getText();
        BufferedReader programReader = new BufferedReader(new StringReader(codeCommands));
        //Lê o programa e cria states
        try {
            str = programReader.readLine();
            while (str != null) {
                str = str.trim();
                if (!str.equals("") && !str.startsWith(";")) {
                    states.add(new State(str));
                }
                str = programReader.readLine();
            }
        } catch (IOException e1) {
            System.out.println("Failed to create states.");
        }

        if (states.size() != 0) {
            if (states.get(0).getRead().length() > number_of_tapes) {
                tapes.add(createTape("_"));
            }
        } else {
            paneTapesOutput.setText(paneTapesOutput.getText() + "\n" + "You forgot the program.");
        }

        if (!paneTapesOutput.getText().equals("")) {
            try {
                String content = paneTapesOutput.getDocument().getText(0, paneTapesOutput.getDocument().getLength());
                int line = content.lastIndexOf("\n");
                paneTapesOutput.getDocument().remove(line, paneTapesOutput.getDocument().getLength() - line);
            } catch (BadLocationException e1) {
                System.out.println("This is ridiculous. Line 89 TM_RUN");
            }
        }

        //review this code
        if (check_coherence()) {
            System.out.println("checked");
            if (!NonDeterministicField.getText().equals("") && !NonDeterministicField.getText().equals("Decision Sequence")) {
                decision_number = 0;
                decisions_enabled = true;
                decisions_string = NonDeterministicField.getText();
            } else {
                NonDeterministicField.setText("");
                decision_number = 0;
                decisions_enabled = false;
            }
            NonDeterministicField.setHorizontalAlignment(SwingConstants.LEFT);
            NonDeterministicField.setOpaque(true);
            Execution execution = new Execution(tapes, states);
            try {
                System.out.println(states.toString() + " " + tapes.toString());
                treeSearchV2(execution);
            } catch (InterruptedException | BadLocationException e1) {
                System.out.println("Execution went wrong. Line 110.");
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
        //String last_state = "";
        State last_state = execution.getTapes().get(0).getState();
        String state_to_queue = "";
        boolean no_chill;
        int k = 0, l = 0;
        paused = true;
        List<State> states_to_use;
        ArrayList<String> marked_states = new ArrayList<>();
        ArrayList<String> queue = new ArrayList<>();
        ArrayList<Execution> execution_clones = new ArrayList<>();
        int clone_number;
        marked_states.add("0");
        queue.add("0");
        execution_clones.add(execution);
        chill();
        List<Tape> executionTapes;
        System.out.println();
        while (queue.size() != 0) {
            executionTapes = execution_clones.get(0).getTapes();
            //if (execution_clones.get(0).find_states_that_work().size() > 0) {
            if (executionTapes.get(0).getState().getExecutableNextStates(executionTapes).size() > 0) {
                if (k == 1) {
                    k = 0;
                    no_chill = true;
                } else {
                    no_chill = false;
                    draw(execution_clones.get(0));
                    if (queue.get(0).substring(1).length() > decisions_string.length() || (queue.get(0).substring(1).length() <= decisions_string.length() && !queue.get(0).substring(1).equals(decisions_string) && !decisions_enabled)) {
                        NonDeterministicField.setText(queue.get(0).substring(1));
                    } else
                        NonDeterministicField.setText(decisions_string);
                }
            } else {
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
                    if (last_state.getNextState().equals("halt-accept") || last_state.getNextState().equals("halt-reject") || last_state.getNextState().equals("halt")) {
                        halt(execution_clones.get(execution_clones.size() - 1), last_state);
                        return;
                    }
                    highlight_decision(decision_number - 1);
                    chill();
                    if (NonDeterministicField.getText().length() > decisions_string.length()) {
                        decisions_string = NonDeterministicField.getText();
                        if (Character.getNumericValue(decisions_string.charAt(decision_number)) <= executionTapes.get(0).getState().getExecutableNextStates(executionTapes).size()) {
                            states_to_use = new ArrayList<>();
                            states_to_use.add(executionTapes.get(0).getState().getExecutableNextStates(executionTapes).get(Character.getNumericValue(decisions_string.charAt(decision_number)) - 1));
                            decision_number++;
                        } else {
                            halt(execution_clones.get(execution_clones.size() - 1), last_state);
                            highlight_decision(decision_number);
                            return;
                        }
                    } else {
                        decisions_enabled = false;
                        states_to_use = executionTapes.get(0).getState().getExecutableNextStates(executionTapes);
                    }
                } else {
                    highlight_decision(decision_number - 1);
                    if (last_state.getNextState().equals("halt-accept") || last_state.getNextState().equals("halt-reject") || last_state.getNextState().equals("halt")) {
                        halt(execution_clones.get(execution_clones.size() - 1), last_state);
                        return;
                    }
                    if (Character.getNumericValue(decisions_string.charAt(decision_number)) <= executionTapes.get(0).getState().getExecutableNextStates(executionTapes).size()) {
                        states_to_use = new ArrayList<>();
                        states_to_use.add(executionTapes.get(0).getState().getExecutableNextStates(executionTapes).get(Character.getNumericValue(decisions_string.charAt(decision_number)) - 1));
                        decision_number++;
                    } else {
                        halt(execution_clones.get(0), null);
                        return;
                    }
                }
            } else {
                highlight_decision(decision_number - 1);
                states_to_use = executionTapes.get(0).getState().getExecutableNextStates(executionTapes);
            }

            if (step_used && (execution_clones.size() != 1 || no_chill)) {
                paused = !paused;
            }

            State statePicked;
            for (int i = 0; i < states_to_use.size(); i++) {
                if (choose_steps.isSelected()) {
                    //create_dialog(states_to_use);
                    statePicked = create_dialog(states_to_use);
                    //if (state_picked != -1) {
                    if (statePicked != null) {
                        //l = states_to_use.indexOf(state_picked);
                        l = execution_clones.get(0).getStates().indexOf(statePicked);
                        states_to_use = new ArrayList<>();
                        //states_to_use.add(state_picked);
                        states_to_use.add(statePicked);
                        paused = false;
                    } else
                        l = i;
                } else
                    l = i;
                if (last_state.getNextState().equals("halt") || last_state.getNextState().equals("halt-accept")) {
                    halt(execution_clones.get(execution_clones.size() - 1), last_state);
                    return;
                }
                if (states_to_use.size() <= 1 || i != 0) {
                    chill();
                } else {
                    if (step_used)
                        paused = !paused;
                }
                if (states_to_use.size() > 1) {
                    if (queue.get(0).substring(1).length() > decisions_string.length() || (queue.get(0).substring(1).length() <= decisions_string.length() && !queue.get(0).substring(1).equals(decisions_string)) && !decisions_enabled) {
                        NonDeterministicField.setText(queue.get(0).substring(1));
                    } else
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
                } else {
                    if (choose_steps.isSelected())
                        state_to_queue = queue.get(0) + (l + 1);
                    else
                        state_to_queue = queue.get(0) + (i + 1);
                }


                if (!marked_states.contains(state_to_queue)) {
                    paneLog.setText(paneLog.getText() + execution_clones.get(i + clone_number).getTapes().get(0).getState().toString() + "\n");
                    execution_clones.get(i + clone_number).execute(states_to_use.get(i));
                    numpassos++;
                    draw(execution_clones.get(i + clone_number));
                    marked_states.add(state_to_queue);
                    queue.add(state_to_queue);

                    if (state_to_queue.substring(1).length() > decisions_string.length() || (state_to_queue.substring(1).length() <= decisions_string.length() && !state_to_queue.substring(1).equals(decisions_string) && !state_to_queue.equals("0") && !decisions_enabled)) {
                        NonDeterministicField.setText(state_to_queue.substring(1));
                    } else
                        NonDeterministicField.setText(decisions_string);
                    highlight_decision(decision_number - 1);
                    if (step_used)
                        paused = !paused;
                } else {
                    if (queue.contains(state_to_queue)) {
                        paneLog.setText(paneLog.getText() + execution_clones.get(i + clone_number).getTapes().get(0).getState().toString() + "\n");
                        execution_clones.get(i + clone_number).execute(states_to_use.get(i));
                        numpassos++;
                        draw(execution_clones.get(i + clone_number));
                        if (state_to_queue.substring(1).length() > decisions_string.length() || (state_to_queue.substring(1).length() <= decisions_string.length() && !state_to_queue.substring(1).equals(decisions_string) && !state_to_queue.substring(1).equals("0") && !decisions_enabled)) {
                            NonDeterministicField.setText(state_to_queue.substring(1));
                        } else
                            NonDeterministicField.setText(decisions_string);
                        highlight_decision(decision_number - 1);
                        if (step_used)
                            paused = !paused;
                    }
                }
                last_state = states_to_use.get(i).getExecutableNextStates(execution_clones.get(i + clone_number).getTapes()).get(0);
            }

            if (states_to_use.size() == 0 && !last_state.getNextState().equals("halt-reject") && !last_state.getNextState().equals("halt") && !last_state.getNextState().equals("halt-accept"))
                //last_state = "";
                //do nothing
                last_state = last_state;
            else {
                if (last_state.getNextState().equals("halt") || last_state.getNextState().equals("halt-accept")) {
                    halt(execution_clones.get(execution_clones.size() - 1), last_state);
                    return;
                }
                if (last_state.getNextState().equals("halt-reject") && queue.size() == 2) {
                    halt(execution_clones.get(execution_clones.size() - 1), last_state);
                    return;
                }
            }
            if (queue.size() != 1) {
                queue.remove(0);
                execution_clones.remove(0);
            } else {
                halt(execution_clones.get(execution_clones.size() - 1), last_state);
                queue.remove(0);
            }
            if (!no_chill || k == 0) {
                if (decisions_enabled) {
                    if (decision_number != decisions_string.length())
                        chill();
                } else
                    chill();
            }
        }
    }

    public static State create_dialog(List<State> states_to_use) {
        State pickedState = null;
        if (states_to_use.size() != 1) {
            Object[] options = new Object[states_to_use.size()];
            for (int i = 0; i < states_to_use.size(); i++) {
                options[i] = states_to_use.get(i).toString();
            }
            int action = JOptionPane.showOptionDialog(panelInput, "Pick a state.", "State picker", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (action != -1)
                pickedState = states_to_use.get(action);
                //state_picked = states_to_use.get(action);
            else {
                //state_picked = -1;
                choose_steps.setSelected(false);
            }
        }
        //else
        //state_picked = -1;
        return pickedState;
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

    public static void halt(Execution execution, State halter) throws BadLocationException {
        int l = 0;
        if (halter != null) {
            try {
                chill();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            execution.execute(halter);
            //paneLog.setText(paneLog.getText() + execution.getTapes().get(0).getState().toString() + "\n");


            switch (halter.getNextState()) {
                case "halt":
                    paneTapesOutput.setText("");
                    counterField.setText(String.valueOf(numpassos));
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
                    }
                    paneTapesOutput.setText(paneTapesOutput.getText() + "HALTED");
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
                        l += execution.getTapes().get(j).getContent().size() + 1;
                    }
                    break;
                case "halt-accept":
                    paneTapesOutput.setText("");
                    counterField.setText(String.valueOf(numpassos));
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
                    }
                    paneTapesOutput.setText(paneTapesOutput.getText() + "ACCEPTED");
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
                        l += execution.getTapes().get(j).getContent().size() + 1;
                    }
                    break;
                case "halt-reject":
                    paneTapesOutput.setText("");
                    counterField.setText(String.valueOf(numpassos));
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
                    }
                    paneTapesOutput.setText(paneTapesOutput.getText() + "REJECTED");
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
                        l += execution.getTapes().get(j).getContent().size() + 1;
                    }
                    break;
            }
        } else {
            paneTapesOutput.setText("");
            counterField.setText(String.valueOf(numpassos));
            for (int j = 0; j < execution.getTapes().size(); j++) {
                paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
            }
            paneTapesOutput.setText(paneTapesOutput.getText() + "ABORTED\nNO STATE TO FOLLOW");
            for (int j = 0; j < execution.getTapes().size(); j++) {
                highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
                l += execution.getTapes().get(j).getContent().size() + 1;
            }
        }
    }

    public static void draw(Execution execution) throws BadLocationException {
        int l = 0;
        counterField.setText(String.valueOf(numpassos));
        paneTapesOutput.setText("");
        for (int j = 0; j < execution.getTapes().size(); j++) {
            paneTapesOutput.setText(paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
        }
        for (int j = 0; j < execution.getTapes().size(); j++) {
            highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
            l += execution.getTapes().get(j).getContent().size() + 1;
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

    public static String _arrayToString(List<Character> array) {
        String str = array.stream().map(e -> e.toString()).collect(Collectors.joining());
        return str;
    }
    //Change to check from a list of states
    private static boolean check_coherence() {
        if (states.size() == 0) {
            return false;
        }
        for (State state : states) {
            if (state.getRead().length() != number_of_tapes || state.getWrite().length() != number_of_tapes || state.getMove().length() != number_of_tapes) {
                paneTapesOutput.setText(paneTapesOutput.getText() + "\n" + state.toString() + " does not have the right number of read/write/move.");
                return false;
            }
            if (!state.getMove().toLowerCase().matches("[s|*|r|l]+")) {
                paneTapesOutput.setText(paneTapesOutput.getText() + "\n" + state.toString() + " has wrong movement.");
                return false;
            }
        }
        return true;
    }
}