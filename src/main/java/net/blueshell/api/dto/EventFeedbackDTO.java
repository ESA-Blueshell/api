package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.common.dto.BaseDTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class EventFeedbackDTO extends BaseDTO {
    private Long id;
    private String feedback;
    private long eventId;
}
