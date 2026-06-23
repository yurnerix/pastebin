package by.yurnerix.pastebin.controller.web;

import by.yurnerix.pastebin.dto.request.CreatePasteRequest;
import by.yurnerix.pastebin.dto.response.PasteResponse;
import by.yurnerix.pastebin.dto.type.ExpirationType;
import by.yurnerix.pastebin.service.PasteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PastePageController {

    private final PasteService pasteService;

    @GetMapping("/pastes/new")
    public String createPastePage(Model model) {
        model.addAttribute("expirationTypes", ExpirationType.values());
        return "paste/create";
    }

    @PostMapping("/pastes")
    public String createPaste(
            @RequestParam(required = false) String title,
            @RequestParam String content,
            @RequestParam ExpirationType expirationTime,
            @RequestParam(required = false, defaultValue = "false") Boolean isPrivate,
            Principal principal,
            HttpServletRequest httpRequest
    ) {
        String email = principal != null ? principal.getName() : null;

        String rateLimitKey = email != null
                ? "user:" + email
                : "ip:" + httpRequest.getRemoteAddr();

        CreatePasteRequest request = new CreatePasteRequest(
                title,
                content,
                expirationTime,
                isPrivate
        );

        PasteResponse response = pasteService.createPaste(request, email, rateLimitKey);

        return "redirect:/p/" + response.hash();
    }

    @GetMapping("/p/{hash}")
    public String viewPastePage(
            @PathVariable String hash,
            Model model,
            Principal principal
    ) {
        String email = principal != null ? principal.getName() : null;

        PasteResponse paste = pasteService.getPasteByHash(hash, email);
        model.addAttribute("paste", paste);

        return "paste/view";
    }

    @GetMapping("/me/pastes")
    public String myPastesPage(Model model, Principal principal) {
        List<PasteResponse> pastes = pasteService.getMyPastes(principal.getName());

        model.addAttribute("pastes", pastes);

        return "paste/my-pastes";
    }

    @PostMapping("/me/pastes/{hash}/delete")
    public String deletePaste(
            @PathVariable String hash,
            Principal principal
    ) {
        pasteService.deletePasteByHash(hash, principal.getName());

        return "redirect:/me/pastes";
    }

    @PostMapping("/me/pastes/delete-expired")
    public String deleteExpiredPastes(Principal principal) {
        pasteService.deleteMyExpiredPastes(principal.getName());

        return "redirect:/me/pastes";
    }
}