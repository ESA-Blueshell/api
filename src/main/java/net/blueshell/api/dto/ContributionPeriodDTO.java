package net.blueshell.api.dto;

import lombok.Data;
import net.blueshell.api.base.DTO;

import java.time.LocalDate;

@Data
public class ContributionPeriodDTO extends DTO {
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private double halfYearFee;

    private double fullYearFee;

    private double alumniFee;

    private Long listId;
}
