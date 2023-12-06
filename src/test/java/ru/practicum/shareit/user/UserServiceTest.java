package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.ValidateService;
import ru.practicum.shareit.common.exeptions.ConflictDataException;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ValidateService validateService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @InjectMocks
    private UserService userService;

    @Test
    void create_whenUserValid_thenSavedUser() {
        User userToSave = new User();
        UserDto expectedUserDto = new UserDto();
        when(userRepository.save(userToSave)).thenReturn(userToSave);
        when(userMapper.toUserDto(userToSave)).thenReturn(expectedUserDto);
        when(userMapper.toUser(expectedUserDto)).thenReturn(userToSave);

        UserDto actualUserDto = userService.create(expectedUserDto);

        verify(userRepository, times(1)).save(userToSave);
        verify(userMapper, times(1)).toUserDto(userToSave);
        verify(userMapper, times(1)).toUser(expectedUserDto);
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void create_avoidTestErrors() {
        User userToSave = new User(2L, "name", "user@mail.ru");
        UserDto expectedUserDto = new UserDto(2L, "name", "user@mail.ru");
        when(userRepository.save(userToSave)).thenReturn(userToSave);
        when(userRepository.findAllByEmail(userToSave.getEmail())).thenReturn(List.of(userToSave, userToSave));
        when(userMapper.toUser(expectedUserDto)).thenReturn(userToSave);

        assertThrows(ConflictDataException.class,
                () -> userService.create(expectedUserDto));
    }

    @Test
    void create_whenUserNotValid_thenNotSavedUser() {
        User userToSave = new User();
        UserDto expectedUserDto = new UserDto();
        doThrow(ValidationException.class)
                .when(validateService).checkNameForValid(expectedUserDto);

        assertThrows(ValidationException.class,
                () -> userService.create(expectedUserDto));

        verify(userRepository, never()).save(userToSave);
    }

    @Test
    void update_whenUserFound_thenUpdatedAvailableFields() {
        Long userId = 0L;
        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("old");
        oldUser.setEmail("mail@mail.ru");

        UserDto oldUserDto = new UserDto();
        oldUserDto.setName("old");
        oldUserDto.setEmail("mail@mail.ru");

        UserDto newUserDto = new UserDto();
        newUserDto.setName("new");
        newUserDto.setEmail("mail@mail.ru");

        when(userRepository.findByEmail(newUserDto.getEmail())).thenReturn(Optional.of(oldUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        UserDto actualUserDto = userService.update(userId, newUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("new", savedUser.getName());
        assertEquals("mail@mail.ru", savedUser.getEmail());

    }

    @Test
    void update_whenUsersNameAndEmailIsEmpty_thenUpdatedUser() {
        Long userId = 0L;
        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("old");
        oldUser.setEmail("mail@mail.ru");

        UserDto oldUserDto = new UserDto();
        oldUserDto.setName("old");
        oldUserDto.setEmail("mail@mail.ru");

        UserDto newUserDto = new UserDto();

        when(userRepository.findByEmail(newUserDto.getEmail())).thenReturn(Optional.of(oldUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        UserDto actualUserDto = userService.update(userId, newUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals("old", savedUser.getName());
        assertEquals("mail@mail.ru", savedUser.getEmail());
    }

    @Test
    void update_whenUserNotFound_thenReturnException() {
        Long userId = 0L;
        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("old");
        oldUser.setEmail("mail@mail.ru");

        UserDto newUserDto = new UserDto();
        newUserDto.setName("new");
        newUserDto.setEmail("mail@mail.ru");

        when(userRepository.findByEmail(newUserDto.getEmail())).thenReturn(Optional.of(oldUser));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.update(userId, newUserDto));

        verify(userRepository, never()).save(oldUser);
    }

    @Test
    void update_whenUserByEmailNotFound_thenReturnException() {
        Long userId = 0L;
        User oldUser = new User(userId,"old", "mail@mail.ru");
        User otherUser = new User(1L,"other", "mail@mail.ru");
        UserDto newUserDto = new UserDto(null, "new", "mail@mail.ru");

        when(userRepository.findByEmail(newUserDto.getEmail())).thenReturn(Optional.of(otherUser));

        assertThrows(ConflictDataException.class,
                () -> userService.update(userId, newUserDto));

        verify(userRepository, never()).save(oldUser);
    }

    @Test
    void update_whenUserIdNotValid_thenExceptionThrown() {
        String email = "mail@mail.ru";
        Long oldUserId = 0L;
        Long newUserId = 1L;
        User oldUser = new User();
        oldUser.setId(oldUserId);
        oldUser.setName("old");
        oldUser.setEmail(email);

        User newUser = new User();
        newUser.setId(newUserId);
        newUser.setName("new");
        newUser.setEmail(email);

        UserDto newUserDto = new UserDto();
        newUserDto.setName("new");
        newUserDto.setEmail(email);

        when(userRepository.findByEmail(newUserDto.getEmail())).thenReturn(Optional.of(oldUser));

        assertThrows(ConflictDataException.class, () -> userService.update(newUserId, newUserDto));
        verify(userRepository, times(0)).save(newUser);

    }

    @Test
    void delete() {
        Long id = 0L;

        userService.delete(id);

        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void getById_whenUserFound_thenReturnedUser() {
        Long userId = 1L;
        User user = new User();
        UserDto expectedUserDto = new UserDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.getById(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toUserDto(user);
        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void getById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getAll_whenUsersFound_thenReturnedUsers() {
        Long userId1 = 0L;
        Long userId2 = 1L;
        String name1 = "one";
        String name2 = "two";

        User user1 = new User(userId1, name1, name1 + "@mail.ru");
        User user2 = new User(userId2, name2, name2 + "@mail.ru");

        UserDto userDto1 = new UserDto(userId1, name1, name1 + "@mail.ru");
        UserDto userDto2 = new UserDto(userId2, name2, name2 + "@mail.ru");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(userMapper.toUserDto(user1)).thenReturn(userDto1);
        when(userMapper.toUserDto(user2)).thenReturn(userDto2);

        List<UserDto> actualList = (List<UserDto>) userService.getAll();

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toUserDto(user1);
        verify(userMapper, times(1)).toUserDto(user2);
        assertEquals(2, actualList.size());
        assertEquals(userId1, actualList.get(0).getId());
        assertEquals(name1, actualList.get(0).getName());
        assertEquals(name1 + "@mail.ru", actualList.get(0).getEmail());
        assertEquals(userId2, actualList.get(1).getId());
        assertEquals(name2, actualList.get(1).getName());
        assertEquals(name2 + "@mail.ru", actualList.get(1).getEmail());
    }
}