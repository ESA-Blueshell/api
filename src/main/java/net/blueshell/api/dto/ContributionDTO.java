package net.blueshell.api.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.common.dto.BaseDTO;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
public class ContributionDTO extends BaseDTO {
    private Long id;

    private Long userId;

    private Long contributionPeriodId;

    private Boolean paid;

    private Timestamp remindedAt;
}
