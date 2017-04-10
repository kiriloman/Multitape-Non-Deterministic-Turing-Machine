package ist.turingmachine;

import java.io.Serializable;
import java.util.ArrayList;

public class Tape implements Serializable {
    public Integer number;
    public ArrayList<String> content;
    public Integer head = 0;
    public String state;

    public Tape(Integer number) {
        this.number = number;
    }
    public void setHead(Integer head) {
        this.head = head;
    }
    public Integer getHead() {
        return head;
    }
    public void setContent(ArrayList<String> content) {
        this.content = content;
    }
    public ArrayList<String> getContent() {
        return content;
    }
    public void setState (String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }
}
