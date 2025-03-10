package net.blueshell.api.dto;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = false)
public class ContributionDTO extends DTO {
    private Long id;

    private Long userId;

    private Long contributionPeriodId;

    private Boolean paid;

    private Timestamp remindedAt;
}
