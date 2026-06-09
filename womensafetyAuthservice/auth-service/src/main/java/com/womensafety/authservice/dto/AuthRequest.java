package com.womensafety.authservice.dto;

import com.womensafety.authservice.common.OnCreateGroupValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Email must not be blank",
            groups = OnCreateGroupValidator.class)
    @Email(message = "Invalid email format",groups = OnCreateGroupValidator.class)
    private String email;

    @NotBlank(message = "Password must not be blank",
            groups = OnCreateGroupValidator.class)
    @Size(min = 6, max = 20 ,message = "Password must be between 6 and 20 characters long",groups = OnCreateGroupValidator.class)
    private String password;

}
