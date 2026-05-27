package com.openlens.auth.controller;

import com.openlens.auth.dto.AuthResponse;
import com.openlens.auth.dto.LoginRequest;
import com.openlens.auth.dto.RegisterRequest;
import com.openlens.auth.dto.UserProfileResponse;
import com.openlens.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final String cookieName;
    private final int cookieMaxAge;

    public AuthController(AuthService authService,
                          @Value("${auth.cookie.name}") String cookieName,
                          @Value("${auth.cookie.max-age-days}") int cookieMaxAgeDays) {
        this.authService = authService;
        this.cookieName = cookieName;
        this.cookieMaxAge = cookieMaxAgeDays * 24 * 60 * 60;
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
                                               HttpServletResponse response) {
        String token = authService.login(request.email(), request.password());
        setTokenCookie(response, token);
        return ResponseEntity.ok(new AuthResponse("logged in successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
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
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
}
