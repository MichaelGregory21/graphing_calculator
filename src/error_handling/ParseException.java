package error_handling;

public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }

}
