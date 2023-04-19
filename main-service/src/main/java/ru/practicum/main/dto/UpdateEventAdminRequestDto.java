package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.main.model.event.EventLocation;

@Data
@AllArgsConstructor
public class UpdateEventAdminRequestDto {

    private String title;
    private String annotation;
    private CategoryDto category;
    private String description;
    private String eventDate;
    private EventLocation location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
}
