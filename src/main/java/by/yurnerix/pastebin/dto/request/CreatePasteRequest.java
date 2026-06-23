package by.yurnerix.pastebin.dto.request;

import by.yurnerix.pastebin.dto.type.ExpirationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePasteRequest(

        @Size(max = 100, message = "Title must be less than 100 characters")
        String title,

        @NotBlank(message = "Content is required")
        @Size(max = 50_000, message = "Content must be less than 50000 characters")
        String content,

        @NotNull(message = "Expiration type is required")
        ExpirationType expirationTime,

        Boolean isPrivate
) {
}