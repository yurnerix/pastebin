package by.yurnerix.pastebin.service;

import by.yurnerix.pastebin.dto.request.LoginRequest;
import by.yurnerix.pastebin.dto.request.RegisterRequest;
import by.yurnerix.pastebin.dto.response.AuthResponse;
import by.yurnerix.pastebin.entity.AppUser;
import by.yurnerix.pastebin.entity.Role;
import by.yurnerix.pastebin.exception.UserAlreadyExistsException;
import by.yurnerix.pastebin.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request)
    {
        if (appUserRepository.existsByEmail(request.email()))
        {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        if (appUserRepository.existsByUsername(request.username()))
        {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();

        appUserRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse login(LoginRequest request)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String token = jwtService.generateToken(request.email());

        return new AuthResponse(token, "Bearer");
    }
}