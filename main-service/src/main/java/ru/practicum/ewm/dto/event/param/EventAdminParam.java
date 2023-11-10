package ru.practicum.ewm.dto.event.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.enums.StateType;
import ru.practicum.ewm.validation.StartBeforeEnd;
import ru.practicum.ewm.validation.Timespan;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@StartBeforeEnd
public class EventAdminParam implements Timespan {
    private List<Long> users;
    private List<StateType> states;
    private List<Long> categories;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent
    private LocalDateTime rangeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

    public EventAdminParam(List<Long> users, List<StateType> states, List<Long> categories, LocalDateTime rangeStart,
                           LocalDateTime rangeEnd) {
        this.users = users;
        this.states = states;
        this.categories = categories;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.from = 0;
        this.size = 10;
    }
}
