package by.yurnerix.pastebin.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class HashGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int HASH_LENGTH = 8;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate()
    {
        StringBuilder hash = new StringBuilder(HASH_LENGTH);

        for(int i = 0; i < HASH_LENGTH; i++)
        {
            int index = secureRandom.nextInt(ALPHABET.length());
            hash.append(ALPHABET.charAt(index));
        }

        return hash.toString();
    }

}
