package by.yurnerix.pastebin.controller;

import by.yurnerix.pastebin.dto.request.CreatePasteRequest;
import by.yurnerix.pastebin.dto.response.PasteResponse;
import by.yurnerix.pastebin.service.PasteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pastes")
@RequiredArgsConstructor
public class PasteRestController {

    private final PasteService pasteService;

    @PostMapping
    public ResponseEntity<PasteResponse> createPaste(
            @Valid @RequestBody CreatePasteRequest request,
            Principal principal,
            HttpServletRequest httpRequest
    ) {
        String email = principal != null ? principal.getName() : null;

        String rateLimitKey = email != null
                ? "user:" + email
                : "ip:" + httpRequest.getRemoteAddr();

        PasteResponse response = pasteService.createPaste(request, email, rateLimitKey);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PasteResponse>> getMyPastes(Principal principal)
    {
        List<PasteResponse> response = pasteService.getMyPastes(principal.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<PasteResponse> getPasteByHash(
            @PathVariable String hash,
            Principal principal
    ) {
        String email = principal != null ? principal.getName() : null;

        PasteResponse response = pasteService.getPasteByHash(hash, email);

        return ResponseEntity.ok(response);
    }
}