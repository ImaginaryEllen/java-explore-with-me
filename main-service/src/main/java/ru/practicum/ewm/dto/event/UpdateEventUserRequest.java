package ru.practicum.ewm.dto.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.enums.ActionUserType;

@Getter
@Setter
public class UpdateEventUserRequest extends UpdateEvent {
    private ActionUserType stateAction;

    public UpdateEventUserRequest(String annotation, Long category, String description, String eventDate,
                                  Location location, Boolean paid, Long participantLimit, Boolean requestModeration,
                                  ActionUserType stateAction, String title) {
        super(annotation, category, description, eventDate, location, paid, participantLimit, requestModeration, title);
        this.stateAction = stateAction;
    }
}
