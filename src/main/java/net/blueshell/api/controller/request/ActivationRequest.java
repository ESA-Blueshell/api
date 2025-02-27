package net.blueshell.api.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.blueshell.api.base.DTO;
import net.blueshell.api.common.enums.ResetType;
import net.blueshell.api.validation.user.ValidActivationRequest;

@Data
@ValidActivationRequest
public class ActivationRequest extends DTO {

    @NotBlank
    private String token;

    @NotBlank
    private ResetType resetType;

    private String username;

    private String password;
}
