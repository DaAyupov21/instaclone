package ru.damirayupov.instaclon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.damirayupov.instaclon.models.User;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @NotEmpty
    private String username;
    private String bio;

    public static UserDto from (User user){
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .lastname(user.getLastname())
                .firstname(user.getName())
                .bio(user.getBio())
                .build();
    };

    public static List<UserDto> from (List<User> users) {
        return users.stream().map(UserDto::from).collect(Collectors.toList());
    }
}
