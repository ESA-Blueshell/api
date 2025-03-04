package net.blueshell.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.api.base.DTO;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class SignatureDTO extends DTO {

    @NotBlank
    private String data;

    @NotBlank
    private Date date;

    @NotBlank
    private String city;

    @NotBlank
    private String country;
}
