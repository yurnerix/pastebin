package by.yurnerix.pastebin.repository;

import by.yurnerix.pastebin.entity.AppUser;
import by.yurnerix.pastebin.entity.Paste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PasteRepository extends JpaRepository<Paste, Long>
{
    Optional<Paste> findByHash(String hash);
    boolean existsByHash(String hash);
    List<Paste> findAllByUserOrderByCreatedAtDesc(AppUser user);
    void deleteAllByExpiresAtBefore(LocalDateTime now);
}