package net.blueshell.api.dto;
import lombok.Data;
import net.blueshell.api.model.File;

import net.blueshell.api.base.DTO;

import java.util.Date;

@Data
public class MembershipDTO extends DTO {

    private Date date;

    private String city;

    private String country;

    private FileDTO signature;
}
