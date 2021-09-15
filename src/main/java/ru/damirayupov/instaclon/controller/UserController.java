package ru.damirayupov.instaclon.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.damirayupov.instaclon.dto.UserDto;
import ru.damirayupov.instaclon.models.User;
import ru.damirayupov.instaclon.services.UserService;
import ru.damirayupov.instaclon.validations.ResponseErrorValidation;

import javax.validation.Valid;

import static ru.damirayupov.instaclon.dto.UserDto.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @GetMapping
    ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        UserDto userDto = from(user);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{user_id")
    public ResponseEntity<UserDto> getUserById (@PathVariable("user_id") String userId){
        User user = userService.getUserById(Long.parseLong(userId));
        UserDto userDto = from(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = getObjectResponseEntity(bindingResult);
        if(!ObjectUtils.isEmpty(errors)) return errors;
        User user = userService.updateUser(userDto, principal);

        return ResponseEntity.ok(from(user));
    }

    private ResponseEntity<Object> getObjectResponseEntity(BindingResult bindingResult) {
        return responseErrorValidation.mapValidationService(bindingResult);
    }

}
