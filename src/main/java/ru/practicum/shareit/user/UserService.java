package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.ValidateService;
import ru.practicum.shareit.exeptions.ConflictDataException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ValidateService validateService;
    private final UserMapper mapper;

    public UserDto create(UserDto userDto) {
        validateService.checkNameForValid(userDto);
        validateService.checkEmailForValid(userDto);
        if (userRepository.existsUserByEmail(userDto.getEmail())) {
            throw new ConflictDataException("Email is already registered: " + userDto.getEmail());
        }
        User user = mapper.toUser(userDto);
        return mapper.toUserDto(userRepository.save(user));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        if (userDto.getName() != null) {
            validateService.checkNameForValid(userDto);
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateService.checkEmailForValid(userDto);
            /*if (userRepository.existsUserByEmail(userDto.getEmail())) {
                throw new ConflictDataException("Email is already registered: " + userDto.getEmail());
            }*/
            user.setEmail(userDto.getEmail());
        }

        return mapper.toUserDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
       /* List<ItemDto> removedItems = itemRepository.findAll();
        for (ItemDto itemDto : removedItems) {
            itemRepository.delete(itemDto.getId());
        }*/
    }

    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

}
