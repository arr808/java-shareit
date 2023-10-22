package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDto userDto;
    private UserDto updateUserDto;
    private final long userId = 1;
    private final String newName = "new name";
    private final String newEmail = "new_email@email.ru";

    @BeforeEach
    public void createEntity() {
        user = User.builder()
                .id(userId)
                .name("test")
                .email("test@email.ru")
                .build();

        userDto = UserDto.builder()
                .id(userId)
                .name("test")
                .email("test@email.ru")
                .build();

        updateUserDto = UserDto.builder()
                .id(userId)
                .name("test")
                .email("new_email@email.ru")
                .build();
    }

    @Test
    public void shouldReturnUserById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        Assertions.assertEquals(userDto, userService.getById(userId));
    }

    @Test
    public void shouldThrowExceptionWhenGetUnknownUserById() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.getById(99));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldReturnUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        Assertions.assertEquals(List.of(userDto), userService.getAll());
    }

    @Test
    public void shouldReturnEmptyList() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        Assertions.assertEquals(0, userService.getAll().size());
    }

    @Test
    public void shouldAddNewUser() {
        UserDto noIdUserDto = UserDto.builder()
                .name("test")
                .email("test@email.ru")
                .build();

        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        Assertions.assertEquals(userDto, userService.add(noIdUserDto));
    }

    @Test
    public void shouldThrowValidationExceptionWhenAddNoNameUser() {
        userDto.setName("");

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> userService.add(userDto));

        Assertions.assertEquals("name", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldThrowValidationExceptionWhenAddNoEmailUser() {
        userDto.setEmail("");

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> userService.add(userDto));

        Assertions.assertEquals("email", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldUpdateUserName() {
        updateUserDto.setName(newName);
        user.setName(newName);
        userDto.setName(newName);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        Assertions.assertEquals(userDto, userService.update(userId, updateUserDto));
    }

    @Test
    public void shouldUpdateUserEmail() {
        updateUserDto.setEmail(newEmail);
        user.setEmail(newEmail);
        userDto.setEmail(newEmail);
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);
        Assertions.assertEquals(userDto, userService.update(userId, updateUserDto));
    }

    @Test
    public void shouldUpdateUserNameAndEmail() {
        updateUserDto.setName(newName);
        updateUserDto.setEmail(newEmail);

        user.setName(newName);
        user.setEmail(newEmail);

        userDto.setName(newName);
        userDto.setEmail(newEmail);

        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);
        Assertions.assertEquals(userDto, userService.update(userId, updateUserDto));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.update(2, userDto));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldDeleteUserById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        userService.deleteById(userId);

        verify(itemRepository, Mockito.times(1))
                .findAll();
        verify(itemRepository, Mockito.never())
                .deleteById(Mockito.any());
        verify(userRepository, Mockito.times(1))
                .deleteById(userId);
    }

    @Test
    public void shouldThrowExceptionWhenDeleteUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> userService.deleteById(99));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldDeleteAllUsers() {
        userService.deleteAll();

        verify(userRepository, Mockito.times(1))
                .deleteAll();
        verify(itemRepository, Mockito.times(1))
                .deleteAll();
    }
}