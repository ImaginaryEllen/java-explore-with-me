package ru.practicum.ewm.dto.event;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.enums.ActionAdminType;

@Getter
@Setter
public class UpdateEventAdminRequest extends UpdateEvent {
    private ActionAdminType stateAction;

    public UpdateEventAdminRequest(String annotation, Long category, String description, String eventDate,
                                   Location location, Boolean paid, Long participantLimit, Boolean requestModeration,
                                   ActionAdminType stateAction, String title) {
        super(annotation, category, description, eventDate, location, paid, participantLimit, requestModeration, title);
        this.stateAction = stateAction;
    }
}

