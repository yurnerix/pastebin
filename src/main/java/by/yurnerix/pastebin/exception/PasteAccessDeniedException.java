package by.yurnerix.pastebin.exception;

public class PasteAccessDeniedException extends RuntimeException {

    public PasteAccessDeniedException(String message)
    {
        super(message);
    }
}