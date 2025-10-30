package exceptions;

public class AlreadyTakenException extends Exception {
    private final int statusCode = 403;

    public AlreadyTakenException(String message) {
        super(message);
    }

    private int getStatusCode() {
        return statusCode;
    }
}
