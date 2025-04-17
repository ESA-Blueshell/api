package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.common.dto.BaseDTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class PictureDTO extends BaseDTO {
    private Long id;
    private String name;
    private String url;
    private long uploaderId;
    private long eventId;
}
