package com.hii.finalProject.auth.service.impl;


import com.hii.finalProject.auth.dto.LoginResponseDTO;
import com.hii.finalProject.auth.dto.LoginSocialRequestDTO;
import com.hii.finalProject.auth.dto.LoginSocialResponseDTO;
import com.hii.finalProject.auth.repository.AuthRedisRepository;
import com.hii.finalProject.auth.service.AuthService;
import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.users.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    private final AuthRedisRepository authRedisRepository;
    private final UserService userService;
    private final JwtDecoder jwtDecoder;

    public AuthServiceImpl(JwtEncoder jwtEncoder, UserRepository userRepository, AuthRedisRepository authRedisRepository, UserService userService, @Qualifier("jwtDecoder") JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.authRedisRepository = authRedisRepository;
        this.userService = userService;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public LoginResponseDTO generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var existingKey = authRedisRepository.getJwtKey(authentication.getName());
        LoginResponseDTO response = new LoginResponseDTO();
        response.setEmail(authentication.getName());
        response.setRole(scope);
        if(existingKey != null){
            log.info("Token already exists for user: " + authentication.getName());
            response.setAccessToken(existingKey);
            return response;
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        if(authRedisRepository.isKeyBlacklisted(jwt)){
            throw new DataNotFoundException("JWT Token has already been blacklisted");
        }
        authRedisRepository.saveJwtKey(authentication.getName(),jwt);
        response.setAccessToken(jwt);
        return response;
    }

    @Override
    public LoginSocialResponseDTO generateSocialToken(LoginSocialRequestDTO data) {
        Instant now = Instant.now();
        Optional<User> user = userRepository.findByEmail(data.getEmail());
        var existingKey = authRedisRepository.getJwtKey(data.getEmail());
        LoginSocialResponseDTO response = new LoginSocialResponseDTO();
        response.setEmail(data.getEmail());
        response.setRole(user.get().getRole().name());
        if(existingKey != null){
            log.info("Token already exists for user: " + data.getEmail());
            response.setAccessToken(existingKey);
            return response;
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(data.getEmail())
                .claim("scope", user.get().getRole().name())
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        if(authRedisRepository.isKeyBlacklisted(jwt)){
            throw new DataNotFoundException("JWT Token has already been blacklisted");
        }
        authRedisRepository.saveJwtKey(data.getEmail(), jwt);
        response.setAccessToken(jwt);
        return response;
    }

    @Override
    public void logout(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String userEmail = jwt.getSubject();

            // Remove the token from Redis
            authRedisRepository.deleteJwtKey(userEmail);

            // Add the token to a blacklist
            authRedisRepository.blackListJwt(userEmail, token);

            log.info("User logged out: " + userEmail);
        } catch (JwtException e) {
            log.warning("Invalid token during logout: " + e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }
}
