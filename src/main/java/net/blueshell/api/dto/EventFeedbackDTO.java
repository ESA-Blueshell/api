package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class EventFeedbackDTO extends DTO {
    private long id;
    private String feedback;
    private long eventId;
}
