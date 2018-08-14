package ist.turingmachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    public List<State> parseStates(String input) {
        List<State> states = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(input));
        String line;
        //Lê o programa e cria states
        try {
            line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (!line.equals("") && !line.startsWith(";")) {
                    states.add(new State(line));
                }
                line = reader.readLine();
            }
        } catch (IOException e1) {
            System.out.println("Failed to create states.");
        }
        return states;
    }

    public List<Tape> parseTapes(String input) {
        List<Tape> tapes = new ArrayList<>();
        int tapeNumber = 0;
        String line;
        BufferedReader reader = new BufferedReader(new StringReader(input));
        //Lê e cria tapes
        try {
            line = reader.readLine();
            if (line != null && !line.equals("Initial tapes here, one in each line")) {
               // gui.paneTapesOutput.setText(gui.paneTapesOutput.getText() + line + "\n"); //maybe no \n
                while (line != null) {
                    tapes.add(createTape(line, tapeNumber++));
                    line = reader.readLine();
                }
            }
        } catch (IOException e1) {
            System.out.println("Failed to create tapes.");
        }
        return tapes;
    }

    private Tape createTape(String content, Integer tapeNumber) {
        Tape tape = new Tape(tapeNumber, content.chars().mapToObj(e -> (char) e).collect(Collectors.toList()));
        return tape;
    }
}
