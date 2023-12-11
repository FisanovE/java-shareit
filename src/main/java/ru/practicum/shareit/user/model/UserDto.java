package ru.practicum.shareit.user.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.common.ValidationMarkers;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Name is not be empty.", groups = {ValidationMarkers.Strategy1.class})
    private String name;

    @NotBlank(message = "Email is not be empty.", groups = {ValidationMarkers.Strategy1.class})
    @Email(groups = {ValidationMarkers.Strategy1.class, ValidationMarkers.Strategy2.class})
    private String email;
}
