package ru.practicum.shareit.common;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class Constants {
    public static final String headerUserId = "X-Sharer-User-Id";
    public static final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
    public static final Sort sortByIdAsc = Sort.by(Sort.Direction.ASC, "id");


}
