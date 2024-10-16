package com.hii.finalProject.config;


import com.hii.finalProject.auth.service.impl.UserDetailServiceImpl;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log
public class SecurityConfig {

    private final RsaConfigProperties rsaKeyConfigProperties;
    private final UserDetailServiceImpl userDetailsService;

    public SecurityConfig(RsaConfigProperties rsaKeyConfigProperties, UserDetailServiceImpl userDetailsService) {
        this.rsaKeyConfigProperties = rsaKeyConfigProperties;
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public AuthenticationManager authManager() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://hii-mart-production.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","DELETE","PUT","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/**").permitAll();
                    auth.requestMatchers("/api/users/register").permitAll();
                    auth.requestMatchers("/api/users/register-google").permitAll();
                    auth.requestMatchers("/api/users/set-password").permitAll();
                    auth.requestMatchers("/api/users/check-verification").permitAll();
                    auth.requestMatchers("/api/users/new-verification-link").permitAll();
                    auth.requestMatchers("/api/users/reset-password").permitAll();
                    auth.requestMatchers("/api/users/check-reset-password").permitAll();
                    auth.requestMatchers("/api/users/new-reset-password-link").permitAll();
                    auth.requestMatchers("/api/cities/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/category/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/product/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/warehouses/**").permitAll();
                    auth.requestMatchers("/api/payments/midtrans-callback").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> {
                    oauth2.jwt((jwt) -> jwt.decoder(jwtDecoder()));
                    oauth2.bearerTokenResolver((request) -> {
                        Cookie[] cookies = request.getCookies();
                        var authHeader = request.getHeader("Authorization");
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if ("Sid".equals(cookie.getName())) {
                                    return cookie.getValue();
                                }
                            }
                        } else if (authHeader!= null && !authHeader.isEmpty()) {
                            return authHeader.replace("Bearer ", "");
                        }
                        return null;
                    });
                })
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeyConfigProperties.publicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeyConfigProperties.publicKey()).privateKey(rsaKeyConfigProperties.privateKey()).build();

        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
