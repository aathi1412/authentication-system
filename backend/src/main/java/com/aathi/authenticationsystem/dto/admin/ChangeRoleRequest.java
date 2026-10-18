package com.aathi.authenticationsystem.dto.admin;

import com.aathi.authenticationsystem.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRoleRequest {
    @NotNull(message = "Role is required")
    private Role role;
}
