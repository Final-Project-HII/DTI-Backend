package com.hii.finalProject.auth.service.impl;


import com.hii.finalProject.auth.dto.LoginResponseDTO;
import com.hii.finalProject.auth.service.AuthService;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.users.service.UserService;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final UserService userService;

    public AuthServiceImpl(JwtEncoder jwtEncoder, UserRepository userRepository, UserService userService) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public LoginResponseDTO generateToken(Authentication authentication) {
//        long userId = userService.getUserByEmail(authentication.getName()).getId();
        Instant expiredDate = Instant.now().minus(90, ChronoUnit.DAYS);
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

//        var existingKey = authRedisRepository.getJwtKey(authentication.getName());
        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(Long.toString(userService.getUserByEmail(authentication.getName()).get().getId()));
        response.setEmail(authentication.getName());
        response.setRole(scope);
//        if(existingKey != null){
//            log.info("Token already exists for user: " + authentication.getName());
//            response.setAccessToken(existingKey);
//            return response;
//        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("id",userService.getUserByEmail(authentication.getName()).get().getId())
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

//        if(authRedisRepository.isKeyBlacklisted(jwt)){
//            throw new InputException("JWT Token has already been blacklisted");
//        }
//        authRedisRepository.saveJwtKey(authentication.getName(),jwt);
        response.setAccessToken(jwt);
        return response;
    }
}
