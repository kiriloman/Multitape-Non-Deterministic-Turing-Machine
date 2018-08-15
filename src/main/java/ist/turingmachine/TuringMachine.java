package ist.turingmachine;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

//TODO: ver os execution clones e ver forma diferente de te-los
public class TuringMachine implements Runnable {
    private List<Tape> tapes;
    private List<State> states;
    private int numOfSteps, numOfTapes, decisionNumber;
    private boolean decisionEnabled;
    private String decisionString;

    private GUI gui;
    private Parser parser;

    public GUI getGui() {
        return gui;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    TuringMachine(GUI gui) {
        tapes = new ArrayList<>();
        states = new ArrayList<>();
        numOfSteps = 0;
        numOfTapes = 0;
        decisionNumber = 0;
        decisionString = "";
        this.gui = gui;
        parser = new Parser();
    }

    private void outputTapes() {
        gui.paneTapesOutput.setText(gui.paneInput.getText()); // newline?
    }

    private void resetGUI() {
        gui.paneLog.setFocusable(true);
        gui.NonDeterministicField.setFocusable(true);
        gui.step.setEnabled(true);
        gui.run_used = false;
        gui.stepped = false;
        gui.reset_used = false;
        gui.paneLog.setCaretPosition(gui.paneLog.getDocument().getLength());
        gui.choose_steps.setEnabled(true);
    }

    private void adjustNumOfTapes() {
        if (states.get(0).getRead().length() > tapes.size()) {
            int extraTapes = states.get(0).getRead().length() - tapes.size();
            Tape tape;
            List<Character> content = new ArrayList<>();
            content.add('_');
            for (int i = 0; i < extraTapes; i++) {
                tape = new Tape(numOfTapes++, content);
                tapes.add(tape);
            }
        }
    }

    @Override
    public void run() {
        gui.paneLog.setText("");
        gui.paneTapesOutput.setText("");

        tapes = parser.parseTapes(gui.paneInput.getText());
        numOfTapes = tapes.size();
        outputTapes();

        states = parser.parseStates(gui.paneCode.getText());


        // Ajusta, se for necessario, o numero de tapes.
        if (states.size() != 0) {
            System.out.println(tapes.toString());
            adjustNumOfTapes();
            System.out.println(tapes.toString());
        } else {
            gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "\n" + "You forgot the program.");
        }

        // Isto aqui remove a ultima newline

        if (!gui.paneTapesOutput.getText().equals("")) {
            try {
                String content = gui.paneTapesOutput.getDocument().getText(0, gui.paneTapesOutput.getDocument().getLength());
                int line = content.lastIndexOf("\n");
                gui.paneTapesOutput.getDocument().remove(line, gui.paneTapesOutput.getDocument().getLength() - line);
            } catch (BadLocationException e1) {
                System.out.println("Failed to remove last line.");
            }
        }

        //review this code
        if (check_coherence()) {
            System.out.println("checked");
            if (!gui.NonDeterministicField.getText().equals("") && !gui.NonDeterministicField.getText().equals("Decision Sequence")) {
                decisionEnabled = true;
                decisionString = gui.NonDeterministicField.getText();
            } else {
                gui.NonDeterministicField.setText("");
                decisionEnabled = false;
            }
            gui.NonDeterministicField.setHorizontalAlignment(SwingConstants.LEFT);
            gui.NonDeterministicField.setOpaque(true);


            Execution execution = new Execution(tapes, states);
            execution.setCurrentStateName(states.get(0).getName());
            try {
                System.out.println(states.toString() + " " + tapes.toString());
                BFS(execution);
            } catch (InterruptedException | BadLocationException e1) {
                System.out.println("Execution went wrong. Line 110.");
            }
        }

        resetGUI();
    }

    private void BFS(Execution execution) throws BadLocationException, InterruptedException {
        draw(execution);
        String lastState = execution.getCurrentStateName();
        String stateToQueue = "";
        boolean no_chill;
        int k = 0, l = 0;
        gui.paused = true;
        List<State> statesToUse;
        //ArrayList<String> marked_states = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        ArrayList<Execution> execution_clones = new ArrayList<>();
        int clone_number;
        //marked_states.add("0");
        queue.add("0");
        execution_clones.add(execution);
        chill();
        while (queue.size() != 0) {
            if (execution_clones.get(0).findExecutableStates().size() > 0) {
                if (k == 1) {
                    k = 0;
                    no_chill = true;
                } else {
                    no_chill = false;
                    draw(execution_clones.get(0));
                    if (queue.peek().substring(1).length() > decisionString.length() || (queue.peek().substring(1).length() <= decisionString.length() && !queue.peek().substring(1).equals(decisionString) && !decisionEnabled)) {
                        gui.NonDeterministicField.setText(queue.peek().substring(1));
                    } else
                        gui.NonDeterministicField.setText(decisionString);
                }
            } else {
                k = 1;
                no_chill = true;
            }
            //no inicio é 1. a propria execution
            clone_number = execution_clones.size();

            if (decisionEnabled) {
                if (decisionNumber > decisionString.length() - 1) {
                    gui.paused = true;
                    gui.choose_steps.setEnabled(true);
                    gui.NonDeterministicField.setFocusable(true);
                    gui.step.setEnabled(true);
                    if (lastState.equals("halt-accept") || lastState.equals("halt-reject") || lastState.equals("halt")) {
                        halt(execution_clones.get(execution_clones.size() - 1), lastState);
                        return;
                    }
                    highlight_decision(decisionNumber - 1);
                    chill();
                    if (gui.NonDeterministicField.getText().length() > decisionString.length()) {
                        decisionString = gui.NonDeterministicField.getText();
                        if (Character.getNumericValue(decisionString.charAt(decisionNumber)) <= execution_clones.get(0).findExecutableStates().size()) {
                            statesToUse = new ArrayList<>();
                            statesToUse.add(execution_clones.get(0).findExecutableStates().get(Character.getNumericValue(decisionString.charAt(decisionNumber)) - 1));
                            decisionNumber++;
                        } else {
                            halt(execution_clones.get(execution_clones.size() - 1), lastState);
                            highlight_decision(decisionNumber);
                            return;
                        }
                    } else {
                        decisionEnabled = false;
                        statesToUse = execution_clones.get(0).findExecutableStates();
                    }
                } else {
                    highlight_decision(decisionNumber - 1);
                    if (lastState.equals("halt-accept") || lastState.equals("halt-reject") || lastState.equals("halt")) {
                        halt(execution_clones.get(execution_clones.size() - 1), lastState);
                        return;
                    }
                    if (Character.getNumericValue(decisionString.charAt(decisionNumber)) <= execution_clones.get(0).findExecutableStates().size()) {
                        statesToUse = new ArrayList<>();
                        statesToUse.add(execution_clones.get(0).findExecutableStates().get(Character.getNumericValue(decisionString.charAt(decisionNumber)) - 1));
                        decisionNumber++;
                    } else {
                        halt(execution_clones.get(0), null);
                        return;
                    }
                }
            } else {
                highlight_decision(decisionNumber - 1);
                statesToUse = execution_clones.get(0).findExecutableStates();
            }

            if (gui.step_used && (execution_clones.size() != 1 || no_chill)) {
                gui.paused = !gui.paused;
            }

            State statePicked;
            System.out.println(statesToUse.toString() + " states to use");

            for (int i = 0; i < statesToUse.size(); i++) {
                System.out.println("State number " + i);
                if (gui.choose_steps.isSelected()) {
                    //create_dialog(statesToUse);
                    statePicked = create_dialog(statesToUse);
                    //if (state_picked != -1) {
                    if (statePicked != null) {
                        //l = statesToUse.indexOf(state_picked);
                        l = execution_clones.get(0).getStates().indexOf(statePicked);
                        statesToUse = new ArrayList<>();
                        //statesToUse.add(state_picked);
                        statesToUse.add(statePicked);
                        gui.paused = false;
                    } else
                        l = i;
                } else
                    l = i;


                if (lastState != null && (lastState.equals("halt") || lastState.equals("halt-accept"))) {
                    halt(execution_clones.get(execution_clones.size() - 1), lastState);
                    return;
                }
                if (statesToUse.size() <= 1 || i != 0) {
                    chill();
                } else {
                    if (gui.step_used)
                        gui.paused = !gui.paused;
                }
                if (statesToUse.size() > 1) {
                    if (queue.peek().substring(1).length() > decisionString.length() || (queue.peek().substring(1).length() <= decisionString.length() && !queue.peek().substring(1).equals(decisionString)) && !decisionEnabled) {
                        gui.NonDeterministicField.setText(queue.peek().substring(1));
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
                    stateToQueue = queue.peek() + String.valueOf(decisionString.charAt(decisionNumber - 1));
                } else {
                    if (gui.choose_steps.isSelected())
                        stateToQueue = queue.peek() + (l + 1);
                    else
                        stateToQueue = queue.peek() + (i + 1);
                }


                /*if (!marked_states.contains(stateToQueue)) {
                    //change log
                    gui.paneLog.setText(gui.paneLog.getText() + execution_clones.get(i + clone_number).getCurrentStateName() + "\n");
                    execution_clones.get(i + clone_number).execute(statesToUse.get(i));
                    //execution_clones.get(i + clone_number).setState(statesToUse.get(i));
                    numOfSteps++;
                    draw(execution_clones.get(i + clone_number));
                    marked_states.add(stateToQueue);
                    queue.add(stateToQueue);

                    if (stateToQueue.substring(1).length() > decisionString.length() || (stateToQueue.substring(1).length() <= decisionString.length() && !stateToQueue.substring(1).equals(decisionString) && !stateToQueue.equals("0") && !decisionEnabled)) {
                        gui.NonDeterministicField.setText(stateToQueue.substring(1));
                    } else
                        gui.NonDeterministicField.setText(decisionString);
                    highlight_decision(decisionNumber - 1);
                    if (gui.step_used)
                        gui.paused = !gui.paused;
                } else {*/
                queue.offer(stateToQueue);
                    if (queue.contains(stateToQueue)) {
                        gui.paneLog.setText(gui.paneLog.getText() + execution_clones.get(i + clone_number).getCurrentStateName() + "\n");
                        execution_clones.get(i + clone_number).execute(statesToUse.get(i));
                        //execution_clones.get(i + clone_number).setState(statesToUse.get(i));
                        numOfSteps++;
                        draw(execution_clones.get(i + clone_number));
                        if (stateToQueue.substring(1).length() > decisionString.length() || (stateToQueue.substring(1).length() <= decisionString.length() && !stateToQueue.substring(1).equals(decisionString) && !stateToQueue.substring(1).equals("0") && !decisionEnabled)) {
                            gui.NonDeterministicField.setText(stateToQueue.substring(1));
                        } else
                            gui.NonDeterministicField.setText(decisionString);
                        highlight_decision(decisionNumber - 1);
                        if (gui.step_used)
                            gui.paused = !gui.paused;
                    }
                //}
                lastState = statesToUse.get(i).getNextState().toLowerCase();
            }

            if (statesToUse.size() == 0 && !lastState.equals("halt-reject") && !lastState.equals("halt") && !lastState.equals("halt-accept"))
                //lastState = "";
                //do nothing
                lastState = lastState;
            else {
                if (lastState.equals("halt") || lastState.equals("halt-accept")) {
                    halt(execution_clones.get(execution_clones.size() - 1), lastState);
                    return;
                }
                if (lastState.equals("halt-reject") && queue.size() == 2) {
                    halt(execution_clones.get(execution_clones.size() - 1), lastState);
                    return;
                }
            }
            if (queue.size() != 1) {
                queue.remove();
                execution_clones.remove(0);
            } else {
                halt(execution_clones.get(execution_clones.size() - 1), lastState);
                queue.remove();
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

    private State create_dialog(List<State> statesToUse) {
        State pickedState = null;
        if (statesToUse.size() != 1) {
            Object[] options = new Object[statesToUse.size()];
            for (int i = 0; i < statesToUse.size(); i++) {
                options[i] = statesToUse.get(i).toString();
            }
            int action = JOptionPane.showOptionDialog(gui.panelInput, "Pick a state.", "State picker", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (action != -1)
                pickedState = statesToUse.get(action);
                //state_picked = statesToUse.get(action);
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
            gui.highlighter_decisions.addHighlight(char_num, char_num + 1, gui.painter);
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

    public void halt(Execution execution, String halter) throws BadLocationException {
        int l = 0;
        //never is null?
        if (halter != null) {
            try {
                chill();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //execution.setState(halter);
            //execution.execute(halter);
            //gui.paneLog.setText(gui.paneLog.getText() + execution.getTapes().get(0).getState().toString() + "\n");


            switch (halter) {
                case "halt":
                    gui.paneTapesOutput.setText("");
                    gui.counterField.setText(String.valueOf(numOfSteps));
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + _arrayToString(execution.getTapes().get(j).getContent()) + "\n");
                    }
                    gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + "HALTED");
                    for (int j = 0; j < execution.getTapes().size(); j++) {
                        gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, gui.painter);
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
                        gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, gui.painter);
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
                        gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, gui.painter);
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
                gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, gui.painter);
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
            gui.highlighter.addHighlight(l + execution.getTapes().get(j).getHead(), l + execution.getTapes().get(j).getHead() + 1, gui.painter);
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