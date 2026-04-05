package com.openlens.auth.service;

import com.openlens.auth.domain.UserEntity;
import com.openlens.auth.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    public String register(String email, String password, String name) {
        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("email already in use");
        }

        String hash = passwordEncoder.encode(password);
        UserEntity user = userRepository.save(new UserEntity(normalizedEmail, hash, name.trim()));
        log.info("new user registered — id={} email={}", user.getId(), normalizedEmail);
        return jwtService.generate(user.getId(), user.getEmail());
    }

    public String login(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();

        UserEntity user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("failed login attempt for email={}", normalizedEmail);
            throw new IllegalArgumentException("invalid email or password");
        }

        log.info("user logged in — id={} email={}", user.getId(), normalizedEmail);
        return jwtService.generate(user.getId(), user.getEmail());
    }

    public Map<String, Object> getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "createdAt", user.getCreatedAt().toString()
        );
    }
}
