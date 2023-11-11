package ru.practicum.ewm.dto.event.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.enums.SortType;
import ru.practicum.ewm.validation.StartBeforeEnd;
import ru.practicum.ewm.validation.Timespan;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@StartBeforeEnd
@NoArgsConstructor
public class EventPublicParam implements Timespan {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent
    private LocalDateTime rangeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private SortType sort;
    private Integer from;
    private Integer size;

    public EventPublicParam(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                            LocalDateTime rangeEnd, Boolean onlyAvailable, SortType sort) {
        this.text = text;
        this.categories = categories;
        this.paid = paid;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.onlyAvailable = onlyAvailable;
        this.sort = sort;
        this.from = 0;
        this.size = 10;
    }
}
