package com.hii.finalProject.users.controller;

import com.hii.finalProject.address.entity.Address;
import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.response.Response;
import com.hii.finalProject.users.dto.*;

import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<Response<UserResponseDTO>> register(@Valid @RequestBody UserRegisterRequestDTO userRegisterRequestDto) {
        return Response.successfulResponse("User registered successfully", userService.register(userRegisterRequestDto));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<Response<UserResponseDTO>> registerAdmin(@Valid @RequestBody AdminRegisterRequestDTO adminRegisterRequestDto) {
        return Response.successfulResponse("Admin registered successfully", userService.registerAdmin(adminRegisterRequestDto));
    }

    @PutMapping("/update-admin")
    public ResponseEntity<Response<UserResponseDTO>> updateAdmin(@Valid @RequestBody AdminRegisterRequestDTO adminRegisterRequestDto) {
        return Response.successfulResponse("Admin update successfully", userService.updateAdmin(adminRegisterRequestDto));
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
    @GetMapping("")
    public ResponseEntity<Response<Page<UserResponseDTO>>> getAllUser(@RequestParam(value = "role",required = false) String role, @RequestParam(value = "email",required = false) String email, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return Response.successfulResponse("All user data has been fetched", userService.getAllUser(email,role,page,size));
    }

    @PutMapping("/profile")
    public ResponseEntity<Response<ProfileResponseDTO>> updateProfile(@ModelAttribute ProfileRequestDTO profileRequestDTO) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse("User profile update successfully", userService.updateProfile(email,profileRequestDTO));
    }


    @GetMapping("/profile")
    public ResponseEntity<Response<ProfileResponseDTO>> getProfileData(){
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        return Response.successfulResponse("Profile data has been fetched",userService.getProfileData(email));
    }


    @PutMapping("/toggle-active-user/{id}")
    public ResponseEntity<Response<Object>> toggleActiveUser(@PathVariable Long id) {
        userService.toggleActiveUser(id);
        return Response.successfulResponse("User active status has been changed");
    }
}
