package ru.damirayupov.instaclon.payload.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest {

    @NotEmpty(message = "Username is not empty")
    private String username;
    @NotEmpty(message = "Password is not empty")
    private String password;
}
