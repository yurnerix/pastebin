package by.yurnerix.pastebin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pastes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paste
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String hash;

    @Column(length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "views_count", nullable = false)
    private Long viewsCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersist()
    {
        if (isPrivate == null)
        {
            isPrivate = false;
        }

        if (viewsCount == null)
        {
            viewsCount = 0L;
        }

        createdAt = LocalDateTime.now();
    }

    public boolean isExpired()
    {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}