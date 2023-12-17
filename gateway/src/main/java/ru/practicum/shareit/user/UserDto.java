package ru.practicum.shareit.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.common.ValidationMarkers;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Name is not be empty.", groups = {ValidationMarkers.Create.class})
    private String name;

    @NotBlank(message = "Email is not be empty.", groups = {ValidationMarkers.Create.class})
    @Email(groups = {ValidationMarkers.Create.class, ValidationMarkers.Update.class})
    private String email;
}
