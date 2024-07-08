package com.example.modelmapper.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public record UserMapperService(ModelMapper modelMapper) {

    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}