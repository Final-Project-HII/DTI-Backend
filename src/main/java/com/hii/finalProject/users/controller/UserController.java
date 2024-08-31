package com.hii.finalProject.users.controller;

import com.hii.finalProject.auth.service.impl.AuthServiceImpl;
import com.hii.finalProject.response.Response;
import com.hii.finalProject.users.dto.*;

import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthServiceImpl authServiceImpl;

    public UserController(UserService userService, AuthServiceImpl authServiceImpl) {
        this.userService = userService;
        this.authServiceImpl = authServiceImpl;
    }
    @PostMapping("/register")
    public ResponseEntity<Response<User>> register(@Valid @RequestBody UserRegisterRequestDTO userRegisterRequestDto) {
        return Response.successfulResponse("User registered successfully", userService.register(userRegisterRequestDto));
    }

    @PostMapping("/register-google")
    public ResponseEntity<Response<User>> registerSocial(@RequestBody UserRegisterSocialRequestDTO userRegisterSocialRequestDto) {
        return Response.successfulResponse("User registered successfully", userService.registerSocial(userRegisterSocialRequestDto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<String>> resetPassword(@RequestParam String email){
        return Response.successfulResponse("Reset password link status has been fetched successfully",userService.sendResetPasswordLink(email));
    }

    @PostMapping("/set-password")
    public ResponseEntity<Response<User>> managePassword(@RequestBody ManagePasswordDTO data){
        return Response.successfulResponse("User has been verified", userService.setPassword(data));
    }

    @PostMapping("/new-verification-link")
    public ResponseEntity<Response<Object>> sendNewVerificationLink(@RequestParam String email){
        userService.newVerificationLink(email);
        return Response.successfulResponse("Verification link has been sent");
    }

    @PostMapping("/new-reset-password-link")
    public ResponseEntity<Response<Object>> sendNewResetPasswordLink(@RequestParam String email){
        userService.newResetPasswordLink(email);
        return Response.successfulResponse("Reset password link has been sent");
    }

    @PostMapping("/check-verification")
    public ResponseEntity<Response<String>> isVerifiedLinkValid(@RequestBody CheckVerificationLinkDTO data){
        return Response.successfulResponse("Verification link status has been fetched", userService.checkVerificationLink(data));
    }

    @PostMapping("/check-reset-password")
    public ResponseEntity<Response<Boolean>> isResetPasswordLinkValid(@RequestBody CheckResetPasswordLinkDTO data){
        return Response.successfulResponse("Verification link status has been fetched", userService.checkResetPasswordLinkIsValid(data));
    }
    @GetMapping("/")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
    return Collections.singletonMap("name", principal.getAttribute("name"));
    }

//    @PostMapping("/logout")
//    public ResponseEntity<Response<String>> logout(@RequestBody LogoutRequestDTO logoutRequest) {
//        userService.logout(logoutRequest);
//        return Response.successfulResponse("User logged out successfully");
//    }
}