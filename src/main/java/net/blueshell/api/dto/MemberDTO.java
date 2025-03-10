package net.blueshell.api.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class MemberDTO extends DTO {

    private Long id;

    private Date date;

    private String city;

    private String country;

    private FileDTO signature;

    private Date startDate;

    private Date endDate;
}
