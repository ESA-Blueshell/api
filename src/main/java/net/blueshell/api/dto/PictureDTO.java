package net.blueshell.api.dto;

import lombok.Data;
import net.blueshell.api.base.DTO;

@Data
public class PictureDTO extends DTO {
    private long id;
    private String name;
    private String url;
    private long uploaderId;
    private long eventId;
}
