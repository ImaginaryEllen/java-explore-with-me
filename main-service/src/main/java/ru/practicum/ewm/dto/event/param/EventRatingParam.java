package ru.practicum.ewm.dto.event.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.enums.SortLikeType;
import ru.practicum.ewm.validation.StartBeforeEnd;
import ru.practicum.ewm.validation.Timespan;

import java.time.LocalDateTime;

@Getter
@Setter
@StartBeforeEnd
@NoArgsConstructor
public class EventRatingParam implements Timespan {
    private Long category;
    private Boolean paid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private SortLikeType sort;
    private Integer from;
    private Integer size;

    public EventRatingParam(Long category, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                            Boolean onlyAvailable, SortLikeType sort) {
        this.category = category;
        this.paid = paid;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.onlyAvailable = onlyAvailable;
        this.sort = sort;
        this.from = 0;
        this.size = 10;
    }
}
