package ru.damirayupov.instaclon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.damirayupov.instaclon.payload.request.LoginRequest;
import ru.damirayupov.instaclon.payload.request.SignUpRequest;
import ru.damirayupov.instaclon.payload.response.JWTTokenSuccessResponse;
import ru.damirayupov.instaclon.payload.response.MessageResponse;
import ru.damirayupov.instaclon.security.JWTTokenProvider;
import ru.damirayupov.instaclon.security.SecurityConstants;
import ru.damirayupov.instaclon.services.UserService;
import ru.damirayupov.instaclon.validations.ResponseErrorValidation;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;



    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = getObjectResponseEntity(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;

         userService.createUser(signUpRequest);
         return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/signIn")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = getObjectResponseEntity(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }

    private ResponseEntity<Object> getObjectResponseEntity(BindingResult bindingResult) {
        return responseErrorValidation.mapValidationService(bindingResult);
    }
}
