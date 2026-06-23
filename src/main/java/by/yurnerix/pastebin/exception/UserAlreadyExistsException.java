package by.yurnerix.pastebin.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message)
    {
        super(message);
    }
}
