package by.yurnerix.pastebin.dto.type;

import java.time.LocalDateTime;

public enum ExpirationType {

    TEN_MINUTES,
    ONE_HOUR,
    ONE_DAY,
    ONE_WEEK,
    NEVER;

    public LocalDateTime calculateExpiresAt()
    {
        LocalDateTime now = LocalDateTime.now();

        return switch (this)
        {
            case TEN_MINUTES -> now.plusMinutes(10);
            case ONE_HOUR -> now.plusHours(1);
            case ONE_DAY -> now.plusDays(1);
            case ONE_WEEK -> now.plusWeeks(1);
            case NEVER -> null;
        };

    }
}
