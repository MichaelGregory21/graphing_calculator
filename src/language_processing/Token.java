package language_processing;

public record Token(String type, String text, String subscript) {

    public String pretty() {
        return "(TYPE: " + type + ", TEXT: " + text + ", SUBSCRIPT: " + subscript + ")";
    }

}
