package net.blueshell.api.dto;

import lombok.Data;
import net.blueshell.api.base.DTO;

@Data
public class ContributionDTO extends DTO {
    private Long id;

    private Long userId;

    private Long contributionPeriodId;
}
