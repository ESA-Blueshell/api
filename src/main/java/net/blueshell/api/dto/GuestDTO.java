package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.common.dto.BaseDTO;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
public class GuestDTO extends BaseDTO {

    private Long id;

    private String name;

    private String discord;

    private String email;

    private Timestamp createdAt;

    private String accessToken;
}
