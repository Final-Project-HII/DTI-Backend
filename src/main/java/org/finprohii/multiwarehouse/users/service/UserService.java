package org.finprohii.multiwarehouse.users.service;

import org.finprohii.multiwarehouse.users.dto.UserDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO saveUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    Optional<UserDTO> getUserById(Long id);
    List<UserDTO> getAllUsers();
}