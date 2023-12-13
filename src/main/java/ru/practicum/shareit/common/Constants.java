package ru.practicum.shareit.common;

import org.springframework.data.domain.Sort;

public class Constants {
    public static final String HEADER_USER_ID = "X-Sharer-User-Id";
    public static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    public static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    public static final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");


}
