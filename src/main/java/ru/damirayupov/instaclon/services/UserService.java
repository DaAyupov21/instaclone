package ru.damirayupov.instaclon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.damirayupov.instaclon.dto.UserDto;
import ru.damirayupov.instaclon.exceptions.UserExistException;
import ru.damirayupov.instaclon.models.User;
import ru.damirayupov.instaclon.models.enums.ERole;
import ru.damirayupov.instaclon.payload.request.SignUpRequest;
import ru.damirayupov.instaclon.repositories.UserRepository;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User createUser(SignUpRequest userIn){
        User user = User.builder()
                .email(userIn.getEmail())
                .name(userIn.getFirstname())
                .lastname(userIn.getLastname())
                .username(userIn.getUsername())
                .password(passwordEncoder.encode(userIn.getPassword()))
                .roles(Collections.singleton(ERole.USER))
                .build();

        try {
            LOG.info("Saving user {}", userIn.getEmail());
            return userRepository.save(user);
        } catch (Exception e){
            LOG.error("Error during registration. {}", e.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exit");
        }
    }

    public User updateUser(UserDto userDto, Principal principal){
        User user = getUserByPrincipal(principal);
        user.setBio(userDto.getBio());
        user.setLastname(userDto.getLastname());
        user.setName(userDto.getFirstname());

        return userRepository.save(user);
    }

    public User getCurrentUser(Principal principal){
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found " + username));

    }

    public User getUserById(Long userId) {
        return userRepository.getById(userId);
    }




}
