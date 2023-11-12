package ru.practicum.shareit;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidateService {

    public void checkNameForValid(UserDto userDto) throws ValidationException {
        if (userDto.getName().isBlank()) throw new ValidationException("Name is empty: \"" + userDto.getName() + "\"");
    }

    public void checkEmailForValid(UserDto userDto) throws ValidationException {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Email is empty: \"" + userDto.getEmail() + "\"");
        }
        String emailRegex = "^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(userDto.getEmail().toLowerCase());
        if (!matcher.matches()) throw new ValidationException("Invalid e-mail format: \"" + userDto.getEmail() + "\"");
    }

    public void checkMatchingIdUsers(Long oneId, Long twoId) {
        if (!Objects.equals(oneId, twoId)) throw new NotFoundException("ID is not equal to owner ID");
    }

    public void checkItemDto(ItemDto itemDto) {
        if (itemDto == null) throw new ValidationException("Item must not be empty");
        if (itemDto.getName() == null || itemDto.getName().isBlank())
            throw new ValidationException("Item Name must not be empty");
        if (itemDto.getAvailable() == null) throw new ValidationException("Available status must not be empty");
        if (itemDto.getDescription() == null) throw new ValidationException("Description must not be empty");
    }

}
