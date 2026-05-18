package shared.exceptions;

public class InvalidSearchParameterException extends DomainException {

    public InvalidSearchParameterException(String message) {
        super(message);
    }

    public InvalidSearchParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}