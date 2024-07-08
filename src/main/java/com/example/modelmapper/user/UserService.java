package com.example.modelmapper.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public record UserService(UserMapperService userMapperService) {

    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = this.getUserList();

        List<UserDto> userDtoList = users.stream()
                .map(userMapperService::convertToDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    public List<User> getUserList() {
        ArrayList<User> users = new ArrayList<>();
        users.add(User.builder()
                .id(UUID.randomUUID())
                .username("john-doe")
                .email("john@test.com")
                .build()
        );
        users.add(User.builder()
                .id(UUID.randomUUID())
                .username("jane-doe")
                .email("jane@test.com")
                .build()
        );

        return users;
    }

    public ResponseEntity<UserDto> getUserByUsername(String username) {
        Optional<User> user = this.findUsernameInUserList(username);
        if (user.isEmpty()) {
            // throw new UserNotFoundException(username);
            // Not part of this example
        }
        return new ResponseEntity<>(
                userMapperService.convertToDto(user.get()),
                HttpStatus.OK
        );
    }

    private Optional<User> findUsernameInUserList(String username) {
        List<User> allUsers = this.getUserList();
        return allUsers.stream()
                .filter(user -> username.equals(user.getUsername()))
                .findAny();
    }

    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        User newUser = userMapperService.convertToEntity(userDto);

        Optional<User> user = this.findUsernameInUserList(userDto.getUsername());
        if (user.isPresent()) {
            // throw new UserAlreadyExistsException(userDto.username());
            // Not part of this example
        }
        // Save the user to the database
        // Not part of this example
        return new ResponseEntity<>(
                userMapperService.convertToDto(newUser),
                HttpStatus.CREATED
        );
    }
}