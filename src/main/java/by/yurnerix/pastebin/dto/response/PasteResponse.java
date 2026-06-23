package by.yurnerix.pastebin.dto.response;

import java.time.LocalDateTime;

public record PasteResponse(
        String hash,
        String title,
        String content,
        Boolean isPrivate,
        Long viewsCount,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        Boolean expired
) {
}
