package ru.practicum.shareit.common;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidateService {

    public void checkPageableParameters(Integer from, Integer size) throws ValidationException {
        if (from != null && from < 0) throw new ValidationException("Parameter \"from\" must not be less than 0");
        if (size != null && size < 1) throw new ValidationException("Parameter \"size\" must not be less than 1");
    }

    public void checkTimeForValid(LocalDateTime startTime, LocalDateTime endTime) throws ValidationException {
        if (startTime == null) throw new ValidationException("Start time must not be equals null");
        if (endTime == null) throw new ValidationException("End time must not be equals null");
        if (startTime.isBefore(LocalDateTime.now())) throw new ValidationException("Start time must not be in past");
        if (endTime.isBefore(LocalDateTime.now())) throw new ValidationException("End time must not be in past");
        if (endTime.isBefore(startTime)) throw new ValidationException("End time must be after Start time");
        if (endTime.equals(startTime)) throw new ValidationException("End time must not be equals Start time");
    }

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
        if (!Objects.equals(oneId, twoId))
            throw new NotFoundException("ID: " + oneId + " is not equal to owner ID: " + twoId);
    }

    public void checkItemDto(ItemDto itemDto) {
        if (itemDto == null) throw new ValidationException("Item must not be empty");
        if (itemDto.getName() == null || itemDto.getName().isBlank())
            throw new ValidationException("Item Name must not be empty");
        if (itemDto.getAvailable() == null) throw new ValidationException("Available status must not be empty");
        if (itemDto.getDescription() == null) throw new ValidationException("Description must not be empty");
    }
}
