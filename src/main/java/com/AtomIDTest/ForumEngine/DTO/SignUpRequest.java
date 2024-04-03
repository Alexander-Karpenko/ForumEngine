package com.AtomIDTest.ForumEngine.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @Size(min = 5, max = 50, message = "Username must contain from 5 to 50 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Size(min = 5, max = 255, message = "The email address must contain between 5 and 255 characters")
        @NotBlank(message = "Email address cannot be empty")
    private String email;

    @Size(max = 255, message = "Password length must be no more than 255 characters")
    private String password;
}