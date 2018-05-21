package ist.turingmachine;

import java.util.List;

// Check whats up with id. implement equals hashcode maybe
public class Tape {
    private Integer id;
    private List<String> content;
    private Integer head;
    private String state;

    public Tape(Integer id) {
        this.id = id;
        this.head = 0;
    }

    public void setHead(Integer head) {
        this.head = head;
    }

    public Integer getHead() {
        return head;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public List<String> getContent() {
        return content;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
