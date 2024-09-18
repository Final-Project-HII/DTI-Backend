package com.hii.finalProject.users.dto;

import com.hii.finalProject.users.entity.Role;
import com.hii.finalProject.users.entity.User;
import com.hii.finalProject.warehouse.entity.Warehouse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class AdminRegisterRequestDTO {
    @NotBlank(message = "Name is required")
    @NotNull
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    @NotNull
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @Positive
    private Long warehouseId;

    public User toEntity() {
        User user = new User();
        user.setRole(role);
        user.setEmail(email);
        user.setName(name);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        user.setWarehouse(warehouse);
        return user;
    }
}
