package net.blueshell.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.common.enums.MemberType;
import net.blueshell.common.dto.BaseDTO;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class MembershipDTO extends BaseDTO {

    private Long id;

    @NotBlank
    private Long userId;

    private MemberType memberType;

    private Date date;

    private String city;

    private String country;

    private FileDTO signature;

    private Date startDate;

    private Date endDate;
}
