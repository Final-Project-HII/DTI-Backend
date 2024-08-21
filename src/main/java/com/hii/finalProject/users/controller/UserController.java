//package com.hii.finalProject.users.controller;
//
//import com.hii.finalProject.users.dto.UserDTO;
//import com.hii.finalProject.users.service.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/v1/users")
//public class UserController {
//
//    private UserService userService;
//
//    @PostMapping
//    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
//        UserDTO createdUser = userService.saveUser(userDTO);
//        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
//        UserDTO updatedUser = userService.updateUser(id, userDTO);
//        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
//        Optional<UserDTO> userDTO = userService.getUserById(id);
//        return userDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @GetMapping
//    public ResponseEntity<List<UserDTO>> getAllUsers() {
//        List<UserDTO> users = userService.getAllUsers();
//        return new ResponseEntity<>(users, HttpStatus.OK);
//    }
//}