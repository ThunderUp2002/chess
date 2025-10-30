package exceptions;

public class BadRequestException extends Exception {
    private final int statusCode = 400;

    public BadRequestException(String message) {
        super(message);
    }

    private int getStatusCode() {
        return statusCode;
    }
}
