package by.yurnerix.pastebin.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "by.yurnerix.pastebin.controller.web")
public class WebExceptionHandler
{

    @ExceptionHandler(PasteNotFoundException.class)
    public String handlePasteNotFound(PasteNotFoundException exception, Model model)
    {
        model.addAttribute("status", 404);
        model.addAttribute("title", "Paste не найден");
        model.addAttribute("message", exception.getMessage());
        return "error/paste-error";
    }

    @ExceptionHandler(PasteExpiredException.class)
    public String handlePasteExpired(PasteExpiredException exception, Model model)
    {
        model.addAttribute("status", 410);
        model.addAttribute("title", "Paste истёк");
        model.addAttribute("message", exception.getMessage());
        return "error/paste-error";
    }

    @ExceptionHandler(PasteAccessDeniedException.class)
    public String handlePasteAccessDenied(PasteAccessDeniedException exception, Model model)
    {
        model.addAttribute("status", 403);
        model.addAttribute("title", "Нет доступа");
        model.addAttribute("message", exception.getMessage());

        return "error/paste-error";
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public String handleRateLimitExceeded(RateLimitExceededException exception, Model model)
    {
        model.addAttribute("status", 429);
        model.addAttribute("title", "Слишком много запросов");
        model.addAttribute("message", exception.getMessage());

        return "error/paste-error";
    }
}