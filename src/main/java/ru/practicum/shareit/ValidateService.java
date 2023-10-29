package ru.practicum.shareit;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidateService {

    public void checkingNameForValid(User user) throws ValidationException {
        if (user.getName().isBlank()) throw new ValidationException("Name is empty: \"" + user.getName() + "\"");
    }

    public void checkingEmailForValid(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email is empty: \"" + user.getEmail() + "\"");
        }
        String emailRegex = "^([a-z0-9_\\.-]+)@([a-z0-9_\\.-]+)\\.([a-z\\.]{2,6})$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(user.getEmail().toLowerCase());
        if (!matcher.matches()) throw new ValidationException("Invalid e-mail format: \"" + user.getEmail() + "\"");
    }

    public void checkMatchingIdUsers(Long oneId, Long twoId) {
        if (!Objects.equals(oneId, twoId)) throw new NotFoundException("ID is not equal to owner ID");
    }

    public void checkItem(Item item) {
        if (item == null) throw new ValidationException("Item must not be empty");
        if (item.getName() == null || item.getName().isBlank())
            throw new ValidationException("Item Name must not be empty");
        if (item.getAvailable() == null) throw new ValidationException("Available status must not be empty");
        if (item.getDescription() == null) throw new ValidationException("Description must not be empty");
    }

}
