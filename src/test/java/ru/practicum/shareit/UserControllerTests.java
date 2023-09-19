package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserService userService;

    private static final String QUERY = "/users";

    private final UserDto userDto = UserDto.builder()
            .name("User")
            .email("user@test.ru")
            .build();

    private final User user = User.builder()
            .id(1)
            .name("User")
            .email("user@test.ru")
            .build();

    @AfterEach
    public void clear() throws Exception {
        userService.deleteAll();
    }

    @Test
    public void shouldAddAndReturnUser() throws Exception {
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));

        List<UserDto> users = new ArrayList<>();
        users.add(UserMapper.getDto(user));

        mockMvc.perform(get(QUERY))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));

        mockMvc.perform(get(QUERY + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));
    }

    @Test
    public void shouldNotReturnUnknownUser() throws Exception {
        mockMvc.perform(get(QUERY + "/999"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldNotAddUserWithEmptyName() throws Exception {
        userDto.setName(null);
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddUserWithEmptyEmail() throws Exception {
        userDto.setEmail(null);
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddUserWithWrongEmail() throws Exception {
        userDto.setEmail("not email");
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddUserWithDuplicateEmail() throws Exception {
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));

        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    public void shouldUpdateName() throws Exception {
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));

        UserDto updateDto = UserDto.builder()
                .name("update name")
                .build();
        user.setName("update name");

        mockMvc.perform(patch(QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));
    }

    @Test
    public void shouldUpdateEmail() throws Exception {
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));

        UserDto updateDto = UserDto.builder()
                .email("update@test.ru")
                .build();
        user.setEmail("update@test.ru");

        mockMvc.perform(patch(QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));
    }

    @Test
    public void shouldUpdateNameAndEmail() throws Exception {
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));

        UserDto updateDto = UserDto.builder()
                .name("update")
                .email("update@test.ru")
                .build();
        user.setName("update");
        user.setEmail("update@test.ru");

        mockMvc.perform(patch(QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));
    }

    @Test
    public void shouldNotUpdateWithWrongEmail() throws Exception {
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));

        UserDto updateDto = UserDto.builder()
                .email("wrong email")
                .build();

        mockMvc.perform(patch(QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotUpdateWithDuplicateEmail() throws Exception {
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));

        UserDto secondUserDto = UserDto.builder()
                .name("User")
                .email("secondUser@test.ru")
                .build();

        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(secondUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        UserDto updateDto = UserDto.builder()
                        .email("user@test.ru")
                        .build();

        mockMvc.perform(patch(QUERY + "/2")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    public void shouldNotUpdateUnknownUser() throws Exception {
        mockMvc.perform(patch(QUERY + "/999")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldDeleteUserById() throws Exception {
        mockMvc.perform(post(QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(UserMapper.getDto(user))));

        mockMvc.perform(delete(QUERY + "/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(QUERY))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<UserDto>())));
    }

    @Test
    public void shouldNotDeleteUnknownUserById() throws Exception {
        mockMvc.perform(delete(QUERY + "/999"))
                .andExpect(status().is(404));
    }
}
