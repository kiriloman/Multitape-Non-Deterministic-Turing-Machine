package ist.turingmachine;

//TODO: chars and not strings, packages
public class Main {
    public static void main(String[] args) {
        GUI gui = new GUI();
        TuringMachine turingMachine = new TuringMachine(gui);
        turingMachine.getGui().Prepare();
        turingMachine.getGui().Props();
        //maybe .run()
    }
}
