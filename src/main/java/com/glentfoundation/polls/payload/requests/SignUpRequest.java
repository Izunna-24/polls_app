package com.glentfoundation.polls.payload.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpRequest {
    @NotBlank
    @Size(min = 4, max = 40)
    private String name;


    @NotBlank
    @Size(max = 20)
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 6, max = 16)
    private String password;
}
