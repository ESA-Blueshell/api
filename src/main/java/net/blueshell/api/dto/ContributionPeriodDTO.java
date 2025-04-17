package net.blueshell.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.common.dto.BaseDTO;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
public class ContributionPeriodDTO extends BaseDTO {
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private double halfYearFee;

    private double fullYearFee;

    private double alumniFee;

    private Long listId;
}
