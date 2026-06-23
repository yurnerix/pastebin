package by.yurnerix.pastebin.exception;

public class PasteNotFoundException extends RuntimeException {

    public PasteNotFoundException(String message)
    {
        super(message);
    }
}
