package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exeptions.ConflictDataException;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User addedUser = userRepository.save(user);
        return userMapper.toUserDto(addedUser);
    }

    public UserDto update(Long id, UserDto userDto) {
        Optional<User> userWithEmail = userRepository.findByEmail(userDto.getEmail());
        if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(id)) {
            throw new ConflictDataException("Email is already registered: " + userDto.getEmail());
        }
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return userMapper.toUserDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
