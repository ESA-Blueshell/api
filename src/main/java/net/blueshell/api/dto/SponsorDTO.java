package net.blueshell.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.blueshell.api.base.DTO;

@Data
public class SponsorDTO extends DTO {

    private long id;

    @NotBlank(message = "Sponsor name cannot be blank.")
    private String name;

    @NotBlank(message = "Sponsor description cannot be blank.")
    private String description;


}
