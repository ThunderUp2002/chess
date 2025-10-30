package exceptions;

public class GeneralException extends Exception {
    private final int statusCode = 500;

    public GeneralException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
