package ru.practicum.ewm.validation;

import java.time.LocalDateTime;

public interface Timespan {
    LocalDateTime getRangeStart();

    LocalDateTime getRangeEnd();
}
