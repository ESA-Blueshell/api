package net.blueshell.api.controller.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.blueshell.dto.BaseDTO;
import net.blueshell.api.common.enums.ResetType;
import net.blueshell.api.validation.user.ValidActivationRequest;

@EqualsAndHashCode(callSuper = true)
@Data
@ValidActivationRequest
public class ActivationRequest extends BaseDTO {

    @NotBlank
    private String token;

    @NotBlank
    private ResetType resetType;

    private String username;

    private String password;
}
