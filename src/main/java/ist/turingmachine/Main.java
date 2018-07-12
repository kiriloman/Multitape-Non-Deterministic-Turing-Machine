package ist.turingmachine;

//TODO: chars and not strings, packages
//TODO: dont extend GUI
public class Main {
    public static void main(String[] args) {
        new Thread(new TuringMachine()).run();
    }
}
