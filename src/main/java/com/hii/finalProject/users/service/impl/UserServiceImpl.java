package com.hii.finalProject.users.service.impl;


import com.hii.finalProject.auth.repository.AuthRedisRepository;
import com.hii.finalProject.cloudinary.CloudinaryService;
import com.hii.finalProject.email.service.EmailService;
import com.hii.finalProject.exceptions.DataNotFoundException;
import com.hii.finalProject.users.dto.*;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.users.repository.UserRepository;
import com.hii.finalProject.users.service.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final AuthRedisRepository authRedisRepository;

    private final CloudinaryService cloudinaryService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, AuthRedisRepository authRedisRepository, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authRedisRepository = authRedisRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public Long getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new DataNotFoundException("User not found with email: " + email));
    }


    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public Optional<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    User updatedUser = convertToEntity(userDTO);
                    updatedUser.setId(existingUser.getId());
                    return convertToDTO(userRepository.save(updatedUser));
                });
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    @Transactional
    @Override
    public UserRegisterResponseDTO register(UserRegisterRequestDTO user) {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        if(userData.isPresent()){
            throw new DataNotFoundException("Email has already been registered");
        }

        User newUser = user.toEntity();
        userRepository.save(newUser);
        UserRegisterResponseDTO userRegisterResponseDTO = new UserRegisterResponseDTO();
        userRegisterResponseDTO.setName(newUser.getName());
        userRegisterResponseDTO.setImageUrl(newUser.getProfilePicture());
        userRegisterResponseDTO.setIsVerified(newUser.getIsVerified());
        userRegisterResponseDTO.setEmail(newUser.getEmail());
        userRegisterResponseDTO.setRole(newUser.getRole());
        String tokenValue = UUID.randomUUID().toString();
        authRedisRepository.saveVerificationLink(user.getEmail(),tokenValue);
        String htmlBody = "<html>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px;'>" +

                "<div style='text-align: center; background-color: #FABC3F; padding: 20px;'>" +
                "<img src='https://res.cloudinary.com/dv9bbdl6i/image/upload/v1724211850/HiiMart/hiimartV1_rgwy5e.png' alt='Your Site' style='width: 50px;'>" +
                "</div>" +

                "<div style='text-align: center; padding: 40px 20px;'>" +
                "<h1 style='color: #E85C0D;'>Thanks for Signing Up!</h1>" +
                "<h2 style='color: #E85C0D;'>Verify Your E-mail Address</h2>" +
                "<p style='color: #333;'>Hi,<br>You're almost ready to get started. Please click on the button below to verify your email address</p>" +
                "<a href='http://localhost:3000/manage-password?token=" + tokenValue +"&email=" + user.getEmail() + "' style='text-decoration: none;'>" +
                "<button style='background-color: #C7253E; color: white; padding: 15px 30px; border: none; border-radius: 5px; font-size: 16px; cursor: pointer;'>VERIFY YOUR EMAIL</button>" +
                "</a>" +
                "</div>" +

                "<div style='background-color: #e0e0e0; padding: 20px; text-align: center;'>" +
                "<p style='color: #333;'>Get in touch:<br>+62 815 8608 1551<br>HiiMart@gmail.com</p>" +
                "<p style='color: #999;'>Copyrights © Company All Rights Reserved</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(user.getEmail(), "Complete Registration for Hii Mart!", htmlBody);
        return userRegisterResponseDTO;
    }

    @Override
    public UserRegisterResponseDTO registerAdmin(AdminRegisterRequestDTO user) {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        if(userData.isPresent()){
            throw new DataNotFoundException("Email has already been registered");
        }
        User newUser = user.toEntity();
        userRepository.save(newUser);
        UserRegisterResponseDTO userRegisterResponseDTO = new UserRegisterResponseDTO();
        userRegisterResponseDTO.setName(newUser.getName());
        userRegisterResponseDTO.setImageUrl(newUser.getProfilePicture());
        userRegisterResponseDTO.setIsVerified(newUser.getIsVerified());
        userRegisterResponseDTO.setEmail(newUser.getEmail());
        userRegisterResponseDTO.setRole(newUser.getRole());
        String tokenValue = UUID.randomUUID().toString();
        authRedisRepository.saveVerificationLink(user.getEmail(),tokenValue);
        String htmlBody = "<html>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px;'>" +

                "<div style='text-align: center; background-color: #FABC3F; padding: 20px;'>" +
                "<img src='https://res.cloudinary.com/dv9bbdl6i/image/upload/v1724211850/HiiMart/hiimartV1_rgwy5e.png' alt='Your Site' style='width: 50px;'>" +
                "</div>" +

                "<div style='text-align: center; padding: 40px 20px;'>" +
                "<h1 style='color: #E85C0D;'>Thanks for Signing Up!</h1>" +
                "<h2 style='color: #E85C0D;'>Verify Your E-mail Address</h2>" +
                "<p style='color: #333;'>Hi,<br>You're almost ready to get started. Please click on the button below to verify your email address</p>" +
                "<a href='http://localhost:3000/manage-password?token=" + tokenValue +"&email=" + user.getEmail() + "' style='text-decoration: none;'>" +
                "<button style='background-color: #C7253E; color: white; padding: 15px 30px; border: none; border-radius: 5px; font-size: 16px; cursor: pointer;'>VERIFY YOUR EMAIL</button>" +
                "</a>" +
                "</div>" +

                "<div style='background-color: #e0e0e0; padding: 20px; text-align: center;'>" +
                "<p style='color: #333;'>Get in touch:<br>+62 815 8608 1551<br>HiiMart@gmail.com</p>" +
                "<p style='color: #999;'>Copyrights © Company All Rights Reserved</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(user.getEmail(), "Complete Registration for Hii Mart!", htmlBody);
        return userRegisterResponseDTO;
    }

    @Override
    public User registerSocial(UserRegisterSocialRequestDTO user) {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        if (userData.isPresent()) {
            User existingUser = userData.get();
            if (existingUser.getPassword() != null && existingUser.getIsVerified()) {
                throw new DataNotFoundException("Email has already been registered");
            }
            if(existingUser.getPassword() == null && !existingUser.getIsVerified())
            {
                throw new DataNotFoundException("Email has already been registered");
            }else{
                return existingUser;
            }
        }
        User newUser = user.toEntity();
        newUser.setIsVerified(true);
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public User setPassword(ManagePasswordDTO data) {
        User user = userRepository.findByEmail(data.getEmail()).orElseThrow(() -> new DataNotFoundException("Email not found"));
        if(!data.getConfirmPassword().equals(data.getPassword())){
            throw new InputMismatchException("Password doesn't match");
        }
        if(!user.getIsVerified()){
            user.setIsVerified(true);
        }
        user.setUpdatedAt(Instant.now());
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public String checkVerificationLink(CheckVerificationLinkDTO data) {
        Optional<User> user = userRepository.findByEmail(data.getEmail());
        var existingToken = authRedisRepository.getVerificationLink(data.getEmail());
        if(user.get().getIsVerified()){
            return "Verified";
        }
        if(!user.get().getIsVerified() && authRedisRepository.isVerificationLinkValid(data.getEmail()) && existingToken.equals(data.getToken())){
            return "Not Verified";
        }
        return "Expired";
    }

    @Override
    public void newVerificationLink(String email) {
        if(authRedisRepository.isVerificationLinkValid(email)){
            authRedisRepository.deleteVerificationLink(email);
        }
        String tokenValue = UUID.randomUUID().toString();
        authRedisRepository.saveVerificationLink(email,tokenValue);
        String htmlBody = "<html>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px;'>" +

                "<div style='text-align: center; background-color: #FABC3F; padding: 20px;'>" +
                "<img src='https://res.cloudinary.com/dv9bbdl6i/image/upload/v1724211850/HiiMart/hiimartV1_rgwy5e.png' alt='Your Site' style='width: 50px;'>" +
                "</div>" +

                "<div style='text-align: center; padding: 40px 20px;'>" +
                "<h1 style='color: #E85C0D;'>Thanks for Signing Up!</h1>" +
                "<h2 style='color: #E85C0D;'>Verify Your E-mail Address</h2>" +
                "<p style='color: #333;'>Hi,<br>You're almost ready to get started. Please click on the button below to verify your email address</p>" +
                "<a href='http://localhost:3000/manage-password?token=" + tokenValue +"&email=" + email + "' style='text-decoration: none;'>" +
                "<button style='background-color: #C7253E; color: white; padding: 15px 30px; border: none; border-radius: 5px; font-size: 16px; cursor: pointer;'>VERIFY YOUR EMAIL</button>" +
                "</a>" +
                "</div>" +

                "<div style='background-color: #e0e0e0; padding: 20px; text-align: center;'>" +
                "<p style='color: #333;'>Get in touch:<br>+62 815 8608 1551<br>HiiMart@gmail.com</p>" +
                "<p style='color: #999;'>Copyrights © Company All Rights Reserved</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(email, "Complete Registration for Hii Mart!", htmlBody);
    }

    @Override
    public void newResetPasswordLink(String email) {
        if(authRedisRepository.isResetPasswordLinkValid(email)){
            authRedisRepository.deleteResetPasswordLink(email);
        }
        String tokenValue = UUID.randomUUID().toString();
        authRedisRepository.saveResetPasswordLink(email,tokenValue);
        String htmlBody = "<html>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px;'>" +

                "<div style='text-align: center; background-color: #FABC3F; padding: 20px;'>" +
                "<img src='https://res.cloudinary.com/dv9bbdl6i/image/upload/v1724211850/HiiMart/hiimartV1_rgwy5e.png' alt='Your Site' style='width: 50px;'>" +
                "</div>" +

                "<div style='text-align: center; padding: 40px 20px;'>" +
                "<h1 style='color: #E85C0D;'>Reset Password!</h1>" +
                "<p style='color: #333;'>Hi,<br>We received a request to reset your password. Click the button below to reset it:</p>" +
                "<a href='http://localhost:3000/reset-password-confirmation?token=" + tokenValue +"&email=" + email + "' style='text-decoration: none;'>" +
                "<button style='background-color: #C7253E; color: white; padding: 15px 30px; border: none; border-radius: 5px; font-size: 16px; cursor: pointer;'>RESET PASSWORD</button>" +
                "</a>" +
                "</div>" +

                "<div style='background-color: #e0e0e0; padding: 20px; text-align: center;'>" +
                "<p style='color: #333;'>Get in touch:<br>+62 815 8608 1551<br>HiiMart@gmail.com</p>" +
                "<p style='color: #999;'>Copyrights © Company All Rights Reserved</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(email, "Reset password for Hii Mart account!", htmlBody);
    }

    @Override
    public String sendResetPasswordLink(String email) {
        Optional<User> userData = userRepository.findByEmail(email);
        if(!userData.get().getIsVerified()){
            return "Not Verified";
        }
        if(userData.isEmpty() || userData.get().getPassword() == null){
            return "Not Registered";
        }
        if (authRedisRepository.isResetPasswordLinkValid(email)) {
            authRedisRepository.deleteResetPasswordLink(email);
        }
        String tokenValue = UUID.randomUUID().toString();
        authRedisRepository.saveResetPasswordLink(email,tokenValue);
        String htmlBody = "<html>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px;'>" +

                "<div style='text-align: center; background-color: #FABC3F; padding: 20px;'>" +
                "<img src='https://res.cloudinary.com/dv9bbdl6i/image/upload/v1724211850/HiiMart/hiimartV1_rgwy5e.png' alt='Your Site' style='width: 50px;'>" +
                "</div>" +

                "<div style='text-align: center; padding: 40px 20px;'>" +
                "<h1 style='color: #E85C0D;'>Reset Password!</h1>" +
                "<p style='color: #333;'>Hi,<br>We received a request to reset your password. Click the button below to reset it:</p>" +
                "<a href='http://localhost:3000/reset-password-confirmation?token=" + tokenValue +"&email=" + email + "' style='text-decoration: none;'>" +
                "<button style='background-color: #C7253E; color: white; padding: 15px 30px; border: none; border-radius: 5px; font-size: 16px; cursor: pointer;'>RESET PASSWORD</button>" +
                "</a>" +
                "</div>" +

                "<div style='background-color: #e0e0e0; padding: 20px; text-align: center;'>" +
                "<p style='color: #333;'>Get in touch:<br>+62 815 8608 1551<br>HiiMart@gmail.com</p>" +
                "<p style='color: #999;'>Copyrights © Company All Rights Reserved</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        emailService.sendEmail(email, "Reset password for Hii Mart account!", htmlBody);
        return "Success";
    }

    @Override
    public Boolean checkResetPasswordLinkIsValid(CheckResetPasswordLinkDTO data) {
        Optional<User> user = userRepository.findByEmail(data.getEmail());
        var existingToken = authRedisRepository.getResetPasswordLink(data.getEmail());
        return user.get().getIsVerified() && authRedisRepository.isResetPasswordLinkValid(data.getEmail()) && existingToken.equals(data.getToken());
    }





    @Override
    @CachePut(value = "getProfileData",key = "#email")
    public ProfileResponseDTO updateProfile(String email, ProfileRequestDTO profileRequestDTO) {
        User userData = userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("User not found"));
        userData.setProfilePicture(cloudinaryService.uploadFile(profileRequestDTO.getAvatar(),"folder_luxtix"));
        userData.setName(profileRequestDTO.getDisplayName());
        userData.setPhoneNumber(profileRequestDTO.getPhoneNumber());
        userRepository.save(userData);
        ProfileResponseDTO data = new ProfileResponseDTO();
        data.setEmail(userData.getEmail());
        data.setDisplayName(userData.getName());
        data.setAvatar(cloudinaryService.generateUrl(userData.getProfilePicture()));
        data.setPhoneNumber(userData.getPhoneNumber());
        data.setDisplayName(userData.getName());
        return data;
    }


    @Override
    @Cacheable(value = "getProfileData",key = "#email")
    public ProfileResponseDTO getProfileData(String email) {
        User userData = userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("User not found"));
        ProfileResponseDTO data = new ProfileResponseDTO();
        data.setEmail(userData.getEmail());
        data.setDisplayName(userData.getName());
        data.setWarehouseId(userData.getWarehouse() != null ? userData.getWarehouse().getId() : null);
        data.setAvatar(cloudinaryService.generateUrl(userData.getProfilePicture()));
        data.setPhoneNumber(userData.getPhoneNumber());
        data.setDisplayName(userData.getName());
        return data;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    private User convertToEntity(UserDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }
}
