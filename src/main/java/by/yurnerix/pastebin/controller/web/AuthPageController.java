package by.yurnerix.pastebin.controller.web;

import by.yurnerix.pastebin.dto.request.RegisterRequest;
import by.yurnerix.pastebin.exception.UserAlreadyExistsException;
import by.yurnerix.pastebin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthPageController {

    private final AuthService authService;

    @GetMapping("/register")
    public String registerPage()
    {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        try
        {
            RegisterRequest request = new RegisterRequest(username, email, password);
            authService.register(request);

            return "redirect:/login?registered";
        } catch (UserAlreadyExistsException exception)
        {
            model.addAttribute("error", exception.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String loginPage()
    {
        return "auth/login";
    }
}