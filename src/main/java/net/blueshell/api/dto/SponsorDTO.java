package net.blueshell.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.common.dto.BaseDTO;

@Data
@EqualsAndHashCode(callSuper = false)
public class SponsorDTO extends BaseDTO {

    private Long id;

    @NotBlank(message = "Sponsor name cannot be blank.")
    private String name;

    @NotBlank(message = "Sponsor description cannot be blank.")
    private String description;
}
