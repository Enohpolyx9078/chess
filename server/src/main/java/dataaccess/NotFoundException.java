package dataaccess;

public class NotFoundException extends DataAccessException {
    public NotFoundException(String message) {
        super(message);
    }
    public NotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}
