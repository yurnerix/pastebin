package by.yurnerix.pastebin.service;

import by.yurnerix.pastebin.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService
{

    private static final int CREATE_PASTE_LIMIT = 10;
    private static final Duration WINDOW = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    public void checkCreatePasteLimit(String key)
    {
        String redisKey = "rate_limit:create_paste:" + key;

        System.out.println("REDIS RATE LIMIT METHOD CALLED");
        System.out.println("REDIS KEY = " + redisKey);

        Long count = redisTemplate.opsForValue().increment(redisKey);

        System.out.println("REDIS COUNT = " + count);

        if (count != null && count == 1)
        {
            redisTemplate.expire(redisKey, WINDOW);
            System.out.println("REDIS EXPIRE SET");
        }

        if (count != null && count > CREATE_PASTE_LIMIT)
        {
            throw new RateLimitExceededException("Слишком много paste. Попробуйте позже.");
        }
    }
}