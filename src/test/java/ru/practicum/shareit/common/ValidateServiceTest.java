package ru.practicum.shareit.common;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ValidateServiceTest {
    ValidateService validateService = new ValidateService();

    @Test
    void checkPageableParameters_WhenNotValidParameterFrom() {
        Integer from = -1;
        Integer size = 1;
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkPageableParameters(from, size);
        });
        assertEquals(thrown.getMessage(),"Parameter \"from\" must not be less than 0");
    }

    @Test
    void checkPageableParameters_WhenNotValidParameterSize() {
        Integer from = 1;
        Integer size = -1;
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkPageableParameters(from, size);
        });
        assertEquals(thrown.getMessage(),"Parameter \"size\" must not be less than 1");
    }

    @Test
    void checkTimeForValid_WhenStartTimeIsNull() {
        LocalDateTime startTime = null;
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(2);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkTimeForValid(startTime, endTime);
        });
        assertEquals(thrown.getMessage(),"Start time must not be equals null");
    }

    @Test
    void checkTimeForValid_WhenEndTimeIsNull() {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endTime = null;
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkTimeForValid(startTime, endTime);
        });
        assertEquals(thrown.getMessage(),"End time must not be equals null");
    }

    @Test
    void checkTimeForValid_WhenStartTimeInPast() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(2);
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(2);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkTimeForValid(startTime, endTime);
        });
        assertEquals(thrown.getMessage(),"Start time must not be in past");
    }

    @Test
    void checkTimeForValid_WhenEndTimeInPast() {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(2);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(2);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkTimeForValid(startTime, endTime);
        });
        assertEquals(thrown.getMessage(),"End time must not be in past");
    }

    @Test
    void checkTimeForValid_WhenEndTimeBeforeStartTime() {
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(2);
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(1);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkTimeForValid(startTime, endTime);
        });
        assertEquals(thrown.getMessage(),"End time must be after Start time");
    }

    @Test
    void checkTimeForValid_WhenEndTimeEqualsStartTime() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        LocalDateTime startTime = now;
        LocalDateTime endTime = now;
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkTimeForValid(startTime, endTime);
        });
        assertEquals(thrown.getMessage(),"End time must not be equals Start time");
    }

    @Test
    void checkNameForValid_WhenNameNotValid() {
        UserDto userDto = new UserDto();
        userDto.setName("");
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkNameForValid(userDto);
        });
        assertEquals(thrown.getMessage(),"Name is empty: \"" + userDto.getName() + "\"");
    }

    @Test
    void checkEmailForValid_WhenEmailIsNull() {
        UserDto userDto = new UserDto();
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkEmailForValid(userDto);
        });
        assertEquals(thrown.getMessage(),"Email is empty: \"" + userDto.getEmail() + "\"");
    }

    @Test
    void checkEmailForValid_WhenEmailNotValid() {
        UserDto userDto = new UserDto();
        userDto.setEmail("email");
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkEmailForValid(userDto);
        });
        assertEquals(thrown.getMessage(),"Invalid e-mail format: \"" + userDto.getEmail() + "\"");
    }

    @Test
    void checkMatchingIdUsers_WhenUsersIdNotEquals() {
        long oneId = 1L;
        long twoId = 2L;
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            validateService.checkMatchingIdUsers(oneId, twoId);
        });
        assertEquals(thrown.getMessage(),"ID: " + oneId + " is not equal to owner ID: " + twoId);
    }

    @Test
    void checkItemDto_WhenItemDtoIsNull() {
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkItemDto(null);
        });
        assertEquals(thrown.getMessage(),"Item must not be empty");
    }

    @Test
    void checkItemDto_WhenNameItemDtoIsNull() {
        ItemDto itemDto = new ItemDto(1L, null, "description", true, null, null, null, null);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkItemDto(itemDto);
        });
        assertEquals(thrown.getMessage(),"Item Name must not be empty");
    }

    @Test
    void checkItemDto_WhenAvailableItemDtoIsNull() {
        ItemDto itemDto = new ItemDto(1L, "name", "description", null, null, null, null, null);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkItemDto(itemDto);
        });
        assertEquals(thrown.getMessage(),"Available status must not be empty");
    }

    @Test
    void checkItemDto_WhenDescriptionItemDtoIsNull() {
        ItemDto itemDto = new ItemDto(1L, "name", null, true, null, null, null, null);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            validateService.checkItemDto(itemDto);
        });
        assertEquals(thrown.getMessage(),"Description must not be empty");
    }
}