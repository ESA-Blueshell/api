package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
public class GuestDTO extends DTO {

    private long id;

    private String name;

    private String discord;

    private String email;

    private Timestamp createdAt;

    private String accessToken;
}
