package by.yurnerix.pastebin.service;

import by.yurnerix.pastebin.dto.request.CreatePasteRequest;
import by.yurnerix.pastebin.dto.response.PasteResponse;
import by.yurnerix.pastebin.entity.AppUser;
import by.yurnerix.pastebin.entity.Paste;
import by.yurnerix.pastebin.exception.PasteAccessDeniedException;
import by.yurnerix.pastebin.exception.PasteExpiredException;
import by.yurnerix.pastebin.exception.PasteNotFoundException;
import by.yurnerix.pastebin.repository.AppUserRepository;
import by.yurnerix.pastebin.repository.PasteRepository;
import by.yurnerix.pastebin.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasteService {

    private static final int MAX_HASH_GENERATION_ATTEMPTS = 10;

    private final PasteRepository pasteRepository;
    private final AppUserRepository appUserRepository;
    private final HashGenerator hashGenerator;

    private final RateLimitService rateLimitService;

    @Transactional
    public PasteResponse createPaste(
            CreatePasteRequest request,
            String userEmail,
            String rateLimitKey
    ) {
        rateLimitService.checkCreatePasteLimit(rateLimitKey);

        String hash = generateUniqueHash();

        AppUser user = null;

        if (userEmail != null) {
            user = appUserRepository.findByEmail(userEmail)
                    .orElse(null);
        }

        Paste paste = Paste.builder()
                .hash(hash)
                .title(request.title())
                .content(request.content())
                .isPrivate(Boolean.TRUE.equals(request.isPrivate()))
                .expiresAt(request.expirationTime().calculateExpiresAt())
                .user(user)
                .build();

        Paste savedPaste = pasteRepository.save(paste);

        return toResponse(savedPaste);
    }

    @Transactional
    public PasteResponse getPasteByHash(String hash, String userEmail)
    {
        Paste paste = pasteRepository.findByHash(hash)
                .orElseThrow(() -> new PasteNotFoundException("Paste not found"));

        if (paste.isExpired())
        {
            throw new PasteExpiredException("Paste has expired");
        }

        if (Boolean.TRUE.equals(paste.getIsPrivate()))
        {
            if (userEmail == null || paste.getUser() == null || !paste.getUser().getEmail().equals(userEmail))
            {
                throw new PasteAccessDeniedException("You do not have access to this paste");
            }
        }

        paste.setViewsCount(paste.getViewsCount() + 1);

        return toResponse(paste);
    }

    @Transactional(readOnly = true)
    public List<PasteResponse> getMyPastes(String userEmail)
    {
        AppUser user = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new PasteAccessDeniedException("User not found"));

        return pasteRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deletePasteByHash(String hash, String userEmail)
    {
        Paste paste = pasteRepository.findByHash(hash)
                .orElseThrow(() -> new PasteNotFoundException("Paste не найден"));

        if (paste.getUser() == null || !paste.getUser().getEmail().equals(userEmail))
        {
            throw new PasteAccessDeniedException("Вы не можете удалить этот paste");
        }

        pasteRepository.delete(paste);
    }

    @Transactional
    public void deleteMyExpiredPastes(String userEmail)
    {
        AppUser user = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new PasteAccessDeniedException("User not found"));

        List<Paste> expiredPastes = pasteRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(Paste::isExpired)
                .toList();

        pasteRepository.deleteAll(expiredPastes);
    }

    private String generateUniqueHash()
    {
        for (int i = 0; i < MAX_HASH_GENERATION_ATTEMPTS; i++)
        {
            String hash = hashGenerator.generate();

            if (!pasteRepository.existsByHash(hash))
            {
                return hash;
            }
        }

        throw new IllegalStateException("Could not generate unique hash");
    }

    private PasteResponse toResponse(Paste paste)
    {
        return new PasteResponse(
                paste.getHash(),
                paste.getTitle(),
                paste.getContent(),
                paste.getIsPrivate(),
                paste.getViewsCount(),
                paste.getCreatedAt(),
                paste.getExpiresAt(),
                paste.isExpired()
        );
    }
}