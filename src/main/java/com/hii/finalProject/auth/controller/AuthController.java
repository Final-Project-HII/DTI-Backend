package com.hii.finalProject.auth.controller;


import com.hii.finalProject.auth.dto.LoginRequestDTO;
import com.hii.finalProject.auth.dto.LoginResponseDTO;
import com.hii.finalProject.auth.dto.LoginSocialRequestDTO;
import com.hii.finalProject.auth.dto.LoginSocialResponseDTO;
import com.hii.finalProject.auth.entity.UserAuth;
import com.hii.finalProject.auth.service.AuthService;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Log
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO userLogin) {
        log.info("User login request received for user: " + userLogin.getEmail());
        try {
            Optional<User> userOptional = userRepository.findByEmail(userLogin.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Email Not Found", "message", "No account found with this email address"));
            }

            User user = userOptional.get();
            if (!user.getIsVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email Not Verified", "message", "Your email address has not been verified"));
            }

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            userLogin.getEmail(),
                            userLogin.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserAuth userDetails = (UserAuth) authentication.getPrincipal();
            log.info("Token requested for user :" + userDetails.getUsername() + " with roles: " + userDetails.getAuthorities().toArray()[0]);
            LoginResponseDTO resp = authService.generateToken(authentication);
            Cookie cookie = new Cookie("Sid", resp.getAccessToken());
            cookie.setMaxAge(3600);
            cookie.setPath("/");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resp);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid Credentials", "message", "The provided password is incorrect"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Authentication Failed", "message", "An error occurred during authentication"));
        }
    }


    @PostMapping("/login-social")
    public ResponseEntity<?> loginSocial(@RequestBody LoginSocialRequestDTO userLogin) {
        log.info("User login request received for user: " + userLogin.getEmail());
        try {
            Optional<User> userOptional = userRepository.findByEmail(userLogin.getEmail());
            if (userOptional.isEmpty() || userOptional.get().getPassword() != null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Email Not Found", "message", "No account found with this email address"));
            }

            User user = userOptional.get();
            if (!user.getIsVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email Not Verified", "message", "Your email address has not been verified"));
            }
            log.info("Token requested for user :" + userOptional.get().getName() + " with roles: " + userOptional.get().getRole());
            LoginSocialResponseDTO resp = authService.generateSocialToken(userLogin);
            Cookie cookie = new Cookie("Sid", resp.getAccessToken());
            cookie.setMaxAge(3600);
            cookie.setPath("/");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Authentication Failed", "message", "An error occurred during authentication"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid Authorization header"));
    }
}
