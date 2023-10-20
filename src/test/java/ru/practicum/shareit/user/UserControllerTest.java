package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    private UserDto userDto;
    private final long userId = 1;
    private static final String QUERY = "/users";

    @BeforeEach
    public void createEntity() {
        userDto = UserDto.builder()
                .id(userId)
                .name("test")
                .email("test@email.ru")
                .build();
    }

    @Test
    public void shouldReturnUserById() throws Exception {
        when(userService.getById(userId))
                .thenReturn((userDto));

        mvc.perform(get(QUERY + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));
    }

    @Test
    public void shouldReturnUsers() throws Exception {
        when(userService.getAll())
                .thenReturn((List.of(userDto)));

        mvc.perform(get(QUERY))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto))));
    }

    @Test
    public void shouldAddUser() throws Exception {
        UserDto noIdUserDto = UserDto.builder()
                .name("test")
                .email("test@email.ru")
                .build();

        when(userService.add(noIdUserDto))
                .thenReturn(userDto);

        mvc.perform(post(QUERY)
                        .content(mapper.writeValueAsString(noIdUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));
    }

    @Test
    public void shouldThrowExceptionAddUserWhenWrongEmail() throws Exception {
        UserDto noIdUserDto = UserDto.builder()
                .name("test")
                .email("test.ru")
                .build();

        mvc.perform(post(QUERY)
                        .content(mapper.writeValueAsString(noIdUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        UserDto updateDto = UserDto.builder()
                .id(userId)
                .name("new name")
                .email("test@email.ru")
                .build();
        userDto.setName("new name");

        when(userService.update(userId, updateDto))
                .thenReturn(userDto);

        mvc.perform(patch(QUERY + "/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));
    }

    @Test
    public void shouldThrowExceptionUpdateUserWhenWrongEmail() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("test")
                .email("test.ru")
                .build();

        mvc.perform(patch(QUERY + "/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldDeleteUserById() throws Exception {
        mvc.perform(delete(QUERY + "/1"))
                .andExpect(status().isOk());
    }
}