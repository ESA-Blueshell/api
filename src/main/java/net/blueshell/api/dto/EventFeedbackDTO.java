package net.blueshell.api.dto;

import lombok.Data;
import net.blueshell.api.base.DTO;

@Data
public class EventFeedbackDTO extends DTO {
    private long id;
    private String feedback;
    private long eventId;
}
