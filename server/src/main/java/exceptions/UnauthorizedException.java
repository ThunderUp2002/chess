package exceptions;

public class UnauthorizedException extends Exception {
    private final int statusCode = 401;

    public UnauthorizedException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
