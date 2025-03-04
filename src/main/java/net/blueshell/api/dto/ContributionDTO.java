package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class ContributionDTO extends DTO {
    private Long id;

    private Long userId;

    private Long contributionPeriodId;
}
