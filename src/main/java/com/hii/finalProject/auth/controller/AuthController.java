package com.hii.finalProject.auth.controller;


import com.hii.finalProject.auth.dto.LoginRequestDTO;
import com.hii.finalProject.auth.dto.LoginResponseDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            // Check if the user exists
            Optional<User> user = userRepository.findByEmail(userLogin.getEmail());
            if (user.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Email Not Found");
                errorResponse.put("message", "No account found with this email address");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
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
            // Custom response for bad credentials
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid Credentials");
            errorResponse.put("message", "The provided email or password is incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            // Handle other exceptions
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication Failed");
            errorResponse.put("message", "An error occurred during authentication");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
