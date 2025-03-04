package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class MemberDTO extends DTO {

    private Date date;

    private String city;

    private String country;

    private FileDTO signature;
}
