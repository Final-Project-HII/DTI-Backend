package com.hii.finalProject.users.controller;

import com.hii.finalProject.response.Response;
import com.hii.finalProject.users.dto.CheckVerificationLinkDTO;
import com.hii.finalProject.users.dto.ManagePasswordDTO;
import com.hii.finalProject.users.dto.UserDTO;

import com.hii.finalProject.users.dto.UserRegisterRequestDTO;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<Response<User>> register(@Valid @RequestBody UserRegisterRequestDTO userRegisterRequestDto) {
        return Response.successfulResponse("User registered successfully", userService.register(userRegisterRequestDto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<Object>> resetPassword(@RequestParam String email){

        return Response.successfulResponse("Reset password link has been sent succesfully");
    }

    @PostMapping("/set-password")
    public ResponseEntity<Response<User>> managePassword(@RequestBody ManagePasswordDTO data){
        return Response.successfulResponse("User has been verified", userService.confirmVerification(data));
    }

    @PostMapping("/new-verification-link")
    public ResponseEntity<Response<Boolean>> sendNewVerificationLink(@RequestParam String email){
        return Response.successfulResponse("Verification link has been sent", userService.newVerificationLink(email));
    }

    @PostMapping("/check-verification")
    public ResponseEntity<Response<String>> isVerifiedLinkValid(@RequestBody CheckVerificationLinkDTO data){
        return Response.successfulResponse("Verification link status has been fetched", userService.checkVerificationLink(data));
    }
    @GetMapping("/")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
    return Collections.singletonMap("name", principal.getAttribute("name"));
    }
}