package ru.damirayupov.instaclon.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTTokenSuccessResponse {
    private Boolean success;
    private String token;
}
