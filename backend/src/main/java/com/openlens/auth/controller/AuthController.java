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
    private final String cookieSameSite;
    private final boolean cookieSecure;

    public AuthController(AuthService authService,
                          @Value("${auth.cookie.name}") String cookieName,
                          @Value("${auth.cookie.max-age-days}") int cookieMaxAgeDays,
                          @Value("${auth.cookie.same-site:Lax}") String cookieSameSite,
                          @Value("${auth.cookie.secure:false}") boolean cookieSecure) {
        this.authService = authService;
        this.cookieName = cookieName;
        this.cookieMaxAge = cookieMaxAgeDays * 24 * 60 * 60;
        this.cookieSameSite = cookieSameSite;
        this.cookieSecure = cookieSecure;
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
        // Attributes must match the cookie that was set, or the browser keeps it.
        response.addCookie(buildCookie("", 0));
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

    /**
     * The UI and the API are served from different sites in production (Vercel and
     * Render), so the session cookie is cross-site. SameSite=Lax is not sent on a
     * cross-site fetch, which makes login appear to succeed and every later request
     * arrive unauthenticated. Production therefore needs SameSite=None, which
     * browsers only accept together with Secure. Both stay configurable so local
     * development over plain HTTP keeps working with Lax.
     */
    private void setTokenCookie(HttpServletResponse response, String token) {
        response.addCookie(buildCookie(token, cookieMaxAge));
    }

    private Cookie buildCookie(String value, int maxAge) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setSecure(cookieSecure);
        cookie.setAttribute("SameSite", cookieSameSite);
        return cookie;
    }
}
