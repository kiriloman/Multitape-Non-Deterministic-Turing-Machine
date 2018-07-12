package ist.turingmachine;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TuringMachine implements Runnable {
    private List<Tape> tapes;
    private List<State> states;
    private int numOfSteps, numOfTapes, decisionNumber;
    private boolean decisionEnabled;
    private String decisionString;
    private GUI gui;
    private Parser parser;

    TuringMachine() {
        tapes = new ArrayList<>();
        states = new ArrayList<>();
        numOfSteps = 0;
        numOfTapes = 0;
        decisionString = "";
        gui = new GUI();
        parser = new Parser();
        gui.Prepare();
        gui.Props();
    }

    private void outputTapes() {
        gui.paneTapesOutput.setText(gui.paneInput.getText()); // newline?
    }

    //initializations to constructor
    @Override
    public void run() {
        gui.paneLog.setText("");
        gui.paneTapesOutput.setText("");

        tapes = parser.parseTapes(gui.paneInput.getText());
        numOfTapes = tapes.size();
        outputTapes();

        states = parser.readStates(gui.paneCode.getText());


        if (states.size() != 0) {
            if (states.get(0).getRead().length() > numOfTapes) {
                tapes.add(createTape("_"));
            }
        } else {
            gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "\n" + "You forgot the program.");
        }

        if (!gui.paneTapesOutput.getText().equals("")) {
            try {
                String content = gui.paneTapesOutput.getDocument().getText(0, gui.paneTapesOutput.getDocument().getLength());
                int line = content.lastIndexOf("\n");
                gui.paneTapesOutput.getDocument().remove(line, gui.paneTapesOutput.getDocument().getLength() - line);
            } catch (BadLocationException e1) {
                System.out.println("This is ridiculous. Line 89 TM_RUN");
            }
        }

        //review this code
        if (check_coherence()) {
            System.out.println("checked");
            if (!gui.NonDeterministicField.getText().equals("") && !gui.NonDeterministicField.getText().equals("Decision Sequence")) {
                System.out.println("USING NON DETERMINISM");
                decisionNumber = 0;
                decisionEnabled = true;
                decisionString = gui.NonDeterministicField.getText();
            } else {
                System.out.println("NOT USING NON DETERMINISM");
                gui.NonDeterministicField.setText("");
                decisionNumber = 0;
                decisionEnabled = false;
            }
            gui.NonDeterministicField.setHorizontalAlignment(SwingConstants.LEFT);
            gui.NonDeterministicField.setOpaque(true);
            Execution execution = new Execution(tapes, states);
            try {
                System.out.println(states.toString() + " " + tapes.toString());
                treeSearchV2(execution);
            } catch (InterruptedException | BadLocationException e1) {
                System.out.println("Execution went wrong. Line 110.");
            }
        }
        gui.paneLog.setFocusable(true);
        gui.NonDeterministicField.setFocusable(true);
        gui.step.setEnabled(true);
        gui.run_used = false;
        gui.stepped = false;
        gui.reset_used = false;
        gui.paneLog.setCaretPosition(gui.paneLog.getDocument().getLength());
        gui.choose_steps.setEnabled(true);
    }

    private void treeSearchV2(Execution execution) throws BadLocationException, InterruptedException {
        draw(execution);
        State last_state = execution.getState();
        String state_to_queue = "";
        boolean no_chill;
        int k = 0, l = 0;
        gui.paused = true;
        List<State> states_to_use;
        ArrayList<String> marked_states = new ArrayList<>();
        ArrayList<String> queue = new ArrayList<>();
        ArrayList<Execution> execution_clones = new ArrayList<>();
        int clone_number;
        marked_states.add("0");
        queue.add("0");
        execution_clones.add(execution);
        chill();
        while (queue.size() != 0) {
            //System.out.println(execution_clones.get(0).getState().toString() + " state");
            if (execution_clones.get(0).getExecutableStates().size() > 0) {
                if (k == 1) {
                    k = 0;
                    no_chill = true;
                } else {
                    no_chill = false;
                    draw(execution_clones.get(0));
                    if (queue.get(0).substring(1).length() > decisionString.length() || (queue.get(0).substring(1).length() <= decisionString.length() && !queue.get(0).substring(1).equals(decisionString) && !decisionEnabled)) {
                        gui.NonDeterministicField.setText(queue.get(0).substring(1));
                    } else
                        gui.NonDeterministicField.setText(decisionString);
                }
            } else {
                k = 1;
                no_chill = true;
            }
            clone_number = execution_clones.size();

            if (decisionEnabled) {
                if (decisionNumber > decisionString.length() - 1) {
                    gui.paused = true;
                    gui.choose_steps.setEnabled(true);
                    gui.NonDeterministicField.setFocusable(true);
                    step.setEnabled(true);
                    if (last_state.getNextState().equals("halt-accept") || last_state.getNextState().equals("halt-reject") || last_state.getNextState().equals("halt")) {
                        halt(execution_clones.get(execution_clones.size() - 1), last_state);
                        return;
                    }
                    highlight_decision(decisionNumber - 1);
                    chill();
                    if (gui.NonDeterministicField.getText().length() > decisionString.length()) {
                        decisionString = gui.NonDeterministicField.getText();
                        if (Character.getNumericValue(decisionString.charAt(decisionNumber)) <= execution_clones.get(0).getExecutableStates().size()) {
                            states_to_use = new ArrayList<>();
                            states_to_use.add(execution_clones.get(0).getExecutableStates().get(Character.getNumericValue(decisionString.charAt(decisionNumber)) - 1));
                            decisionNumber++;
                        } else {
                            halt(execution_clones.get(execution_clones.size() - 1), last_state);
                            highlight_decision(decisionNumber);
                            return;
                        }
                    } else {
                        decisionEnabled = false;
                        states_to_use = execution_clones.get(0).getExecutableStates();
                    }
                } else {
                    highlight_decision(decisionNumber - 1);
                    if (last_state.getNextState().equals("halt-accept") || last_state.getNextState().equals("halt-reject") || last_state.getNextState().equals("halt")) {
                        halt(execution_clones.get(execution_clones.size() - 1), last_state);
                        return;
                    }
                    if (Character.getNumericValue(decisionString.charAt(decisionNumber)) <= execution_clones.get(0).getExecutableStates().size()) {
                        states_to_use = new ArrayList<>();
                        states_to_use.add(execution_clones.get(0).getExecutableStates().get(Character.getNumericValue(decisionString.charAt(decisionNumber)) - 1));
                        decisionNumber++;
                    } else {
                        halt(execution_clones.get(0), null);
                        return;
                    }
                }
            } else {
                highlight_decision(decisionNumber - 1);
                states_to_use = execution_clones.get(0).getExecutableStates();
            }

            if (gui.step_used && (execution_clones.size() != 1 || no_chill)) {
                gui.paused = !gui.paused;
            }

            State statePicked;
            System.out.println(states_to_use.toString() + " states to use");
            for (int i = 0; i < states_to_use.size(); i++) {
                if (gui.choose_steps.isSelected()) {
                    //create_dialog(states_to_use);
                    statePicked = create_dialog(states_to_use);
                    //if (state_picked != -1) {
                    if (statePicked != null) {
                        //l = states_to_use.indexOf(state_picked);
                        l = execution_clones.get(0).getStates().indexOf(statePicked);
                        states_to_use = new ArrayList<>();
                        //states_to_use.add(state_picked);
                        states_to_use.add(statePicked);
                        gui.paused = false;
                    } else
                        l = i;
                } else
                    l = i;
                if (last_state != null && (last_state.getNextState().equals("halt") || last_state.getNextState().equals("halt-accept"))) {
                    halt(execution_clones.get(execution_clones.size() - 1), last_state);
                    return;
                }
                if (states_to_use.size() <= 1 || i != 0) {
                    chill();
                } else {
                    if (gui.step_used)
                        gui.paused = !gui.paused;
                }
                if (states_to_use.size() > 1) {
                    if (queue.get(0).substring(1).length() > decisionString.length() || (queue.get(0).substring(1).length() <= decisionString.length() && !queue.get(0).substring(1).equals(decisionString)) && !decisionEnabled) {
                        gui.NonDeterministicField.setText(queue.get(0).substring(1));
                    } else
                        gui.NonDeterministicField.setText(decisionString);
                    highlight_decision(decisionNumber - 1);
                    if (gui.step_used)
                        gui.paused = !gui.paused;
                    draw(execution_clones.get(0));
                    chill();
                }
                execution_clones.add((Execution) deepClone(execution_clones.get(0)));

                if (decisionEnabled) {
                    state_to_queue = queue.get(0) + String.valueOf(decisionString.charAt(decisionNumber - 1));
                } else {
                    if (gui.choose_steps.isSelected())
                        state_to_queue = queue.get(0) + (l + 1);
                    else
                        state_to_queue = queue.get(0) + (i + 1);
                }


                if (!marked_states.contains(state_to_queue)) {
                    gui.paneLog.setText(gui.paneLog.getText() + execution_clones.get(i + clone_number).getState().toString() + "\n");
                    execution_clones.get(i + clone_number).execute(states_to_use.get(i));
                    //execution_clones.get(i + clone_number).setState(states_to_use.get(i));
                    numOfSteps++;
                    draw(execution_clones.get(i + clone_number));
                    marked_states.add(state_to_queue);
                    queue.add(state_to_queue);

                    if (state_to_queue.substring(1).length() > decisionString.length() || (state_to_queue.substring(1).length() <= decisionString.length() && !state_to_queue.substring(1).equals(decisionString) && !state_to_queue.equals("0") && !decisionEnabled)) {
                        gui.NonDeterministicField.setText(state_to_queue.substring(1));
                    } else
                        gui.NonDeterministicField.setText(decisionString);
                    highlight_decision(decisionNumber - 1);
                    if (gui.step_used)
                        gui.paused = !gui.paused;
                } else {
                    if (queue.contains(state_to_queue)) {
                        gui.paneLog.setText(gui.paneLog.getText() + execution_clones.get(i + clone_number).getState().toString() + "\n");
                        execution_clones.get(i + clone_number).execute(states_to_use.get(i));
                        //execution_clones.get(i + clone_number).setState(states_to_use.get(i));
                        numOfSteps++;
                        draw(execution_clones.get(i + clone_number));
                        if (state_to_queue.substring(1).length() > decisionString.length() || (state_to_queue.substring(1).length() <= decisionString.length() && !state_to_queue.substring(1).equals(decisionString) && !state_to_queue.substring(1).equals("0") && !decisionEnabled)) {
                            gui.NonDeterministicField.setText(state_to_queue.substring(1));
                        } else
                            gui.NonDeterministicField.setText(decisionString);
                        highlight_decision(decisionNumber - 1);
                        if (gui.step_used)
                            gui.paused = !gui.paused;
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
                if (decisionEnabled) {
                    if (decisionNumber != decisionString.length())
                        chill();
                } else
                    chill();
            }
        }
    }

    private State create_dialog(List<State> states_to_use) {
        State pickedState = null;
        if (states_to_use.size() != 1) {
            Object[] options = new Object[states_to_use.size()];
            for (int i = 0; i < states_to_use.size(); i++) {
                options[i] = states_to_use.get(i).toString();
            }
            int action = JOptionPane.showOptionDialog(gui.panelInput, "Pick a state.", "State picker", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (action != -1)
                pickedState = states_to_use.get(action);
                //state_picked = states_to_use.get(action);
            else {
                //state_picked = -1;
                gui.choose_steps.setSelected(false);
            }
        }
        //else
        //state_picked = -1;
        return pickedState;
    }

    private void highlight_decision(int char_num) throws BadLocationException {
        if (char_num >= 0) {
            gui.highlighter_decisions.removeAllHighlights();
            gui.highlighter_decisions.addHighlight(char_num, char_num + 1, painter);
        }
    }

    private void chill() throws InterruptedException {
        while (gui.paused) {
            Thread.sleep(50);
        }
        if (!gui.run_faster.isSelected()) {
            Thread.sleep(100);
        }
    }

    public void halt(Execution execution, State halter) throws BadLocationException {
        int l = 0;
        if (halter != null) {
            try {
                chill();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //execution.setState(halter);
            execution.execute(halter);
            //gui.paneLog.setText(gui.paneLog.getText() + execution.getTapes().get(0).getState().toString() + "\n");


            switch (halter.getNextState()) {
                case "halt":
                    gui.paneTapesOutput.setText("");
                    gui.counterField.setText(String.valueOf(numOfSteps));
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
                    }
                    gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "HALTED");
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
                        l += execution.getTapes().get(j).getContent().size() + 1;
                    }
                    break;
                case "halt-accept":
                    gui.paneTapesOutput.setText("");
                    gui.counterField.setText(String.valueOf(numOfSteps));
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
                    }
                    gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "ACCEPTED");
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
                        l += execution.getTapes().get(j).getContent().size() + 1;
                    }
                    break;
                case "halt-reject":
                    gui.paneTapesOutput.setText("");
                    gui.counterField.setText(String.valueOf(numOfSteps));
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
                    }
                    gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "REJECTED");
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
                        l += execution.getTapes().get(j).getContent().size() + 1;
                    }
                    break;
            }
        } else {
            gui.paneTapesOutput.setText("");
            gui.counterField.setText(String.valueOf(numOfSteps));
            for (int j = 0; j < execution.getTapes().size(); j++) {
                gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
            }
            gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "ABORTED\nNO STATE TO FOLLOW");
            for (int j = 0; j < execution.getTapes().size(); j++) {
                gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
                l += execution.getTapes().get(j).getContent().size() + 1;
            }
        }
    }

    private void draw(Execution execution) throws BadLocationException {
        int l = 0;
        gui.counterField.setText(String.valueOf(numOfSteps));
        gui.paneTapesOutput.setText("");
        for (int j = 0; j < execution.getTapes().size(); j++) {
            gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
        }
        for (int j = 0; j < execution.getTapes().size(); j++) {
            gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, painter);
            l += execution.getTapes().get(j).getContent().size() + 1;
        }
    }

    private Object deepClone(Object object) {
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

    private String _arrayToString(List<Character> array) {
        String str = array.stream().map(Object::toString).collect(Collectors.joining());
        return str;
    }
    //Change to check from a list of states
    private boolean check_coherence() {
        if (states.size() == 0) {
            return false;
        }
        for (State state : states) {
            if (state.getRead().length() != numOfTapes || state.getWrite().length() != numOfTapes || state.getMove().length() != numOfTapes) {
                gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "\n" + state.toString() + " does not have the right number of read/write/move.");
                return false;
            }
            if (!state.getMove().toLowerCase().matches("[s|*|r|l]+")) {
                gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "\n" + state.toString() + " has wrong movement.");
                return false;
            }
        }
        return true;
    }
}