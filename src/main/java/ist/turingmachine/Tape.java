package ist.turingmachine;

import java.io.Serializable;
import java.util.List;

//REMOVE SERIALIZABLE
public class Tape implements Serializable {
    private Integer id;
    private List<Character> content;
    private Integer head;

    public Tape(Integer id, List<Character> content) {
        this.id = id;
        this.content = content;
        head = 0;
    }

    public void setHead(Integer head) {
        this.head = head;
    }

    public Integer getHead() {
        return head;
    }

    public void setContent(List<Character> content) {
        this.content = content;
    }

    public List<Character> getContent() {
        return content;
    }

    public char getHeadContent() {
        return content.get(head);
    }

    @Override
    public int hashCode() {
        return 31 + id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Tape)) return false;
        Tape other = (Tape) o;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "[ID: " + id + ", Head: " + head + ", Content: " + content + "]";
    }
}