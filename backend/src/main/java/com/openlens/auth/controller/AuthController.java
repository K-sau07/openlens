package com.openlens.auth.controller;

import com.openlens.auth.dto.AuthResponse;
import com.openlens.auth.dto.LoginRequest;
import com.openlens.auth.dto.RegisterRequest;
import com.openlens.auth.dto.UserProfileResponse;
import com.openlens.auth.service.AuthService;
import com.openlens.auth.service.LoginRateLimiter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final LoginRateLimiter rateLimiter;

    public AuthController(AuthService authService, LoginRateLimiter rateLimiter) {
        this.authService = authService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletResponse response) {
        String token = authService.register(request.email(), request.password(), request.name());
        setTokenCookie(response, token);
        return ResponseEntity.ok(new AuthResponse("registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest,
                                               HttpServletResponse httpResponse) {
        String ip = getClientIp(httpRequest);

        if (!rateLimiter.isAllowed(ip)) {
            log.warn("login rate limit exceeded for ip={}", ip);
            return ResponseEntity.status(429).body(new AuthResponse("too many login attempts, try again in 15 minutes"));
        }

        String token = authService.login(request.email(), request.password());
        rateLimiter.reset(ip);
        setTokenCookie(httpResponse, token);
        return ResponseEntity.ok(new AuthResponse("logged in successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("ol_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(new AuthResponse("logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(authService.getProfile(userId));
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("ol_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
