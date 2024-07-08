package com.example.modelmapper.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserMapperServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserMapperService userMapperService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        userDto = new UserDto();
        userDto.setUsername("testUserDto");
        userDto.setEmail("testDto@example.com");
    }

    @Test
    void testConvertToDto() {
        when(modelMapper.map(any(User.class), any(Class.class))).thenReturn(userDto);

        UserDto dto = userMapperService.convertToDto(user);
        assertEquals(userDto.getUsername(), dto.getUsername());
        assertEquals(userDto.getEmail(), dto.getEmail());
    }

    @Test
    void testConvertToEntity() {
        when(modelMapper.map(any(UserDto.class), any(Class.class))).thenReturn(user);

        User entity = userMapperService.convertToEntity(userDto);
        assertEquals(user.getUsername(), entity.getUsername());
        assertEquals(user.getEmail(), entity.getEmail());
    }
}